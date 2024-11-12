package chapter10;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PortScannerFX extends Application {
    private static AtomicInteger portCount = new AtomicInteger(0);
    //private final String ip = "202.116.195.71";
    private String start;
    private String end;
    private Thread thread;
    private File file = new File("test.txt");

    private Button btnScanner = new Button("扫描");
    private Button btnFast = new Button("快速扫描");
    private Button btnThread = new Button("多线程扫描");
    private Button btnExit = new Button("退出");

    private TextField tfSend = new TextField();
    private TextField address = new TextField();
    private TextField portStart = new TextField("8000");
    private TextField portEnd = new TextField("10000");

    private Button btnReadFile = new Button("读取ip列表");


    private TextArea taDisplay = new TextArea();

    public void start(Stage primaryStage) {
        taDisplay.setWrapText(true);
        taDisplay.setEditable(false);
        portStart.setMaxWidth(70);
        portEnd.setMaxWidth(70);

        btnExit.setOnAction(event -> System.exit(0));
        btnScanner.setOnAction(event -> {
            extracted();
        });
        btnFast.setOnAction(event -> {
            extracted();
        });
        btnThread.setOnAction(event -> {
            portCount.set(0);
            start = portStart.getText().trim();
            end = portEnd.getText().trim();
            for (int i = 0;i < 10;i++){
                thread = new Thread(new ScanHandler(10, i, address.getText().trim()));
                thread.start();
            }

        });
        btnReadFile.setOnAction(event -> {
            try {
                String ip = null;
                InputStreamReader read = new InputStreamReader(new FileInputStream(file),"UTF-8");
                BufferedReader br = new BufferedReader(read);
                List<String> ipList = new ArrayList<>();
                while ((ip = br.readLine()) != null){
                    ipList.add(ip);
                }
                for(String ips:ipList){
                    System.out.println(ips);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
        });
        BorderPane mainPane = new BorderPane();

        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10, 20, 10, 20));

        HBox hBox1 = new HBox();
        hBox1.setSpacing(10);
        hBox1.setPadding(new Insets(10, 20, 10, 20));
        hBox1.setAlignment(Pos.CENTER);
        hBox1.getChildren().addAll(new Label("IP地址："), address,
                new Label("起始端口："), portStart,
                new Label("结束端口："), portEnd, btnScanner);

        vBox.getChildren().addAll(hBox1, new Label("信息显示区："),
                taDisplay, new Label("信息输入区："), tfSend);

        VBox.setVgrow(taDisplay, Priority.ALWAYS);
        mainPane.setCenter(vBox);

        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(10, 20, 10, 20));
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.getChildren().addAll(btnReadFile,btnThread, btnExit);
        mainPane.setBottom(hBox);

        Scene scene = new Scene(mainPane, 800, 400);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void extracted() {
        String ip = address.getText().trim();
        String start = portStart.getText().trim();
        String end = portEnd.getText().trim();
        taDisplay.appendText("开始扫描..." + "\n");
        for (int i = Integer.parseInt(start); i <= Integer.parseInt(end); i++){
            taDisplay.clear();
            try {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(ip, i), 300);
                socket.close();
                taDisplay.appendText("端口" + i + "已开放！\n");
            } catch (IOException e) {
                taDisplay.appendText("端口" + i + "未开放！\n");
            }
        }
        taDisplay.appendText("扫描结束" + "\n");
    }

    class ScanHandler implements Runnable {
        private int totalThreadNum;
        private int threadNo;
        private String ip;

        public ScanHandler(int threadNo, String ip) {
            this.totalThreadNum = 10;
            this.threadNo = threadNo;
            this.ip = ip;
        }

        public ScanHandler(int totalThreadNum, int threadNo, String ip) {
            this.totalThreadNum = totalThreadNum;
            this.threadNo = threadNo;
            this.ip = ip;
        }

        @Override
        public void run() {
            for (int i = Integer.parseInt(start) + threadNo; i <= Integer.parseInt(end);
                 i = i + totalThreadNum){
                try {
                    Socket socket = new Socket();
                    socket.connect(new InetSocketAddress(ip, i), 500);
                    socket.close();
                    int finalI = i;
                    Platform.runLater(() -> taDisplay.appendText("端口" + finalI + "已开放！\n"));
                } catch (IOException e) {
                    int finalI1 = i;
//                    Platform.runLater(() -> taDisplay.appendText("端口" + finalI1 + "未开放！\n"));
                }
                portCount.incrementAndGet();
            }
            if (portCount.compareAndSet(Integer.parseInt(end) - Integer.parseInt(start) + 1, 0)){
                Platform.runLater(() -> taDisplay.appendText("扫描结束" + "\n"));
            }
        }
    }
}
