package com.network.chapter13.client;

import com.network.rmi.HelloService;
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

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

public class HelloClientFX extends Application {
    private TextArea taDisplay = new TextArea();
    private TextField tfMessage = new TextField();
    Button btnEcho = new Button("调用echo方法");
    Button btnGetTime = new Button("调用getTime 方法");

    Button btnListSort = new Button("调用sort方法");
    //客户端也有一份和服务端相同的远程接口 9
    private HelloService helloService;

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
                btnEcho,btnGetTime, btnListSort);

        vBoxMain.getChildren().addAll(new Label("信息显示区："),
                taDisplay,hBox);
        Scene scene = new Scene(vBoxMain);
        primaryStage.setScene(scene);
        primaryStage.show();
        //初始化rmi相关操作
        new Thread(()->{
            rmiInit();
        }).start();
        btnEcho.setOnAction(event -> {
            try {
                String msg = tfMessage.getText();
                taDisplay.appendText(helloService.echo(msg) + "\n");

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });

        btnGetTime.setOnAction(event -> {
            try {
                String msg = helloService.getTime().toString();
                taDisplay.appendText(msg + "\n");
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
            Registry registry = LocateRegistry.getRegistry("192.168.201.51",1099);
            System.out.println("RMI远程服务别名列表：");
            for (String name: registry.list()) {
                System.out.println(name);
            }
            helloService = (HelloService) registry.lookup("HelloService");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
