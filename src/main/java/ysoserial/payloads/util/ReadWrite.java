package ysoserial.payloads.util;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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
        f.close();
    }

    public static String writeTempFile(byte[] content) throws Exception{
        File tmp = Files.createTempFile("1nhann",null).toFile();
        FileOutputStream f = new FileOutputStream(tmp);
        f.write(content);
        f.close();
        return tmp.getAbsolutePath();
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

    public static byte[] readJar(String jarPath , String entryPath) throws Exception{
        File file = new File(jarPath);
        JarFile jarFile = new JarFile(file);
        JarEntry entry = jarFile.getJarEntry(entryPath);
        InputStream inputStream = jarFile.getInputStream(entry);
        byte[] c = ReadWrite.readAllBytesFromInputStream(inputStream);
        return c;
    }

    public static byte[] readJar(byte[] jarContent , String entryPath) throws Exception{
        String tmp = ReadWrite.writeTempFile(jarContent);
        byte[] c = readJar(tmp,entryPath);
        new File(tmp).delete();
        return c;
    }

    public static byte[] updateJar(byte[] jarContent, String entryPath , byte[] entryContent) throws Exception{
        String tmp = ReadWrite.writeTempFile(jarContent);
        updateJar(tmp,entryPath,entryContent);
        byte[] c = readFile(tmp);
        new File(tmp).delete();
        return c;
    }

    public static void updateJar(String jarPath , String entryPath , byte[] content) throws Exception{
        File tmp = Files.createTempDirectory("1nhann").toFile();
        String tmpPath = tmp.getPath();
        String path = tmpPath + "/" + entryPath;
        try {
            File j = new File(jarPath);
            String dir = path.substring(0,path.lastIndexOf('/'));
            File d = new File(dir);
            d.mkdirs();
            ReadWrite.writeFile(content,path);

            String[] cmd = new String[]{"jar","-fu",j.getAbsolutePath(),entryPath};
            ProcessBuilder processBuilder = new ProcessBuilder(cmd)
                .directory(tmp);
            Process p = processBuilder.start();

            while (p.isAlive()){

            }
            p.destroy();
        }catch (Exception e){
            throw new RuntimeException(e);
        }finally {
            new File(path).delete();
            tmp.delete();
        }
    }

}
