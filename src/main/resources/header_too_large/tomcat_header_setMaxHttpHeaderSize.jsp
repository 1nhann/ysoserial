<%@ page import="org.apache.catalina.core.StandardContext" %>
<%@ page import="org.apache.catalina.loader.WebappClassLoaderBase" %>
<%@ page import="java.lang.reflect.Field" %>
<%@ page import="org.apache.catalina.core.ApplicationContext" %>
<%@ page import="org.apache.catalina.core.StandardService" %>
<%@ page import="org.apache.catalina.connector.Connector" %>
<%
    String headerSize = "%headerSize%";
    int size = Integer.valueOf(headerSize);
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


    for (int i=0;i<connectors.length;i++) {
        org.apache.coyote.ProtocolHandler protocolHandler = connectors[i].getProtocolHandler();
        if (protocolHandler instanceof org.apache.coyote.http11.AbstractHttp11Protocol) {
            ((org.apache.coyote.http11.AbstractHttp11Protocol)protocolHandler).setMaxHttpHeaderSize(size);
        }
    }

%>
