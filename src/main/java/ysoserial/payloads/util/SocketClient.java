package ysoserial.payloads.util;

import org.apache.commons.lang.ArrayUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class SocketClient {
    private String host;
    private int port;
    private Socket socket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private ArrayList<Byte> buffer;

    private SocketClient(String host,int port) throws Exception{
        this.host = host;
        this.port = port;
        this.socket = new Socket(host,port);
        this.outputStream = socket.getOutputStream();
        this.inputStream = socket.getInputStream();
    }
    public static SocketClient remote(String host,int port) throws Exception{
        return new SocketClient(host,port);
    }
    public void send(byte[] data) throws Exception{
        this.outputStream.write(data);
    }
    public void sendline(byte[] data) throws Exception{
        send(ArrayUtils.addAll(data,"\n".getBytes(StandardCharsets.UTF_8)));
    }
    public void sendline() throws Exception{
        send("\n".getBytes(StandardCharsets.UTF_8));
    }
//    public byte[] recv(int len) throws Exception{
//        byte[] bytes = new byte[len];
//        int l = this.inputStream.read(bytes);
//        return ArrayUtils.subarray(bytes,0,l);
//    }
//    public byte[] recvline() throws Exception{
//        return recvutil(0x0a);
//    }

    public byte[] recvline() throws Exception{

        ArrayList<Byte> bytes = new ArrayList<>();

        while (true){
            byte[] b = new byte[0xff];
            int l = this.inputStream.read(b);
            bytes.addAll(Arrays.asList(ArrayUtils.toObject(ArrayUtils.subarray(b,0,l))));
            if (l < 0xff){
                break;
            }
        }
        return ArrayUtils.toPrimitive(bytes.toArray(new Byte[0]));
    }
//    public byte[] recvutil(int b) throws Exception{
//        int c = this.inputStream.read();
//        int i = 0;
//        ArrayList<Byte> bytes = new ArrayList<Byte>();
//
//        while (true){
//            c = this.inputStream.read();
//            bytes.add((byte)c);
//            if(c == b){
//                break;
//            }
//        }
//        return ArrayUtils.toPrimitive(bytes.toArray(new Byte[0]));
//    }
    public byte[] recvall() throws Exception{
        return ReadWrite.readAllBytesFromInputStream(this.inputStream);
    }
    public void close() throws Exception{
        this.outputStream.close();
        this.inputStream.close();
        this.socket.close();
    }
}
