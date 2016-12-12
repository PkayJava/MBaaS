package com.angkorteam.mbaas.server.bean;

import org.apache.wicket.Application;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.application.IClassResolver;
import org.apache.wicket.util.collections.UrlExternalFormComparator;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by socheat on 10/30/16.
 */
public class ClassResolver implements IClassResolver {

    private final ConcurrentMap<String, WeakReference<Class<?>>> classes = new ConcurrentHashMap<>();

    private GroovyClassLoader classLoader;

    public ClassResolver(GroovyClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    @Override
    public Class<?> resolveClass(String className) throws ClassNotFoundException {
        Class<?> clazz = null;

        switch (className) {
            case "byte":
                clazz = byte.class;
                break;
            case "short":
                clazz = short.class;
                break;
            case "int":
                clazz = int.class;
                break;
            case "long":
                clazz = long.class;
                break;
            case "float":
                clazz = float.class;
                break;
            case "double":
                clazz = double.class;
                break;
            case "boolean":
                clazz = boolean.class;
                break;
            case "char":
                clazz = char.class;
                break;
            default:
                clazz = this.classLoader.loadClass(className);
                break;
        }
        return clazz;
    }

    @Override
    public Iterator<URL> getResources(final String name) {
        Set<URL> resultSet = new TreeSet<>(new UrlExternalFormComparator());

        try {
            // Try the classloader for the wicket jar/bundle
            Enumeration<URL> resources = Application.class.getClassLoader().getResources(name);
            loadResources(resources, resultSet);

            // Try the classloader for the user's application jar/bundle
            resources = Application.get().getClass().getClassLoader().getResources(name);
            loadResources(resources, resultSet);

            // Try the context class loader
            resources = getClassLoader().getResources(name);
            loadResources(resources, resultSet);
        } catch (Exception e) {
            throw new WicketRuntimeException(e);
        }

        return resultSet.iterator();
    }

    /**
     * @param resources
     * @param loadedResources
     */
    private void loadResources(Enumeration<URL> resources, Set<URL> loadedResources) {
        if (resources != null) {
            while (resources.hasMoreElements()) {
                final URL url = resources.nextElement();
                loadedResources.add(url);
            }
        }
    }
}
