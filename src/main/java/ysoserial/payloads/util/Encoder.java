package ysoserial.payloads.util;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.BitSet;

public class Encoder {


    public static String base64_encode(byte[] data){
        return new String(Base64.getEncoder().encode(data));
    }
    public static String base64_encode(String data){
        return new String(Base64.getEncoder().encode(data.getBytes(StandardCharsets.ISO_8859_1)));
    }
    public static byte[] base64_decode(String data){
        return Base64.getDecoder().decode(data);
    }

    public static byte[] base64_decode_smart(String data){
        try {
            return Base64.getDecoder().decode(data);
        }catch (IllegalArgumentException e){
            if(e.getMessage().equals("Last unit does not have enough valid bits")){
                return base64_decode_smart(data + "a");
            }else if(e.getMessage().contains("Input byte array has incorrect ending byte at ")){
                int i = Integer.valueOf(e.getMessage().substring("Input byte array has incorrect ending byte at ".length()));
                return base64_decode_smart(data.substring(0,i));
            }else if(e.getMessage().contains("Illegal base64 character ")){
                int i = Integer.valueOf(e.getMessage().substring("Illegal base64 character ".length()),16);
                i = data.indexOf(new String(Character.toChars(i)));
                return base64_decode_smart(data.substring(0,i));
            }else {
                e.printStackTrace();
            }
        }
        return null;
    }
    public static String urlencode(byte[] data) throws Exception{
        Reflections.setStaticFieldValue(URLEncoder.class,"dontNeedEncoding",new BitSet());
        return URLEncoder.encode(new String(data,"ISO8859_1"),"ISO8859_1");
    }
    public static String urlencode(String data) throws Exception{
        Reflections.setStaticFieldValue(URLEncoder.class,"dontNeedEncoding",new BitSet());
        return URLEncoder.encode(data,"ISO8859_1");
    }
    public static String urldecode(String data) throws Exception{
        return URLDecoder.decode(data,"ISO8859_1");
    }
}
