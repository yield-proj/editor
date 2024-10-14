package com.xebisco.yieldengine.jarmng;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class JarMng {
    private final ClassLoader loader;
    private final File jarFile;

    public JarMng(File jarFile) {
        this.jarFile = jarFile;
        try {
            loader = new URLClassLoader(new URL[]{jarFile.toURI().toURL()});
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public Class<?> getClassForName(String className) throws ClassNotFoundException {
        return loader.loadClass(className);
    }

    public <T> T invokeMethod(Object o, String methodName, Class<T> returnType, Class<?>[] parameterTypes, Object[] parameters) {
        try {
            return returnType.cast(o.getClass().getDeclaredMethod(methodName, parameterTypes).invoke(o, parameters));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassCastException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T invokeMethod(Object o, String methodName, Class<T> returnType) {
        return invokeMethod(o, methodName, returnType, null, null);
    }
}
