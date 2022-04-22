<%@ page import="org.apache.catalina.core.StandardContext" %>
<%@ page import="org.apache.catalina.loader.WebappClassLoaderBase" %>
<%@ page import="java.lang.reflect.Field" %>
<%@ page import="org.apache.catalina.core.ApplicationContext" %>
<%@ page import="org.apache.catalina.core.StandardService" %>
<%@ page import="org.apache.catalina.connector.Connector" %>
<%@ page import="org.apache.coyote.ProtocolHandler" %>
<%@ page import="org.apache.coyote.AbstractProtocol" %>
<%@ page import="org.apache.tomcat.util.net.AbstractEndpoint" %>
<%@ page import="org.apache.coyote.RequestGroupInfo" %>
<%@ page import="java.util.List" %>
<%@ page import="org.apache.catalina.connector.ResponseFacade" %>
<%@ page import="java.io.Writer" %>
<%@ page import="org.apache.catalina.connector.Response" %>
<%@ page import="org.apache.catalina.connector.Request" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    //获取Tomcat CloassLoader context
    WebappClassLoaderBase webappClassLoaderBase = (WebappClassLoaderBase) Thread.currentThread().getContextClassLoader();
    StandardContext standardContext = (StandardContext) webappClassLoaderBase.getResources().getContext();
    //获取standardContext的context
    Field context = Class.forName("org.apache.catalina.core.StandardContext").getDeclaredField("context");
    context.setAccessible(true);
    ApplicationContext applicationContext = (ApplicationContext)context.get(standardContext);
    //获取ApplicationContext的service
    Field service = Class.forName("org.apache.catalina.core.ApplicationContext").getDeclaredField("service");
    service.setAccessible(true);
    StandardService standardService = (StandardService) service.get(applicationContext);
    //获取StandardService的connectors
    Field connectorsField = Class.forName("org.apache.catalina.core.StandardService").getDeclaredField("connectors");
    connectorsField.setAccessible(true);
    Connector[] connectors = (Connector[])connectorsField.get(standardService);
    //获取AbstractProtocol的handler
    ProtocolHandler protocolHandler = connectors[0].getProtocolHandler();
    Field handlerField = AbstractProtocol.class.getDeclaredField("handler");
    handlerField.setAccessible(true);
    AbstractEndpoint.Handler handler = (AbstractEndpoint.Handler) handlerField.get(protocolHandler);
    //获取内部类ConnectionHandler的global
    Field globalField = Class.forName("org.apache.coyote.AbstractProtocol$ConnectionHandler").getDeclaredField("global");
    globalField.setAccessible(true);
    RequestGroupInfo global = (RequestGroupInfo) globalField.get(handler);
    //获取RequestGroupInfo的processors
    Field processors = Class.forName("org.apache.coyote.RequestGroupInfo").getDeclaredField("processors");
    processors.setAccessible(true);
    List RequestInfolist = (List)processors.get(global);
    //获取Response，并做输出处理
    Field req = Class.forName("org.apache.coyote.RequestInfo").getDeclaredField("req");
    req.setAccessible(true);
    for(int i = 0; i < RequestInfolist.size(); i++){//遍历
        org.apache.coyote.Request request1 = (org.apache.coyote.Request )req.get(RequestInfolist.get(i));//获取request
        Request request2 = (Request)request1.getNote(1);//获取catalina.connector.Request类型的Request

        Response response2 = request2.getResponse();
        Writer w = response2.getWriter();//获取Writer
        Field responseField = ResponseFacade.class.getDeclaredField("response");
        responseField.setAccessible(true);
        Field usingWriter = Response.class.getDeclaredField("usingWriter");
        usingWriter.setAccessible(true);
        usingWriter.set(response2, Boolean.FALSE);//初始化
        w.write("bitch");
        w.flush();//刷新
    }
%>
