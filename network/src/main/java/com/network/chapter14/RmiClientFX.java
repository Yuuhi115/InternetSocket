package com.network.chapter14;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import chapter13.rmi.ClientService;
import chapter13.rmi.ServerService;

import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RmiClientFX extends Application {
  private Button btnExit = new Button("退出");
  private Button btnSend = new Button("发送");

  private Button btnLogin = new Button("登录");
  //学号和姓名的文本框
  private TextField tfNO = new TextField();
  private TextField tfName = new TextField();
  private TextField tfMsg = new TextField();
  //显示信息的文本区域
  private TextArea taDisplay = new TextArea();
  //服务端远程接口
  private ServerService serverService;
  //客户端远程接口
  private ClientService clientService;
  private String client;//学号-姓名的格式

  public static void main(String[] args)
  {
    launch(args);
  }
  @Override
  public void start(Stage primaryStage) throws Exception {
    //初始化界面元素
    initComponents(primaryStage);
    //初始化 rmi 相关操作
    new Thread(() -> {
      initRmi();
    }).start();
    //初始化事件
    initEvent(primaryStage);


    //发送按钮
    btnSend.setOnAction(event -> {
      String sendMsg = tfMsg.getText();
      try {
        //调用服务端的远程服务，群发消息
        serverService.sendPublicMsgToServer(client, sendMsg);
      } catch (RemoteException e) {
        e.printStackTrace();
      }
    });
    //退出按钮
    btnExit.setOnAction(event -> {
      //调用退出方法
      exit();
    });
    //响应窗体关闭
    primaryStage.setOnCloseRequest(event ->
    {
      //调用退出方法
      exit();
    });
  }


  private void initComponents(Stage primaryStage)
  {
    BorderPane mainPane = new BorderPane();
    //登录区域
    HBox hBoxLogin = new HBox();
    hBoxLogin.setSpacing(10);
    hBoxLogin.setPadding(new Insets(10, 20, 10, 20));
    hBoxLogin.getChildren().addAll(new Label("学号"),
      tfNO, new Label("姓名"), tfName, btnLogin);
    //内容显示区域
    VBox vBox = new VBox();
    vBox.setSpacing(10);//各控件之间的间隔
    //VBox 面板中的内容距离四周的留空区域
    vBox.setPadding(new Insets(10, 20, 10, 20));
    vBox.getChildren().addAll(hBoxLogin,taDisplay);
    Scene scene = new Scene(vBox);
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  //初始化 rmi 操作
  private void initRmi()
  {
    try
    {
      String ip = "202.116.195.71";
      //为了不和上一讲端口冲突，临时修改为 8008，一般不做特别说明，是使用1099
      int port = 8008;
      //获取 RMI 注册器
      Registry registry = LocateRegistry.getRegistry(ip, port);
      for (String name : registry.list())
      {
        System.out.println(name);
      }
      //客户端(调用端)到注册器中使用助记符寻找并创建远程服务对象的客户端(调用端)stub，之后本地调用 serverService 的方法，实质就是调用了远程同名接口下的同名方法
      serverService = (ServerService)
        registry.lookup("ServerService");
      //实例化本地客户端的远程对象
      clientService = new ClientServiceImpl(this);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  //初始化事件
  private void initEvent(Stage primaryStage) {
    //登录，加入在线用户群，格式为学号-姓名
    btnLogin.setOnAction(event ->
    {
      try {
        String NO = tfNO.getText().trim();
        String name = tfName.getText().trim();
        if (!NO.equals("") && !name.equals("")) {
          client = NO + "-" + name;
          //以下为课堂计分操作，加入在线用户组成功，完成任务得分．
          //调用服务端的远程方法，将客户加入在线用户组，并将客户端的远程对象注入，用于服务器跟踪客户
          String retStr =
            serverService.addClientToOnlineGroup(client, clientService);
          taDisplay.appendText(retStr + "\n");
        }
      }
      //服务端如果重启，就会失去 rmi 连接，引发此异常，并且可能引发调用serverService 方法的空指针异常，所以捕获这两个异常并再次登录
      catch (NoSuchObjectException | NullPointerException e) {
        System.err.println(e.getMessage());
        initRmi();
        btnLogin.fire();
      } catch (RemoteException e) {
        e.printStackTrace();
      }
    });
  }

  public void appendMsg(String msg)
  {
    taDisplay.appendText(msg + "\n");
  }


//退出方法，一定要记住调用服务端远程方法清除登录记录，否则会造成无法再次登录
  private void exit() {
    try {
      serverService.removeClientFromOnlineGroup(client, clientService);
    } catch (RemoteException e) {
      e.printStackTrace();
    } finally {
      System.exit(0);
    }
  }
}

