package com.network.chapter10;

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
import java.util.ArrayList;
import java.util.List;

public class HostScannerFX extends Application {
    private final String start = "192.168.236.80";
    private final String end = "192.168.236.255";
    private List<String> availablePort = new ArrayList<>();
    private File file = new File("test.txt");
    private Thread thread;

    private Button btnScanner = new Button("扫描");
    private Button btnExecute = new Button("执行命令");
    private Button btnExit = new Button("退出");

    private TextField tfSend = new TextField();
    private TextField addressStart = new TextField();
    private TextField addressEnd = new TextField();

    private TextArea taDisplay = new TextArea();

    public long ipToLong(String ip) {
        String[] split = ip.split("\\.");
        long num = 0;
        for (int i = 0; i < split.length; i++) {
            num = (Long.parseLong(split[i]) << (8 * (3 - i))) | num;
        }
        return num;
    }

    public String longToIp(long ip) {
        return (ip >>> 24) + "." + ((ip >>> 16) & 0xff) + "." + ((ip >>> 8) & 0xff) + "." + (ip & 0xff);
    }

    public void start(Stage primaryStage) {
        taDisplay.setWrapText(true);
        taDisplay.setEditable(false);
        addressStart.setText(start);
        addressEnd.setText(end);

        btnExit.setOnAction(event -> System.exit(0));

        btnScanner.setOnAction(event -> {
            String start = addressStart.getText().trim();
            String end = addressEnd.getText().trim();
            long startIp = ipToLong(start);
            long endIp = ipToLong(end);
            Thread thread = new Thread(() -> {
                Platform.runLater(() -> {
                    taDisplay.appendText("开始扫描...\n");
                });
                for (long i = startIp; i <= endIp; i++) {
                    String ip = longToIp(i);
                    try {
                        InetAddress address = InetAddress.getByName(ip);
                        boolean status = address.isReachable(300);
                        if (status) {

                            Platform.runLater(() -> {
                                taDisplay.appendText("IP地址：" + ip + " 可用\n");
                                availablePort.add(ip);
                                System.out.println(ip);

                            });
                        }else {
                            Platform.runLater(() -> {
                                taDisplay.appendText("IP地址：" + ip + " 不可用\n");
                            });
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                Platform.runLater(() -> {
                    taDisplay.appendText("扫描结束！\n");
                });
                try {
                    OutputStream os = new FileOutputStream(file,true);
                    for(String ip:availablePort){
                        os.write(ip.getBytes("UTF-8"));
                        os.write('\n');
                    }
                    os.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            thread.start();
        });
        btnExecute.setOnAction(event -> {
            taDisplay.clear();
            String cmd = tfSend.getText().trim();
            tfSend.clear();
            Thread thread = new Thread(() -> {
                Platform.runLater(() -> {
                    taDisplay.appendText("开始执行命令...\n");
                });
                try {
                    Process process = Runtime.getRuntime().exec(cmd);
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            process.getInputStream(), "GBK"));
                    String line;
                    while ((line = br.readLine()) != null) {
                        String finalLine = line;
                        Platform.runLater(() -> {
                            taDisplay.appendText(finalLine + "\n");
                        });
                    }
                    Platform.runLater(() -> {
                        taDisplay.appendText("命令执行完毕！\n");
                    });
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            });
            thread.start();
        });
        BorderPane mainPane = new BorderPane();

        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10, 20, 10, 20));

        HBox hBox1 = new HBox();
        hBox1.setSpacing(10);
        hBox1.setPadding(new Insets(10, 20, 10, 20));
        hBox1.setAlignment(Pos.CENTER);
        hBox1.getChildren().addAll(new Label("起始IP地址："), addressStart,
                new Label("结束IP地址："), addressEnd, btnScanner);

        vBox.getChildren().addAll(hBox1, new Label("信息显示区："),
                taDisplay, new Label("信息输入区："), tfSend);

        VBox.setVgrow(taDisplay, Priority.ALWAYS);
        mainPane.setCenter(vBox);

        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(10, 20, 10, 20));
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.getChildren().addAll(btnExecute, btnExit);
        mainPane.setBottom(hBox);

        Scene scene = new Scene(mainPane, 800, 400);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
