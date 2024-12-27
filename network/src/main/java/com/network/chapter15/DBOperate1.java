package com.network.chapter15;

import java.lang.reflect.Constructor;
import java.sql.*;

public class DBOperate1 {
  public static void main(String[] args) {
    //加载 MySQL 驱动器，其中 com.mysql.jdbc.Driver 就是由下载的 mysql 驱动包提供
    Class jdbcDriver= null;
    try {
      //加载 MySQL 驱动器，其中 com.mysql.jdbc.Driver 就是由下载的 mysql 驱动包提供
      jdbcDriver = Class.forName("com.mysql.jdbc.Driver");
      //注册 MySQL 驱动器
      DriverManager.registerDriver((Driver)jdbcDriver.newInstance());

      //指定数据库所在位置，先用本地地址测试，访问本地的数据库
      //String dbUrl = "jdbc:mysql://127.0.0.1:3306/STUDENTDB2?characterEncoding=utf8&useSSL=false";
      //String dbUrl = "jdbc:mysql://202.116.195.71:3306/STUDENTDB1?characterEncoding=utf8&useSSL=false";
      String dbUrl = "jdbc:mysql://202.116.195.71:3306/mypeopledb?characterEncoding=utf8&useSSL=false";
      //指定用户名和密码
      String dbUser="student";
      String dbPwd="student";

      //创建数据库连接对象
      Connection con =
        DriverManager.getConnection(dbUrl,dbUser,dbPwd);
      //创建 sql 查询语句
      //String sql="select NO,NAME,AGE,CLASS from students where name like ? and age= ?";
      String sql = "insert into peoples2(NO,NAME,AGE,CLASS,IP) VALUES (?,?,?,?,?)";

      //创建数据库执行对象
      PreparedStatement stmt = con.prepareStatement(sql);
      //设置 sql 语句参数，查找名字以“小”开头，年龄 23 岁的记录
      stmt.setObject(1,"20221003127");
      stmt.setObject(2,"刘铧熙");
      stmt.setObject(3,21);
      stmt.setObject(4,"软件工程2202");
      stmt.setObject(5,"192.168.236.149");

      stmt.executeUpdate();

      sql = "select NO,NAME,AGE,CLASS from PEOPLES2";
      stmt = con.prepareStatement(sql);

      //从数据库的返回集合中读出数据
      ResultSet rs = stmt.executeQuery();

      //循环遍历结果
      while (rs.next())
      {
        //不知道字段类型的情况下，也可以用 rs.getObject(…)来打印输出结果
        System.out.print(rs.getString(1)+"\t");
        System.out.print(rs.getString(2)+"\t");
        System.out.print(rs.getInt(3)+"\t");
        System.out.print(rs.getString(4)+"\n");
      }
      System.out.println("------------------------------------");

      if (rs != null)
        rs.close();
      if (stmt != null)
        stmt.close();
      if (con != null)
        con.close();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
