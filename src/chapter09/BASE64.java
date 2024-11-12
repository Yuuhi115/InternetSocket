package chapter09;


import java.io.UnsupportedEncodingException;
import java.util.Base64;

public class BASE64 {


  public static String encode(String str){
    Base64.Encoder encoder = Base64.getEncoder();
    String encodeMsg = null;
    try {
      encodeMsg = encoder.encodeToString(str.getBytes("UTF-8"));
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return encodeMsg;
  }
}
