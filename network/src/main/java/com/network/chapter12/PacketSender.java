package com.network.chapter12;

import jpcap.JpcapSender;
import jpcap.packet.EthernetPacket;
import jpcap.packet.IPPacket;
import jpcap.packet.TCPPacket;

import java.net.InetAddress;

public class PacketSender {

    /**
     * @param sender JpcapSender 类型
     * @param srcPort 源端口
     * @param dstPort 目的端口
     * @param srcHost ip 地址形式或类似 www.baidu.com 的域名形式
     * @param dstHost
     * @param data 填充到 tcp 包中的数据
     * @param srcMAC 格式为"dc-8b-28-87-b9-82"或"dc:8b:28:87:b9:82"
     * @param dstMAC
     * @param syn 这几个为常用标识位
     * @param ack
     * @param rst
     * @param fin
     */
    public static void sendTCPPacket(JpcapSender sender, int srcPort,
                                     int dstPort, String srcHost, String dstHost, String data,
                                     String srcMAC, String dstMAC,
                                     boolean syn, boolean ack, boolean rst, boolean fin) {
        try {
            //构造一个 TCP 包
            TCPPacket tcp = new TCPPacket(srcPort,dstPort,56,78,false,ack,false,rst,syn,fin,true,true,200,10);
            //设置 IPv4 报头参数，ip 地址可以伪造


            tcp.setIPv4Parameter(0,false,false,false,0,false,false,false,0,1010101,100,IPPacket.IPPROTO_TCP, InetAddress.getByName(srcHost),
                    InetAddress.getByName (dstHost));
            //填充 TCP 包中的数据
            tcp.data = data.getBytes("utf-8");
            //构造相应的 MAC 帧

            EthernetPacket ether = new EthernetPacket();
            //set frame type as IP
            ether.frametype = EthernetPacket.ETHERTYPE_IP;
            //set the datalink frame of the tcp packet as ether
            tcp.datalink = ether;

            ether.src_mac = convertMacFormat(srcMAC);
            ether.dst_mac = convertMacFormat(dstMAC);
            if(ether.src_mac == null || ether.dst_mac==null)
                throw new Exception("MAC 地址输入错误");

            sender.sendPacket(tcp);

            System.out.println("发包成功！");


        } catch (Exception e) {
            System.err.println(e.getMessage());
            //重新抛出异常，调用者可以捕获处理
            throw new RuntimeException(e);
        }
    }

   public static byte[] convertMacFormat(String MAC) {
        //（1）首先判断参数 MAC 中是否包含"-"或":";
       //(2)通过 split 方法将 MAC 切分为字符串数组;
       //(3)定义一个 6 字节的字节数组，循环将十六进制形式的字符串转为字节，赋值给字节数组;
       byte[] MacList = new byte[6];
       int i=0;
       String[] macs = MAC.split("-");
        for (String mac:macs){
            //提示：利用 Integer.parseInt(字符串,进制)将 16 进制表示的字符串转为字节，
            //例如：(byte)Integer.parseInt("0F",16)
            MacList[i++] = (byte) Integer.parseInt(mac,16);
        }
        return MacList;
    }
}
