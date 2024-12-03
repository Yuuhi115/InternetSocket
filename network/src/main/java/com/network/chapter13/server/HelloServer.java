package com.network.chapter13.server;

import com.network.rmi.HelloService;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class HelloServer {
    public static void main(String[] args) {
        try {
            System.setProperty("java.rmi.server.hostname","192.168.201.51");
            /*(1)启动RMI注册器，并监听在1099端口（这是RMI的默认端口，正如11
            HTTP的默认端口是80）*/
            Registry registry = LocateRegistry.createRegistry(1099);
            /*(2)实例化远程服务对象，如果有多个远程接口，只实例化自己实现的接口15
            （为什么可能有没有实例化的接口？）*/
            HelloService helloService = new HelloServiceImpl("XXX的远程服务");
            /*(3)用助记符来注册发布远程服务对象,助记符建议和远程服务接口命名相20
            同，这样更好起到“助记”效果*/
            registry.rebind("HelloService", helloService);
            System.out.println("发布了一个HelloService RMI 远程服务");
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
