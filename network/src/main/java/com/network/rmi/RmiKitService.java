package com.network.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RmiKitService extends Remote {
  public long ipToLong(String ip) throws RemoteException;

  //远程方法二 将长整型转为 ipv4 字符串格式
  public String longToIp(long ipNum) throws RemoteException;

  //远程方法三 将"-"格式连接的 MAC 地址转为 Jpcap 可用的字节数组
  public byte[] macStringToBytes(String macStr) throws RemoteException;

  //远程方法四 将 Jpcap 的 byte[]格式的 MAC 地址转为"-"连接 MAC 字符串
  public String bytesToMACString(byte[] macBytes) throws RemoteException;
}
