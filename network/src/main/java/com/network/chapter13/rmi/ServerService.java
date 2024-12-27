package com.network.chapter13.rmi;

import chapter13.rmi.ClientService;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerService extends Remote {
   // 客户加入群组的远程方法
  public String addClientToOnlineGroup(String client, ClientService clientService) throws RemoteException;
  /**
   * 客户退出群组的远程方法
   */
  public String removeClientFromOnlineGroup(String client,ClientService clientService) throws RemoteException;
  /**
   * 客户发送群聊信息的远程方法
   * @param client 格式为学号-姓名的字符串
   * @param msg 要发送的信息
   * @throws
   */
  public void sendPublicMsgToServer(String client,String msg) throws RemoteException;
}
