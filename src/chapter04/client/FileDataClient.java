package chapter04.client;

import java.io.*;
import java.net.Socket;

public class FileDataClient {
  private Socket dataSocket;
  //定义字符输入流和输出流 8
  private PrintWriter pw;
  private BufferedReader br;
  public FileDataClient(String ip, String port) throws IOException {

    //主动向服务器发起连接，实现 TCP 的三次握手过程 13
    //如果不成功，则抛出错误信息，其错误信息交由调用者处理 14
    dataSocket = new Socket(ip, Integer.parseInt(port));
    //得到网络输出字节流地址，并封装成网络输出字符流 17
    OutputStream socketOut = dataSocket.getOutputStream();
    pw = new PrintWriter( // 设置最后一个参数为 true，表示自动 flush 数据 19
      new OutputStreamWriter(//设置 utf-8 编码 20
        socketOut, "utf-8"), true);

    //得到网络输入字节流地址，并封装成网络输入字符流 23
    InputStream socketIn = dataSocket.getInputStream();
    br = new BufferedReader(new InputStreamReader(socketIn, "utf-8"));
  }

  public void getFile(File saveFile) throws IOException {
    if(dataSocket != null){ // dataSocket 是 Socket 类型的成员变量
      FileOutputStream fileOut = new FileOutputStream(saveFile); //新建本地空文件
      byte[] buf = new byte[1024];  // 用来缓存接收的字节数据
      //网络字节输入流
      InputStream socketIn = dataSocket.getInputStream();
      //网络字节输出流
      OutputStream socketOut = dataSocket.getOutputStream();

      //向服务器发送请求的文件名，字符串读写功能
      PrintWriter pw = new PrintWriter(new OutputStreamWriter(socketOut, "utf-8"), true);
      pw.println(saveFile.getName());

      //接收服务器的数据文件，字节读写功能
      int size = 0;
      while ((size = socketIn.read(buf)) != -1) {//读一块到缓存，读取结束返回-1
        fileOut.write(buf, 0, size);
      }//写一块到文件
      fileOut.flush();//关闭前将缓存的数据全部推出
      fileOut.close();//文件传输完毕，关闭流

      if (dataSocket != null) {
        dataSocket.close();
      } else {
        System.err.println("连接 ftp 数据服务器失败");
      }
    }
  }
}
