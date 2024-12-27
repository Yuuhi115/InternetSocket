package com.network.chapter13.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientService extends Remote {
  public void showMsgToClient(String msg) throws RemoteException;
}
