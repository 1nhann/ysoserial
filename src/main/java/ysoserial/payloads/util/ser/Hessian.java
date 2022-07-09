package ysoserial.payloads.util.ser;

import com.caucho.hessian.io.*;
import com.caucho.hessian.io.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class Hessian {

    public static byte[] serialize(Object o) throws Exception{
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        AbstractHessianOutput out = new HessianOutput(bos);
        NoWriteReplaceSerializerFactory sf = new NoWriteReplaceSerializerFactory();
        sf.setAllowNonSerializable(true);
        out.setSerializerFactory(sf);
        out.writeObject(o);
        out.close();
        return bos.toByteArray();
    }

    public static Object deserialize(byte[] bytes) throws Exception{
        ByteArrayInputStream bai = new ByteArrayInputStream(bytes);
        HessianInput input = new HessianInput(bai);
        Object o = input.readObject();
        return o;
    }

    public static class NoWriteReplaceSerializerFactory extends SerializerFactory {

        /**
         * {@inheritDoc}
         *
         * @see com.caucho.hessian.io.SerializerFactory#getObjectSerializer(Class)
         */
        @Override
        public com.caucho.hessian.io.Serializer getObjectSerializer (Class<?> cl ) throws HessianProtocolException {
            return super.getObjectSerializer(cl);
        }


        /**
         * {@inheritDoc}
         *
         * @see com.caucho.hessian.io.SerializerFactory#getSerializer(Class)
         */
        @Override
        public com.caucho.hessian.io.Serializer getSerializer (Class cl ) throws HessianProtocolException {
            Serializer serializer = super.getSerializer(cl);

            if ( serializer instanceof WriteReplaceSerializer ) {
                return UnsafeSerializer.create(cl);
            }
            return serializer;
        }

    }
}
