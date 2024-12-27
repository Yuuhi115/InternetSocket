package com.network.chapter13.client;

import rmi.HelloService;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import rmi.RmiKitService;
import rmi.RmiMsgService;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

public class HelloClientFX extends Application {
    private TextArea taDisplay = new TextArea();
    private TextField tfMessage = new TextField();
    private TextField tfNO = new TextField();
    private TextField tfName = new TextField();
    Button btnSendMsg = new Button("发送信息");
    Button btnSendNoAndName = new Button("发送学号和姓名");

    Button btnListSort = new Button("调用sort方法");
    //客户端也有一份和服务端相同的远程接口 9
    private HelloService helloService;
    private RmiKitService rmiKitService;
    private RmiMsgService rmiMsgService;

    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        VBox vBoxMain = new VBox();
        vBoxMain.setSpacing(10);//各控件之间的间隔 18
        //VBoxMain面板中的内容距离四周的留空区域 19
        vBoxMain.setPadding(new Insets(10, 20, 10, 20));
        HBox hBox = new HBox();
        hBox.setSpacing(10);//各控件之间的间隔 22
        //HBox面板中的内容距离四周的留空区域 23
        hBox.setPadding(new Insets(10, 20, 10, 20));
        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().addAll(new Label("输入信息："),tfMessage,
                btnSendMsg,new Label("学号："),tfNO,new Label("姓名"),tfName,btnSendNoAndName);

        vBoxMain.getChildren().addAll(new Label("信息显示区："),
                taDisplay,hBox);
        Scene scene = new Scene(vBoxMain);
        primaryStage.setScene(scene);
        primaryStage.show();
        //初始化rmi相关操作
        new Thread(()->{
            rmiInit();
        }).start();
        btnSendMsg.setOnAction(event -> {
            try {
                String msg = tfMessage.getText();
                taDisplay.appendText(rmiMsgService.send(msg) + "\n");

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });

        btnSendNoAndName.setOnAction(event -> {
            try {
                String Name = tfName.getText().trim();
                String No = tfNO.getText().trim();
                //System.out.println(rmiKitService.ipToLong("192.168.236.149"));
                taDisplay.appendText(rmiMsgService.send(No,Name));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
        btnListSort.setOnAction((event -> {
            //演示传递和返回数组列表 55
            try {
                ArrayList<Integer> list = new ArrayList<>();
                list.add(8);
                list.add(1);
                list.add(9);
                list.add(7);
                list = helloService.sort(list);
                for (Integer i : list) {
                    taDisplay.appendText(i + "  ");
                }
                taDisplay.appendText("\n");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }));
    }
    public void rmiInit() {
        try {
            Registry registry = LocateRegistry.getRegistry("202.116.195.71",1099);
            System.out.println("RMI远程服务别名列表：");
            for (String name: registry.list()) {
                System.out.println(name);
            }
            /*helloService = (HelloService) registry.lookup("HelloService");
            rmiKitService = (RmiKitService) registry.lookup("RmiKitService");*/
            rmiMsgService = (RmiMsgService) registry.lookup("RmiMsgService");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
