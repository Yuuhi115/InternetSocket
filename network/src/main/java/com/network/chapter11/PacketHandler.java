package com.network.chapter11;

import javafx.application.Platform;
import jpcap.PacketReceiver;
import jpcap.packet.Packet;


public class PacketHandler implements PacketReceiver {
    @Override
    public void receivePacket(Packet packet) {
        Platform.runLater(()->{

        }); //输出抓取的包的原始信息
    }
}
