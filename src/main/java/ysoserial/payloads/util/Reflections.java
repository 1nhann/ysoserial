package ysoserial.payloads.util;

import com.nqzero.permit.Permit;
import sun.reflect.ReflectionFactory;

import java.lang.reflect.*;

public class Reflections {
    public static void setAccessible(AccessibleObject member) {
        String versionStr = System.getProperty("java.version");
        int javaVersion = Integer.parseInt(versionStr.split("\\.")[0]);
        if (javaVersion < 12) {
            // quiet runtime warnings from JDK9+
            Permit.setAccessible(member);
        } else {
            // not possible to quiet runtime warnings anymore...
            // see https://bugs.openjdk.java.net/browse/JDK-8210522
            // to understand impact on Permit (i.e. it does not work
            // anymore with Java >= 12)
            member.setAccessible(true);
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

    public static void setFieldValue ( final Object obj, final String fieldName, final Object value ) throws Exception {
        final Field field = getField(obj.getClass(), fieldName);
        Field modifersField = Field.class.getDeclaredField("modifiers");
        modifersField.setAccessible(true);
        modifersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(obj, value);
    }
    public static void setStaticFieldValue (final Class clazz, final String fieldName, final Object value ) throws Exception {
        final Field field = getField(clazz, fieldName);
        Field modifersField = Field.class.getDeclaredField("modifiers");
        modifersField.setAccessible(true);
        modifersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(null, value);
    }
//    public static void setFinalFieldValue ( final Object obj, final String fieldName, final Object value ) throws Exception {
//        Field f = obj.getClass().getDeclaredField(fieldName);
//        f.setAccessible(true);
//        Field modifersField = Field.class.getDeclaredField("modifiers");
//        modifersField.setAccessible(true);
//        modifersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
//        f.set(obj,value);
//    }
//    public static void setStaticFinalFieldValue (final Class clazz, final String fieldName, final Object value ) throws Exception {
//        Field f = clazz.getDeclaredField(fieldName);
//        f.setAccessible(true);
//        Field modifersField = Field.class.getDeclaredField("modifiers");
//        modifersField.setAccessible(true);
//        modifersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
//        f.set(null,value);
//    }


    public static Object getFieldValue ( final Object obj, final String fieldName ) throws Exception {
        final Field field = getField(obj.getClass(), fieldName);
        return field.get(obj);
    }


    public static Constructor<?> getFirstCtor (final String name ) throws Exception {
        final Constructor<?> ctor = Class.forName(name).getDeclaredConstructors()[ 0 ];
        ctor.setAccessible(true);
        return ctor;
    }


    public static <T> T createWithoutConstructor ( Class<T> classToInstantiate )
            throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        return createWithConstructor(classToInstantiate, Object.class, new Class[0], new Object[0]);
    }


    @SuppressWarnings ( {
            "unchecked"
    } )
    public static <T> T createWithConstructor ( Class<T> classToInstantiate, Class<? super T> constructorClass, Class<?>[] consArgTypes,
                                                Object[] consArgs ) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Constructor<? super T> objCons = constructorClass.getDeclaredConstructor(consArgTypes);
        objCons.setAccessible(true);
        Constructor<?> sc = ReflectionFactory.getReflectionFactory().newConstructorForSerialization(classToInstantiate, objCons);
        sc.setAccessible(true);
        return (T) sc.newInstance(consArgs);
    }

}
