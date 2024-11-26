package com.network.chapter12;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jpcap.JpcapCaptor;
import jpcap.JpcapSender;
import jpcap.NetworkInterface;

public class NetworkChoiceDialog {

    private Stage stage = new Stage();
    private JpcapSender jpcapSender; // 用于返回给主窗体
    private NetworkInterface[] devices = JpcapCaptor.getDeviceList();
    private Button btnConfirm = new Button("确认");// 获取网卡列表
    private ComboBox<String> cob = new ComboBox<>();

    public NetworkChoiceDialog(Stage parentStage) {
        // 主容器
        VBox vBox = new VBox(10);
        vBox.setPadding(new javafx.geometry.Insets(10));
        // 设置对话框的父窗体
        stage.initOwner(parentStage);
        // 设置模态窗体
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setResizable(false);
        stage.setTitle("选择网卡");

        // 1. 网卡选择
        Label lblDevices = new Label("请选择网卡：");

        cob.setMaxWidth(800);
        if (devices != null && devices.length > 0) {
            for (int i = 0; i < devices.length; i++) {
                cob.getItems().add(i + " : " + devices[i].description);
            }
            // 默认选择第一项
            cob.getSelectionModel().selectFirst();
        } else {
            cob.getItems().add("未检测到可用网卡");
            cob.setDisable(true);
        }

        // 底部按钮
        HBox hBoxBottom = new HBox(10);
        hBoxBottom.setStyle("-fx-alignment: center-right;");


        hBoxBottom.getChildren().add(btnConfirm);

        vBox.getChildren().addAll(lblDevices, cob, hBoxBottom);

        Scene scene = new Scene(vBox);
        stage.setScene(scene);

        // 确定按钮动作
        btnConfirm.setOnAction(event -> {
            try {
                if (devices == null || devices.length == 0) {
                    throw new Exception("未检测到可用网卡，无法开始抓包！");
                }

                int index = cob.getSelectionModel().getSelectedIndex();
                if (index < 0 || index >= devices.length) {
                    throw new Exception("未选择有效网卡！");
                }
                // 获取用户配置
                NetworkInterface networkInterface = devices[index];
                // 初始化 JpcapCaptor
                jpcapSender = JpcapSender.openDevice(networkInterface);
                // 关闭对话框
                stage.hide();

            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "错误: " + e.getMessage()).showAndWait();
            }
        });
    }



    // 显示对话框并阻塞
    public void showAndWait() {
        stage.showAndWait();
    }

    public JpcapSender getJpcapSender() {
        return jpcapSender;
    }
}
