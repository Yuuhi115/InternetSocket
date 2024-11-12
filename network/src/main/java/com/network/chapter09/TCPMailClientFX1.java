package com.network.chapter09;

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

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class TCPMailClientFX1 extends Application {
  private Button btnExit = new Button("退出");
  private Button btnSend = new Button("发送");
  private Button btnOpen = new Button("加载");
  private Button btnSave = new Button("保存");
  private Button btnConn = new Button("连接");
  //待发送信息的文本框 6
  private TextField tfSend = new TextField();
  //显示信息的文本区域 8
  private TextArea taDisplay = new TextArea();

  private TextField smtpServer = new TextField();

  private TextField smtpServerPort = new TextField();

  private TextField senderAddress = new TextField();

  private TextField receiverAddress = new TextField();

  private TextField mailTitle = new TextField();

  private TextArea mailContent = new TextArea();

  private TextArea mailServerFeedback = new TextArea();

  private TCPMailClient tcpMailClient;

  ExecutorService executorService = Executors.newFixedThreadPool(6);


  Thread receiveThread; //定义子线程成员变量

  public void start(Stage primaryStage) {
    smtpServer.setText("smtp.qq.com");
    smtpServerPort.setText("465");
    senderAddress.setText("1274406252@qq.com");
    receiverAddress.setText("1521427714@qq.com");
    BorderPane mainPane = new BorderPane();
    //内容显示区域 13
    VBox vBox = new VBox();
    vBox.setSpacing(10);//各控件之间的间隔 15
    //VBox 面板中的内容距离四周的留空区域 16
    vBox.setPadding(new Insets(10, 20, 10, 20));

    HBox hBox1 = new HBox();
    hBox1.setPadding(new Insets(10, 20, 10, 20));
    hBox1.setAlignment(Pos.CENTER);
    hBox1.getChildren().addAll(new Label("邮件服务器地址："),smtpServer,new Label("邮件服务器端口："),smtpServerPort);

    HBox hBox2 = new HBox();
    hBox2.setPadding(new Insets(10, 20, 10, 20));
    hBox2.setAlignment(Pos.CENTER);
    hBox2.getChildren().addAll(new Label("邮件发送者地址："),senderAddress,new Label("邮件接受者地址："),receiverAddress);

    HBox hBox3 = new HBox();
    hBox3.setPadding(new Insets(10, 20, 10, 20));
    hBox3.setAlignment(Pos.CENTER);
    hBox3.getChildren().addAll(new Label("邮件标题："),mailTitle);

    VBox vBox1 = new VBox();
    vBox1.setSpacing(10);//各控件之间的间隔 15
    vBox1.setPadding(new Insets(10, 20, 10, 20));
    vBox1.getChildren().addAll(new Label("邮件正文："),
      mailContent);

    VBox vBox2 = new VBox();
    vBox2.setSpacing(10);//各控件之间的间隔 15
    vBox2.setPadding(new Insets(10, 20, 10, 20));
    vBox2.getChildren().addAll(new Label("服务器反馈信息"),mailServerFeedback);

    HBox hBox4 = new HBox();
    hBox4.setPadding(new Insets(10, 20, 10, 20));
    hBox4.setAlignment(Pos.CENTER);
    hBox4.getChildren().addAll(vBox1,vBox2);

    vBox.getChildren().addAll(hBox1,hBox2,hBox3,hBox4);
    //设置显示信息区的文本区域可以纵向自动扩充范围 20
    //VBox.setVgrow(taDisplay, Priority.ALWAYS);
    mainPane.setCenter(vBox);
    //底部按钮区域 23
    HBox hBox = new HBox();
    hBox.setSpacing(10);
    hBox.setPadding(new Insets(10, 20, 10, 20));
    hBox.setAlignment(Pos.CENTER_RIGHT);
    hBox.getChildren().addAll(btnSend, btnSave, btnOpen, btnExit);
    mainPane.setBottom(hBox);


    //发送按钮绑定事件
    btnSend.setOnAction(event -> {
      String smtpAddr = smtpServer.getText().trim();
      String smtpPort = smtpServerPort.getText().trim();
      String username = "1274406252@qq.com";
      String authCode = "mxfmbshahvrjhgdj";
      try {
        tcpMailClient = new TCPMailClient(smtpAddr,smtpPort);
        executorService.execute(() -> {
          tcpMailClient.send("HELO myfriend");
          mailServerFeedback.appendText("客户端发送：HELO myfriend" + '\n');

          tcpMailClient.send("AUTH LOGIN");
          mailServerFeedback.appendText("客户端发送：AUTH LOGIN" + '\n');

          String msg = BASE64.encode(username);
          tcpMailClient.send(msg);
          mailServerFeedback.appendText("客户端发送："+ msg + '\n');

          msg = BASE64.encode(authCode);
          tcpMailClient.send(msg);
          mailServerFeedback.appendText("客户端发送："+ msg + '\n');

          msg = "MAIL FROM:<" + senderAddress.getText().trim() + ">";
          tcpMailClient.send(msg);
          mailServerFeedback.appendText("客户端发送："+ msg + '\n');

          msg = "RCPT TO:<" + receiverAddress.getText().trim() + ">";
          tcpMailClient.send(msg);
          mailServerFeedback.appendText("客户端发送："+ msg + '\n');

          msg = "DATA";
          tcpMailClient.send(msg);
          mailServerFeedback.appendText("客户端发送："+ msg + '\n');

          msg = "FROM:" + senderAddress.getText().trim();
          tcpMailClient.send(msg);
          mailServerFeedback.appendText("客户端发送："+ msg + '\n');

          msg = "SUBJECT:" + mailTitle.getText().trim();
          tcpMailClient.send(msg);
          mailServerFeedback.appendText("客户端发送："+ msg + '\n');

          msg = "TO:" + receiverAddress.getText().trim();
          tcpMailClient.send(msg);
          mailServerFeedback.appendText("客户端发送："+ msg + '\n');

          tcpMailClient.send("");

          tcpMailClient.send(mailContent.getText());
          mailServerFeedback.appendText("客户端发送："+ mailContent.getText() + '\n');

          tcpMailClient.send(".");
          mailServerFeedback.appendText("客户端发送：." + '\n');
          //mailServerFeedback.appendText("服务端返回：" + tcpMailClient.receive());
          tcpMailClient.send("quit");
          mailServerFeedback.appendText("客户端发送：quit" + '\n');
        });
        executorService.execute(() ->{
          mailServerFeedback.appendText("服务端返回：" + tcpMailClient.receive() + '\n');

          mailServerFeedback.appendText("服务端返回：" + tcpMailClient.receive() + '\n');

          mailServerFeedback.appendText("服务端返回：" + tcpMailClient.receive() + '\n');

          mailServerFeedback.appendText("服务端返回：" + tcpMailClient.receive() + '\n');

          mailServerFeedback.appendText("服务端返回：" + tcpMailClient.receive() + '\n');

          mailServerFeedback.appendText("服务端返回：" + tcpMailClient.receive() + '\n');

          mailServerFeedback.appendText("服务端返回：" + tcpMailClient.receive() + '\n');

          mailServerFeedback.appendText("服务端返回：" + tcpMailClient.receive() + '\n');

          mailServerFeedback.appendText("服务端返回：" + tcpMailClient.receive() + '\n');

          mailServerFeedback.appendText("服务端返回：" + tcpMailClient.receive() + '\n');

        });



        //mailServerFeedback.appendText("服务端返回：" + tcpMailClient.receive());
      } catch (IOException e) {
        e.printStackTrace();
      }


      //String receiveMsg = tcpClient.receive();//单线程：从服务器接收一行字符
      //taDisplay.appendText(receiveMsg + "\n");
    });

    //连接按钮绑定事件
    /*btnConn.setOnAction(event -> {
      String ip = ipSend.getText().trim();
      String port = portSend.getText().trim();
      try {
        //tcpClient 不是局部变量，是本程序定义的一个 TCPMailClient 类型的成员变量 6
        tcpClient = new TCPClient(ip,port);

        receiveThread = new Thread(()->{
          String msg = null;
          while (( msg = tcpClient.receive())!= null){
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
        //String firstMsg = tcpClient.receive();
        //taDisplay.appendText(firstMsg + "\n");
      } catch (Exception e) {
        taDisplay.appendText("服务器连接失败！" + e.getMessage() + "\n");
      }
    });
*/
    //退出按钮绑定事件
    btnExit.setOnAction(event -> {
      if(tcpMailClient != null){
        //向服务器发送关闭连接的约定信息
        tcpMailClient.close();
      }
      System.exit(0);
    });


    Scene scene = new Scene(mainPane, 700, 400);
    primaryStage.setScene(scene);
    primaryStage.show();
  }
}

