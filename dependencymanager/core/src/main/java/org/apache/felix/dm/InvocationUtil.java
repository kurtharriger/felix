package org.apache.felix.dm;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;

public class InvocationUtil {
    private static final HashMap m_methodCache = new HashMap();
    
    public static Object invokeCallbackMethod(Object instance, String methodName, Class[][] signatures, Object[][] parameters) throws NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Class currentClazz = instance.getClass();
        while (currentClazz != null) {
            try {
                return invokeMethod(instance, currentClazz, methodName, signatures, parameters, false);
            }
            catch (NoSuchMethodException nsme) {
                // ignore
            }
            currentClazz = currentClazz.getSuperclass();
        }
        throw new NoSuchMethodException(methodName);
    }

    public static Object invokeMethod(Object object, Class clazz, String name, Class[][] signatures, Object[][] parameters, boolean isSuper) throws NoSuchMethodException, InvocationTargetException, IllegalArgumentException, IllegalAccessException {
        if (object == null) {
            throw new IllegalArgumentException("Instance cannot be null");
        }
        if (clazz == null) {
            throw new IllegalArgumentException("Class cannot be null");
        }
        
        // if we're talking to a proxy here, dig one level deeper to expose the
        // underlying invocation handler (we do the same for injecting instances)
        if (Proxy.isProxyClass(clazz)) {
            object = Proxy.getInvocationHandler(object);
            clazz = object.getClass();
        }
        
        Method m = null;
        for (int i = 0; i < signatures.length; i++) {
            Class[] signature = signatures[i];
            m = getDeclaredMethod(clazz, name, signature, isSuper);
            if (m != null) {
                return m.invoke(object, parameters[i]);
            }
        }
        throw new NoSuchMethodException(name);
    }
    
    private static Method getDeclaredMethod(Class clazz, String name, Class[] signature, boolean isSuper) {
        // first check our cache
        Key key = new Key(clazz, name, signature);
        Method m = null;
        synchronized (m_methodCache) {
            if (m_methodCache.containsKey(key)) {
                m = (Method) m_methodCache.get(key);
                return m;
            }
        }
        // then do a lookup
        try {
            m = clazz.getDeclaredMethod(name, signature);
            if (!(isSuper && Modifier.isPrivate(m.getModifiers()))) {
                m.setAccessible(true);
            }
        }
        catch (NoSuchMethodException e) {
            // ignore
        }
        synchronized (m_methodCache) {
            m_methodCache.put(key, m);
        }
        return m;
    }
    
    public static class Key {
        private final Class m_clazz;
        private final String m_name;
        private final Class[] m_signature;

        public Key(Class clazz, String name, Class[] signature) {
            m_clazz = clazz;
            m_name = name;
            m_signature = signature;
        }

        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((m_clazz == null) ? 0 : m_clazz.hashCode());
            result = prime * result + ((m_name == null) ? 0 : m_name.hashCode());
            result = prime * result + Arrays.hashCode(m_signature);
            return result;
        }

        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Key other = (Key) obj;
            if (m_clazz == null) {
                if (other.m_clazz != null)
                    return false;
            }
            else if (!m_clazz.equals(other.m_clazz))
                return false;
            if (m_name == null) {
                if (other.m_name != null)
                    return false;
            }
            else if (!m_name.equals(other.m_name))
                return false;
            if (!Arrays.equals(m_signature, other.m_signature))
                return false;
            return true;
        }
    }
}
