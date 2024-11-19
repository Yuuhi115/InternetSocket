package com.network.chapter11;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jpcap.JpcapCaptor;
import jpcap.PacketReceiver;
import jpcap.packet.Packet;

public class PacketCaptureFX extends Application {
    TextArea captureInfo = new TextArea();
    Button buttonStart = new Button("开始抓包");
    Button buttonStop = new Button("停止抓包");
    Button buttonClear = new Button("清空");
    Button buttonConfig = new Button("设置");
    Button buttonExit = new Button("退出");
    ConfigDialog configDialog;
    JpcapCaptor jpcapCaptor;
    Thread captureThread;

    class PacketHandler implements PacketReceiver {

        @Override
        public void receivePacket(Packet packet) {
            captureInfo.appendText(packet.toString() + "\n");
        }
    }


    private void interrupt(String threadName) {
        ThreadGroup currentGroup =
        Thread.currentThread().getThreadGroup();
        //获取当前线程的线程组及其子线程组中活动线程数量
        int noThreads = currentGroup.activeCount();
        Thread[] lstThreads = new Thread[noThreads];
        currentGroup.enumerate(lstThreads);//将活动线程复制到线程数组
        //遍历这些活动线程，符合指定线程名的则声明关闭
        for (int i = 0; i < noThreads; i++) {
            if (lstThreads[i].getName().equals(threadName)) {
                lstThreads[i].interrupt();//声明线程关闭
            }
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        BorderPane mainPane = new BorderPane();
        //内容显示区域 13
        VBox vBox = new VBox();
        vBox.setSpacing(10);//各控件之间的间隔 15
        //VBox 面板中的内容距离四周的留空区域 16
        vBox.setPadding(new Insets(10, 20, 10, 20));
        mainPane.setCenter(vBox);

        HBox hBox1 = new HBox();
        hBox1.setPadding(new Insets(10, 20, 10, 20));
        hBox1.setAlignment(Pos.CENTER);
        hBox1.getChildren().addAll(buttonStart,buttonStop,buttonClear,buttonConfig,buttonExit);
        mainPane.setBottom(hBox1);
        captureInfo.setEditable(false);
        vBox.getChildren().addAll(new Label("抓包信息："),captureInfo,hBox1);

        buttonConfig.setOnAction(e -> {
            if (configDialog == null) {
                configDialog = new ConfigDialog(stage);
                configDialog.showAndWait();
                jpcapCaptor = configDialog.getJpcapCaptor();
            }

        });

        buttonStop.setOnAction(e -> {
            interrupt("captureThread");
        });

        buttonStart.setOnAction(e -> {
            //还没有 jpcapCaptor 对象实例，则打开设置对话框 3
            if (jpcapCaptor == null) {
                configDialog = new ConfigDialog(stage);
                configDialog.showAndWait();
                jpcapCaptor = configDialog.getJpcapCaptor();
            }

            //停止还没结束的抓包线程
            //开线程名为"captureThread"的新线程进行抓包
            captureThread =
            new Thread(() -> {
                while (true){
                    //如果声明了本线程被中断，则退出循环
                    if(Thread.currentThread().isInterrupted())
                        break;

                    //每次抓一个包，交给内部类 PacketHandler 的实例处理
                    // PacketHandler 为接口 PacketReceiver 的实现类
                    jpcapCaptor.processPacket(1, new PacketHandler());
                }
            },"captureThread");
            //将当前的主线程优先级提高
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            //降低抓包线程的优先级，避免抓包线程卡住资源 27
            captureThread.setPriority(Thread.MIN_PRIORITY);
            captureThread.start();
        });

        Scene scene = new Scene(mainPane, 700, 400);
        stage.setTitle("网络抓包");
        stage.setScene(scene);
        stage.show();
    }
}
