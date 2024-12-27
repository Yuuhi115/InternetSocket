package com.network.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RmiMsgService extends Remote {
  public String send(String msg) throws RemoteException;

  //声明远程方法二 用于学生发送学号和姓名给教师端，该方法由教师端实现，学生端调用
  public String send(String yourNo, String yourName) throws RemoteException;
}
