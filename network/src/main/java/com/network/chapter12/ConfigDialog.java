package com.network.chapter12;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;

public class ConfigDialog {
  private JpcapCaptor jpcapCaptor; // 用于返回给主窗体
  private NetworkInterface[] devices = JpcapCaptor.getDeviceList(); // 获取网卡列表
  private Stage stage = new Stage();
  private TextField tfKeyData = new TextField(); // 选择的TCP包中的数据部分关键字


  public ConfigDialog(Stage parentStage) {
    // 设置对话框的父窗体
    stage.initOwner(parentStage);
    // 设置模态窗体
    stage.initModality(Modality.WINDOW_MODAL);
    stage.setResizable(false);
    stage.setTitle("选择网卡并设置参数");

    // 主容器
    VBox vBox = new VBox(10);
    vBox.setPadding(new javafx.geometry.Insets(10));

    // 1. 网卡选择
    Label lblDevices = new Label("请选择网卡：");
    ComboBox<String> cob = new ComboBox<>();
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

    // 2. 抓包过滤器
    Label lblFilter = new Label("设置抓包过滤器（例如 ip and tcp）：");
    TextField tfFilter = new TextField();

    // 抓包包含的关键字
    Label lbKeyData = new Label("包中数据包含的关键字，匹配则显示数据内容(多个关键字则为or关系，用空格隔开)");


    // 3. 抓包大小
    Label lblSnapLen = new Label("设置抓包大小（建议介于 68~1514 之间）：");
    TextField tfSize = new TextField("1514");

    // 4. 是否设置混杂模式
    CheckBox cbPromisc = new CheckBox("是否设置为混杂模式");
    cbPromisc.setSelected(true);

    // 底部按钮
    HBox hBoxBottom = new HBox(10);
    hBoxBottom.setStyle("-fx-alignment: center-right;");

    Button btnConfirm = new Button("确定");
    Button btnCancel = new Button("取消");
    hBoxBottom.getChildren().addAll(btnConfirm, btnCancel);

    // 添加组件到主容器
    vBox.getChildren().addAll(
      lblDevices, cob,
      lblFilter, tfFilter,
      lbKeyData, tfKeyData,
      lblSnapLen, tfSize,
      cbPromisc,
      new Separator(), hBoxBottom
    );

    // 设置场景
    Scene scene = new Scene(vBox);
    stage.setScene(scene);

    // ************** 事件响应部分 **************

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
        int snapLen = Integer.parseInt(tfSize.getText().trim());
        boolean promisc = cbPromisc.isSelected();
        String filter = tfFilter.getText().trim();

        // 初始化 JpcapCaptor
        jpcapCaptor = JpcapCaptor.openDevice(networkInterface, snapLen, promisc, 20);
        if (!filter.isEmpty()) {
          jpcapCaptor.setFilter(filter, true);
        }
        stage.hide(); // 关闭对话框

      } catch (Exception e) {
        new Alert(Alert.AlertType.ERROR, "错误: " + e.getMessage()).showAndWait();
      }
    });

    // 取消按钮动作
    btnCancel.setOnAction(event -> stage.hide());
  }

  // 获取设置了参数的 JpcapCaptor 对象
  public JpcapCaptor getJpcapCaptor() {
    return jpcapCaptor;
  }

  public String getKeyData(){
      return tfKeyData.getText();
  }

  // 显示对话框并阻塞
  public void showAndWait() {
    stage.showAndWait();
  }
}
