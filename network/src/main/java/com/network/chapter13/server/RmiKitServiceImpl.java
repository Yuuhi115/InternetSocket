package com.network.chapter13.server;

import rmi.RmiKitService;

import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RmiKitServiceImpl extends UnicastRemoteObject implements RmiKitService {
  private String name;
  protected RmiKitServiceImpl() throws RemoteException {
  }

  protected RmiKitServiceImpl(String name) throws RemoteException {
    this.name = name;
  }

  @Override
  public long ipToLong(String ip) throws RemoteException {
    String[] split = ip.split("\\.");
    long num = 0;
    for (int i = 0; i < split.length; i++) {
      num = (Long.parseLong(split[i]) << (8 * (3 - i))) | num;
    }
    return num;
  }

  @Override
  public String longToIp(long ip) throws RemoteException {
    return (ip >>> 24) + "." + ((ip >>> 16) & 0xff) + "." + ((ip >>> 8) & 0xff) + "." + (ip & 0xff);
  }

  @Override
  public byte[] macStringToBytes(String macStr) throws RemoteException {
    //（1）首先判断参数 MAC 中是否包含"-"或":";
    //(2)通过 split 方法将 MAC 切分为字符串数组;
    //(3)定义一个 6 字节的字节数组，循环将十六进制形式的字符串转为字节，赋值给字节数组;
    byte[] MacList = new byte[6];
    int i=0;
    String[] macs = macStr.split("-");
    for (String mac:macs){
      //提示：利用 Integer.parseInt(字符串,进制)将 16 进制表示的字符串转为字节，
      //例如：(byte)Integer.parseInt("0F",16)
      MacList[i++] = (byte) Integer.parseInt(mac,16);
    }
    return MacList;
  }

  @Override
  public String bytesToMACString(byte[] macBytes) throws RemoteException {
    StringBuilder macString = new StringBuilder();
    for (int i=0;i<macBytes.length;i++){
      macString.append(String.format("%02X",macBytes[i]));
      if (i<macBytes.length-1){
        macString.append("-");
      }
    }
    return macString.toString();
  }
}
