package com.network.chapter11;

import com.network.chapter08.HTTPClient;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;


public class URLClientThreadFX extends Application {
  private Button btnExit = new Button("退出");
  private Button btnSend = new Button("发送");
  private Button btnOpen = new Button("加载");
  private Button btnSave = new Button("保存");
  private Button btnConn = new Button("连接");
  private Button btnHttpRequest = new Button("网页请求");
  private Button btnClear = new Button("清空");

  //待发送信息的文本框 6
  private TextField tfSend = new TextField();
  //显示信息的文本区域 8
  private TextArea taDisplay = new TextArea();
  //ip输入栏
  private TextField ipSend = new TextField();
  //端口输入栏
  private TextField portSend = new TextField();
  private HTTPClient HTTPClient;

  Thread receiveThread; //定义子线程成员变量

  public void start(Stage primaryStage) {
    BorderPane mainPane = new BorderPane();
    taDisplay.setWrapText(true);
    //内容显示区域 13
    VBox vBox = new VBox();
    vBox.setSpacing(10);//各控件之间的间隔 15
    //VBox 面板中的内容距离四周的留空区域 16
    vBox.setPadding(new Insets(10, 20, 10, 20));

    HBox hBox1 = new HBox();
    hBox1.setPadding(new Insets(10, 20, 10, 20));
    hBox1.setAlignment(Pos.CENTER);
    hBox1.getChildren().addAll(new Label("ip地址："),ipSend,new Label("端口号："),portSend,btnConn);


    vBox.getChildren().addAll(hBox1,new Label("信息显示区："),
      taDisplay, new Label("信息输入区："), tfSend);
    //设置显示信息区的文本区域可以纵向自动扩充范围 20
    VBox.setVgrow(taDisplay, Priority.ALWAYS);
    mainPane.setCenter(vBox);
    //底部按钮区域 23
    HBox hBox = new HBox();
    hBox.setSpacing(10);
    hBox.setPadding(new Insets(10, 20, 10, 20));
    hBox.setAlignment(Pos.CENTER_RIGHT);
    //hBox.getChildren().addAll(btnSend, btnSave, btnOpen, btnExit);
    hBox.getChildren().addAll(btnHttpRequest,btnClear,btnExit);
    mainPane.setBottom(hBox);





    //发送按钮绑定事件
    btnHttpRequest.setOnAction(event -> {
      taDisplay.clear();
      String address = tfSend.getText().trim();

      try {
        URL url = new URL(address);
        System.out.printf("连接%s成功！\n", address);
        //获得 url 的字节流输入
        InputStream in = url.openStream();
        //装饰成字符输入流
        BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));

        receiveThread = new Thread(()->{
          String msg = null;
          while (true){
            try {
              if ((msg = br.readLine()) == null) break;
              final String finalMsg = msg;
              Platform.runLater(()->{
                taDisplay.appendText(finalMsg + "\n");
              });
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
          Platform.runLater(()->{
            taDisplay.appendText("对话已关闭！\n");
          });
        });
        receiveThread.start();

      } catch (MalformedURLException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }

      //String receiveMsg = TCPMailClient.receive();//单线程：从服务器接收一行字符
      //taDisplay.appendText(receiveMsg + "\n");
    });



    //连接按钮绑定事件
    btnConn.setOnAction(event -> {
      String ip = ipSend.getText().trim();
      String port = portSend.getText().trim();
      try {
        //TCPMailClient 不是局部变量，是本程序定义的一个 TCPMailClient 类型的成员变量 6
        HTTPClient = new HTTPClient(ip,port);

        receiveThread = new Thread(()->{
          String msg = null;
          while (( msg = HTTPClient.receive())!= null){
            //runLater 中的 lambda 表达式不能直接访问外部非 final 类型局部变量
            //所以这里使用了一个临时常量，可以省略 final，但本质还是会作为常量使用
            final String msgTemp = msg; //msgTemp 实质是 final 类型
            Platform.runLater(()->{
              taDisplay.appendText(msgTemp + "\n");
            });
          }
          Platform.runLater(()->{
            taDisplay.appendText("对话已关闭！\n");
          });
        }, "my-readServerThread"); //给新线程取别名，方便识别
        receiveThread.start(); //启动线程

        //单线程成功连接服务器，接收服务器发来的第一条欢迎信息 8
        //String firstMsg = TCPMailClient.receive();
        //taDisplay.appendText(firstMsg + "\n");
      } catch (Exception e) {
        taDisplay.appendText("服务器连接失败！" + e.getMessage() + "\n");
      }
    });

    //退出按钮绑定事件
    btnExit.setOnAction(event -> {
      if(HTTPClient != null){
        //向服务器发送关闭连接的约定信息
        HTTPClient.send("bye");
        HTTPClient.close();
      }
      System.exit(0);
    });

    //清空按钮
    btnClear.setOnAction(event -> {
      tfSend.clear();
      taDisplay.clear();
    });


    Scene scene = new Scene(mainPane, 700, 400);
    primaryStage.setScene(scene);
    primaryStage.show();
  }
}

