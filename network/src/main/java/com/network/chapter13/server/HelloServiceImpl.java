package com.network.chapter13.server;

import com.network.rmi.HelloService;

import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class HelloServiceImpl extends UnicastRemoteObject implements HelloService {
    private String name;
    public HelloServiceImpl() throws RemoteException {
    }

    public HelloServiceImpl(String name) throws RemoteException {
        this.name = name;
    }


    @Override
    public String echo(String message) throws RemoteException {
        System.out.println("服务端完成一些echo方法相关任务...");
        return "echo: " + message + "from:" + name;
    }

    @Override
    public Date getTime() throws RemoteException {
        System.out.println("服务端完成一些getTime方法相关任务...");
        return new Date();
    }

    @Override
    public ArrayList<Integer> sort(ArrayList<Integer> list) throws RemoteException {
        System.out.println("服务端完成一些getTime方法相关任务...");
        Collections.sort(list);
        return list;
    }
}
