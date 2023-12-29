package com.cxy.rpc.core.extension;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ExtensionLoader<T> {

    /**
     * 服务的存储目录
     */
    private static final String SERVICES_DIRECTORY = "META-INF/extensions/";

    /**
     * 扩展类加载器缓存，key - class，val - 对应的扩展器类
     */
    private static final Map<Class<?>, ExtensionLoader<?>> EXTENSION_LOADERS = new ConcurrentHashMap<>();

    /**
     * 存储接口实现类的实例，key - impClass，val - object 实例对象
     */
    private static final Map<Class<?>, Object> EXTENSION_INSTANCES = new ConcurrentHashMap<>();

    /**
     * 拓展类加载器对应的接口类型
     */
    private final Class<?> type;

    /**
     * 扩展类工厂（实现依赖注入）
     */
    private final ExtensionFactory objectFactory;

    /**
     * 缓存的实例
     */
    private final Map<String, Holder<Object>> cachedInstances = new ConcurrentHashMap<>();

    private final Holder<Object> cachedAdaptiveInstance = new Holder<>();

    /**
     * 缓存的类型（当前接口的所有 Extension 类型，对应文件内的：String - key，implClass - value）
     */
    private final Holder<Map<String, Class<?>>> cachedClasses = new Holder<>();

    public ExtensionLoader(Class<?> type) {
        this.type = type;
        objectFactory = null;
    }

    @SuppressWarnings("unchecked")
    public static <T> ExtensionLoader<T> getExtensionLoader(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("Extension type is null");
        }
        if (!type.isInterface()) {
            throw new IllegalArgumentException(String.format("Extension type (%s) is not an interface!", type));
        }
        if (type.getAnnotation(SPI.class) == null) {
            throw new IllegalArgumentException(String.format("Extension type (%s) is not an extension, " + "because it is NOT annotated with @%s!", type, SPI.class.getSimpleName()));
        }
        ExtensionLoader<T> loader = (ExtensionLoader<T>) EXTENSION_LOADERS.get(type);
        if (loader == null) {
            EXTENSION_LOADERS.putIfAbsent(type, new ExtensionLoader<>(type));
            loader = (ExtensionLoader<T>) EXTENSION_LOADERS.get(type);
        }
        return loader;
    }

    @SuppressWarnings("unchecked")
    public T getExtension(String name){
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Extension name is null.");
        }
        Holder<Object> holder = cachedInstances.get(name);
        if (holder == null) {
            cachedInstances.putIfAbsent(name, new Holder<>());
            holder = cachedInstances.get(name);
        }
        Object instance = holder.get();
        if (instance == null) {
            synchronized (holder) {
                instance = holder.get();
                if (instance == null) {
                    instance = createExtension(name);
                    holder.set(instance);
                }
            }
        }
        return (T) instance;
    }

    @SuppressWarnings("unchecked")
    public T createExtension(String name) {
        Class<?> clazz = getExtensionClasses().get(name);
        if (clazz == null) {
            throw new IllegalArgumentException("No such extension name " + name);
        }
        T instance = (T) EXTENSION_INSTANCES.get(clazz);
        if (instance == null) {
            try {
                EXTENSION_INSTANCES.putIfAbsent(clazz, clazz.newInstance());
                instance = (T) EXTENSION_INSTANCES.get(clazz);
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

    /**
     * 获取当前接口的所有扩展实现类类型
     */
    private Map<String, Class<?>> getExtensionClasses() {
        Map<String, Class<?>> classes = cachedClasses.get();
        if (classes == null) {
            synchronized (cachedClasses) {
                classes = cachedClasses.get();
                if (classes == null) {
                    classes = new HashMap<>();
                    loadDirectory(classes);
                    cachedClasses.set(classes);
                }
            }
        }
        return classes;
    }

    private void loadDirectory(Map<String, Class<?>> extensionClasses) {
        String filename = SERVICES_DIRECTORY + type.getName();
        try {
            Enumeration<URL> urls;
            // 获取ExtensionLoader的类加载器
            ClassLoader classLoader = ExtensionLoader.class.getClassLoader();
            urls = classLoader.getResources(filename);
            if (urls != null) {
                while (urls.hasMoreElements()) {
                    URL resourceUrl = urls.nextElement();
                    loadResource(extensionClasses, classLoader, resourceUrl);
                }
            }
        }catch (IOException e) {
            log.debug("Failed to load directory.", e);
        }
    }

    private void loadResource(Map<String, Class<?>> extensionClasses, ClassLoader classLoader, URL resourceUrl) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resourceUrl.openStream(), StandardCharsets.UTF_8))) {
            String line;
            while((line = reader.readLine()) != null){
                final int ci = line.indexOf("#");
                if (ci >= 0) {
                    line = line.substring(0, ci);
                }
                line = line.trim();
                if (!line.isEmpty()) {
                    try {
                        final int i = line.indexOf("=");
                        String name = line.substring(0, i).trim();
                        String className = line.substring(i + 1).trim();
                        if (!name.isEmpty() && !className.isEmpty()) {
                            Class<?> clazz = classLoader.loadClass(className);
                            extensionClasses.put(name, clazz);
                        }
                    } catch (ClassNotFoundException e) {
                        log.error("Failed to load extension class (interface: " + type + ", class line: " + line + ") in "
                                + resourceUrl + ", cause: " + e.getMessage(), e);
                    }
                }
            }
        } catch (IOException e) {
            log.error("Exception occurred when loading extension class (interface: " +
                    type + ", class file: " + resourceUrl + ") in " + resourceUrl, e);
            throw new RuntimeException(e);
        }
    }
}
