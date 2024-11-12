package com.network.chapter08;
import java.io.*;
import java.net.Socket;

public class HTTPClient {
  private Socket socket; //定义套接字 7
  //定义字符输入流和输出流 8
  private PrintWriter pw;
  private BufferedReader br;

  public HTTPClient(String ip, String port) throws IOException {

      //主动向服务器发起连接，实现 TCP 的三次握手过程 13
      //如果不成功，则抛出错误信息，其错误信息交由调用者处理 14
      socket = new Socket(ip, Integer.parseInt(port));
    //得到网络输出字节流地址，并封装成网络输出字符流 17
    OutputStream socketOut = socket.getOutputStream();
    pw = new PrintWriter( // 设置最后一个参数为 true，表示自动 flush 数据 19
      new OutputStreamWriter(//设置 utf-8 编码 20
        socketOut, "utf-8"), true);

    //得到网络输入字节流地址，并封装成网络输入字符流 23
    InputStream socketIn = socket.getInputStream();
    br = new BufferedReader(new InputStreamReader(socketIn, "utf-8"));
  }

  public void send(String msg) {
    //输出字符流，由 Socket 调用系统底层函数，经网卡发送字节流 30
    pw.println(msg);
  }

  public String receive() {
    String msg = null;
    try {
      //从网络输入字符流中读信息，每次只能接受一行信息 37
      //如果不够一行（无行结束符），则该语句阻塞等待， 38
      // 直到条件满足，程序才往下运行 39
      msg = br.readLine();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return msg;
  }

  public void close() {
    try {
      if (socket != null) {
        //关闭 socket 连接及相关的输入输出流,实现四次握手断开 49
        socket.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
