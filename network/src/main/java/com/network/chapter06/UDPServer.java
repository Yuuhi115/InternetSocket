package com.network.chapter06;

import java.io.*;
import java.net.*;
import java.util.Date;

public class UDPServer {
  private int port = 1145;
  private DatagramSocket datagramSocket;
  private static final int MAX_PACKET_SIZE = 512;
  public UDPServer() throws SocketException {
    datagramSocket = new DatagramSocket(port);
    System.out.println("服务器启动监听在 " + port + " 端口");
  }

  public void Service() throws IOException {
    byte[] buffer = new byte[MAX_PACKET_SIZE];
    DatagramPacket datagramPacket = new DatagramPacket(buffer,buffer.length);
    while (true){
      //阻塞等待,来了哪个客户就服务哪个客服
      datagramSocket.receive(datagramPacket);
      //解析数据报文
      int port = datagramPacket.getPort();
      InetAddress address = datagramPacket.getAddress();
      //将接收到的字节数组转为对应的字符串
      String msg = new String(datagramPacket.getData(), 0,datagramPacket.getLength(),"utf-8");

      String info = "20221003127&刘铧熙&"+new Date().toString()+"&"+msg;
      DatagramPacket packetSend = new DatagramPacket(info.getBytes("utf-8"),info.getBytes().length,address,port);
      //发送数据报文
      datagramSocket.send(packetSend);
      // 每次调用前都应该将报文内部消息长度重置为缓冲区的实际长度
      datagramPacket.setLength(buffer.length);
    }
  }

  public static void main(String[] args) {
    try {
      new UDPServer().Service();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
