<%@ page import="java.nio.file.Files" %>
<%@ page import="java.nio.file.Paths" %><%
    try{
        Files.delete(Paths.get("%path%"));
    }catch (Exception e){
        e.printStackTrace();
    }
%>
