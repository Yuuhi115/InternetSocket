package com.network.chapter12;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jpcap.JpcapSender;

public class SendPacketFX extends Application {
  private TextField tfSourcePort = new TextField();
  private TextField tfTargetPort = new TextField();
  private TextField tfSourceHost = new TextField();
  private TextField tfTargetHost = new TextField();
  private TextField tfSourceMac = new TextField();
  private TextField tfTargetMac = new TextField();
  private TextField tfSendData = new TextField();
  private Button btnSendTCP = new Button("发送TCP包");
  private Button btnSelNetCard = new Button("选择网卡");
  private Button btnExit = new Button("退出");
  private NetworkChoiceDialog dialog;
  private JpcapSender jpcapSender;


  private CheckBox cbSYN = new CheckBox("SYN");
  private CheckBox cbACK = new CheckBox("ACK");
  private CheckBox cbRST = new CheckBox("RST");
  private CheckBox cbFIN = new CheckBox("FIN");

  @Override
  public void start(Stage primaryStage) throws Exception {
    // 主界面布局
    VBox mainLayout = new VBox(10);
    mainLayout.setPadding(new javafx.geometry.Insets(10));

    HBox hBox1 = new HBox();
    Label lbSourcePort = new Label("源端口:");
    Label lbTargetPort = new Label("目的端口:");
    hBox1.getChildren().addAll(lbSourcePort,tfSourcePort,lbTargetPort,tfTargetPort);

    Label lbSign = new Label("TCP标识位:");
    HBox hBox2 = new HBox();
    hBox2.getChildren().addAll(lbSign,cbSYN,cbACK,cbRST,cbFIN);

    HBox hBox3 = new HBox();
    hBox3.setAlignment(Pos.CENTER);
    hBox3.getChildren().addAll(btnSendTCP,btnSelNetCard,btnExit);

    tfSourceMac.setText("94-C6-91-24-7B-34");
    tfSourceHost.setText("192.168.236.149");
    tfTargetHost.setText("202.116.195.71");
    tfTargetMac.setText("b4-e9-b0-c8-35-00");
    tfSendData.setText("20221003127&刘铧熙");
    mainLayout.getChildren().addAll(hBox1,hBox2,new Label("源主机地址"),tfSourceHost,
      new Label("目的主机地址"),tfTargetHost,new Label("源MAC地址"),tfSourceMac,new Label("目的MAC地址"),
      tfTargetMac,new Label("发送的数据"),tfSendData,hBox3);




    // 设置场景
    Scene scene = new Scene(mainLayout, 500, 400);
    primaryStage.setScene(scene);
    primaryStage.setTitle("发送自构包");
    dialog = new NetworkChoiceDialog(primaryStage);
    dialog.showAndWait();
    jpcapSender = dialog.getJpcapSender();
    primaryStage.show();

    btnSendTCP.setOnAction(event -> {
       try {
         int srcPort = Integer.parseInt(tfSourcePort.getText().trim());
         int dstPort = Integer.parseInt((tfTargetPort.getText().trim()));
         String srcHost = tfSourceHost.getText().trim();
         String dstHost = tfTargetHost.getText().trim();
         String srcMAC = tfSourceMac.getText().trim();
         String dstMAC = tfTargetMac.getText().trim();
         String data = tfSendData.getText();
         //调用发包方法
         PacketSender.sendTCPPacket(jpcapSender, srcPort, dstPort, srcHost,
           dstHost, data, srcMAC, dstMAC,cbSYN.isSelected(),
           cbACK.isSelected(),cbRST.isSelected(),cbFIN.isSelected());

         new Alert(Alert.AlertType.INFORMATION, "已发送！").showAndWait();

         } catch (Exception e) {
         new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
         }
       });
    btnSelNetCard.setOnAction(event -> {
      dialog.showAndWait();
      jpcapSender = dialog.getJpcapSender();
    });
    btnExit.setOnAction(event -> {
      primaryStage.close();
    });
  }
}
