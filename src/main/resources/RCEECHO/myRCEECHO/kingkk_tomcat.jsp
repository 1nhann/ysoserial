<%@ page import="java.lang.reflect.Field" %>
<%@ page import="java.lang.reflect.Modifier" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Field modifers = Field.class.getDeclaredField("modifiers");
    modifers.setAccessible(true);

    Field WRAP_SAME_OBJECT = Class.forName("org.apache.catalina.core.ApplicationDispatcher").getDeclaredField("WRAP_SAME_OBJECT");
    WRAP_SAME_OBJECT.setAccessible(true);
    modifers.setInt(WRAP_SAME_OBJECT, WRAP_SAME_OBJECT.getModifiers() & ~Modifier.FINAL);

    Field lastServicedRequest = Class.forName("org.apache.catalina.core.ApplicationFilterChain").getDeclaredField("lastServicedRequest");
    lastServicedRequest.setAccessible(true);
    modifers.setInt(lastServicedRequest, lastServicedRequest.getModifiers() & ~Modifier.FINAL);

    Field lastServicedResponse = Class.forName("org.apache.catalina.core.ApplicationFilterChain").getDeclaredField("lastServicedResponse");
    lastServicedResponse.setAccessible(true);
    modifers.setInt(lastServicedResponse, lastServicedResponse.getModifiers() & ~Modifier.FINAL);


    ThreadLocal _resp = (ThreadLocal) lastServicedResponse.get(null);
    ThreadLocal _req = (ThreadLocal) lastServicedRequest.get(null);
    boolean _WRAP_SAME_OBJECT = WRAP_SAME_OBJECT.getBoolean(null);

    if(!_WRAP_SAME_OBJECT || _req == null || _resp == null){
        WRAP_SAME_OBJECT.set(null,Boolean.TRUE);
        lastServicedRequest.set(null,new ThreadLocal());
        lastServicedResponse.set(null,new ThreadLocal());
    }else{
        ServletResponse resp = (ServletResponse) _resp.get();
        ServletRequest req = (ServletRequest) _req.get();
        resp.getWriter().write("bitch");
    }
%>
