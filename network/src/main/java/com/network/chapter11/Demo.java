package com.network.chapter11;

import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import jpcap.NetworkInterfaceAddress;
import jpcap.PacketReceiver;
import jpcap.packet.Packet;
import jpcap.packet.TCPPacket;

import java.io.IOException;

public class Demo {
    private PacketHandler packetHandler;
    public static void main(String[] args) throws IOException {
        NetworkInterface[] devices = JpcapCaptor.getDeviceList();
        for (int i = 0; i < devices.length; i++) {
            //打印 GUID information and description 3
            System.out.println(i + ": " + devices[i].name + " " + devices[i].description);
            //打印 MAC address，各段用":隔开
            String mac = "";
            for (byte b : devices[i].mac_address){
               /* mac 地址 6 段，每段是 8 位，而 int 转换的十六进制是 4 个字节，所以和
                0xff 相与，这样就只保留低 8 位 */
                mac = mac + Integer.toHexString(b & 0xff) + ":";
            }
            System.out.println("MAC address:" + mac.substring(0, mac.length() - 1));

            //print out its IP address, subnet mask and broadcast address
            for (NetworkInterfaceAddress addr : devices[i].addresses) {
                //System.out.println(" address:"+addr.address + " " + addr.subnet + " "+ addr.broadcast ) ;
            }
        }

        /*获得网卡接口列表后，打开一个可用的网卡返回 JpcapCaptor 对象，用于
        捕获数据。假设以上测试代码中，devices[0]获取的是真实可用的物理网卡：*/
        /*
        * 四个参数分别为：
        * 第一个参数表示 NetworkInterface 对象；
        * 第二个参数表示捕获的数据字节数，一般最小不小于 68，避免连首部数据
           都抓不全，一般可设置为 1514（以太网帧 MTU=1500）就够用了，特殊情
           况还可设置更大的值（巨型帧的情况）；
        * 第三个参数表示是否开启网卡的混杂模式；
        * 第四个参数是毫秒为单位的超时设置；*/
        JpcapCaptor jpcapCaptor = JpcapCaptor.openDevice(devices[9], 1514,
                true, 20);

    }
}
