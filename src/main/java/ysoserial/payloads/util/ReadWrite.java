package ysoserial.payloads.util;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ReadWrite {
    public static void writeFile(String content,String path) throws Exception{
        BufferedWriter out = new BufferedWriter(new FileWriter(path));
        out.write(content);
        out.close();
    }

    public static void writeFile(byte[] content,String path) throws Exception{
        File file = new File(path);
        FileOutputStream f = new FileOutputStream(file);
        f.write(content);
    }

    public static byte[] readFile(String path) throws Exception{
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        return bytes;
    }

    public static byte[] readResource(Class thisclass,String path) throws Exception{

        InputStream inputStream = thisclass.getClassLoader().getResourceAsStream(path);
        byte[] bytes = IOUtils.toByteArray(inputStream);

        return bytes;
    }

    public static byte[] readAllBytesFromInputStream(InputStream inputStream) throws IOException {
        final int bufLen = 4 * 0x400;
        byte[] buf = new byte[bufLen];
        int readLen;
        Exception exception = null;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            while ((readLen = inputStream.read(buf)) != -1)
                outputStream.write(buf, 0, readLen);
            return outputStream.toByteArray();
        }catch (EOFException e){
            return outputStream.toByteArray();
        } catch (IOException e) {
            exception = e;
            throw e;
        } finally {
            outputStream.close();
            if (exception == null) inputStream.close();
            else try {
                inputStream.close();
            } catch (IOException e) {
                exception.addSuppressed(e);
            }
        }
    }

}
