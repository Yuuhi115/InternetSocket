package com.network.chapter12;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import jpcap.JpcapCaptor;
import jpcap.PacketReceiver;
import jpcap.packet.Packet;

public class PacketCaptureFX extends Application {
  private NetworkChoiceDialog networkChoiceDialog;
  private ConfigDialog configDialog; // 设置对话框
  private JpcapCaptor jpcapCaptor;  // JpcapCaptor 对象
  private Thread packetCaptureThread; // 抓包线程
  private TextArea displayArea; // 显示抓包数据的文本区域

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    primaryStage.setTitle("网络抓包工具");

    // 主界面布局
    VBox mainLayout = new VBox(10);
    mainLayout.setPadding(new javafx.geometry.Insets(10));

    // 按钮
    Button btnOpenConfigDialog = new Button("设置");
    Button btnStart = new Button("开始抓包");
    Button btnStop = new Button("停止抓包");
    Button btnClear = new Button("清空");
    Button btnExit = new Button("退出");
    Button btnChoiceAndSend = new Button("发包");

    // 显示区
    displayArea = new TextArea();
    displayArea.setEditable(false);
    displayArea.setWrapText(true);

    // 按钮容器
    HBox buttonLayout = new HBox(10, btnOpenConfigDialog, btnStart, btnStop, btnClear, btnChoiceAndSend, btnExit);
    buttonLayout.setStyle("-fx-alignment: center;");

    // 添加组件到主界面
    mainLayout.getChildren().addAll(displayArea, buttonLayout);

    // 设置场景
    Scene scene = new Scene(mainLayout, 800, 300);
    primaryStage.setScene(scene);
    primaryStage.show();

    // ************** 按钮事件响应 **************

    // 设置按钮动作事件
    btnOpenConfigDialog.setOnAction(event -> {
      // 如果还没有实例化对话框，则先实例化
      if (configDialog == null) {
        configDialog = new ConfigDialog(primaryStage);
      }

      // 阻塞式显示，等待设置窗体完成设置
      configDialog.showAndWait();

      // 获取设置后的 JpcapCaptor 对象实例
      jpcapCaptor = configDialog.getJpcapCaptor();
    });

    btnChoiceAndSend.setOnAction(event -> {
      if (networkChoiceDialog == null) {
        networkChoiceDialog = new NetworkChoiceDialog(primaryStage);
      }
      networkChoiceDialog.showAndWait();
    });

    // 开始抓包按钮动作事件
    btnStart.setOnAction(event -> {
      // 如果还没有 jpcapCaptor 对象实例，则打开设置对话框
      if (jpcapCaptor == null) {
        btnOpenConfigDialog.fire(); // 触发设置按钮动作
        if (jpcapCaptor == null) {
          // 如果用户未完成设置，则直接返回
          return;
        }
      }

      // 停止还未结束的抓包线程
      interrupt("captureThread");

      // 开线程名为 "captureThread" 的新线程进行抓包
      packetCaptureThread = new Thread(() -> {
        while (true) {
          // 如果声明了本线程被中断，则退出循环
          if (Thread.currentThread().isInterrupted()) {
            break;
          }

          // 每次抓一个包，交给 PacketHandler 处理
          jpcapCaptor.processPacket(1, new PacketHandler());
        }
      }, "captureThread");

      // 设置线程优先级
      Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
      packetCaptureThread.setPriority(Thread.MIN_PRIORITY);
      packetCaptureThread.start();
    });

    // 停止抓包按钮动作事件
    btnStop.setOnAction(event -> interrupt("captureThread"));

    // 清空按钮动作事件
    btnClear.setOnAction(event -> displayArea.clear());

    // 退出按钮动作事件
    btnExit.setOnAction(event -> {
      // 停止抓包线程
      interrupt("captureThread");
      // 关闭程序
      Platform.exit();
    });
  }

  /**
   * 停止指定线程名的线程
   */
  private void interrupt(String threadName) {
    ThreadGroup currentGroup = Thread.currentThread().getThreadGroup();
    int noThreads = currentGroup.activeCount();
    Thread[] lstThreads = new Thread[noThreads];
    currentGroup.enumerate(lstThreads);

    for (int i = 0; i < noThreads; i++) {
      if (lstThreads[i].getName().equals(threadName)) {
        lstThreads[i].interrupt();
      }
    }
  }

  /**
   * 内部类：实现 PacketReceiver 接口，用于处理抓到的包
   */
  class PacketHandler implements PacketReceiver {
    @Override
    public void receivePacket(Packet packet) {
      String keyData = null;
      // 在显示区显示抓包原始信息
      //Platform.runLater(() -> displayArea.appendText(packet.toString() + "\n"));
      keyData = configDialog.getKeyData();
      if (keyData == null || keyData.trim().equalsIgnoreCase(""))
        return;
      try {
        String[] keyList = keyData.split(" ");
        String msg = new String(packet.data, 0, packet.data.length, "utf-8");
        for (String key : keyList) {
          if (msg.toUpperCase().contains(key.toUpperCase())) {
            Platform.runLater(() -> {
              displayArea.appendText("数据部分：" + msg + "\n\n");
            });
          }
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }
}

