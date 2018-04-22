package com.tg.async.utils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

/**
 * Created by twogoods on 2018/4/20.
 */
public class ResourceScanner {


    public static InputStream getStreamFromFile(String file) throws IOException {
        return new FileInputStream(file);
    }

    public static InputStream getResourceAsStream(String resource) throws IOException {
        InputStream in = ResourceScanner.class.getClassLoader().getResourceAsStream(resource);
        if (in == null) {
            throw new IOException("Could not find resource " + resource);
        }
        return in;
    }

    public static Set<String> getXml(List<String> paths) throws Exception {
        Set<String> xmlFiles = new HashSet<>();
        for (String path : paths) {
            getByPackage(path, xmlFiles, ".xml", null);
        }
        return xmlFiles;
    }

    public static Set<String> getClasses(List<String> packages) throws Exception {
        Set<String> classes = new HashSet<>();
        for (String packageName : packages) {
            if (StringUtils.isEmpty(packageName)) {
                continue;
            }
            getByPackage(packageName.replace(".", File.separator), classes, ".class", new WholeClassNameHandler());
        }
        return classes;
    }

    private static void getByPackage(String packageName, Set<String> contents, String fileSuffix, TransformHandler handler) throws Exception {
        Enumeration<URL> baseURLs = ResourceScanner.class.getClassLoader().getResources(packageName);
        URL baseURL = null;
        while (baseURLs.hasMoreElements()) {
            baseURL = baseURLs.nextElement();
            if (baseURL != null) {
                String protocol = baseURL.getProtocol();
                String basePath = baseURL.getFile();
                //TODO xml和class 检测
                if ("jar".equals(protocol)) {
                    //实际运行中看到了这样的形式的jar目录 /BOOT-INF/classes!/com ,classes是一个目录,去掉后面的!,原因还没弄明白.
                    basePath = basePath.replace("classes!", "classes");
                    String[] paths = basePath.split("jar!/");
                    if (paths.length == 2) {
                        findFileInJar(paths[0] + "jar!/", paths[1], contents);
                    } else if (paths.length > 2) {
                        int index = basePath.lastIndexOf("jar!/") + "jar".length();
                        String lastJarPath = basePath.substring(0, index);
                        String packagepath = basePath.substring(index + "!/".length(), basePath.length());
                        findFileInJarWithinJar(lastJarPath, packagepath, contents);
                    }
                } else {
                    getFromFile(basePath, contents, fileSuffix, handler);
                }
            }
        }
    }

    private static void findFileInJar(String filePath, String packagePath, Set<String> classes) throws IOException {
        URL url = new URL("jar", null, 0, filePath);
        URLConnection con = url.openConnection();
        if (con instanceof JarURLConnection) {
            JarURLConnection result = (JarURLConnection) con;
            JarFile jarFile = result.getJarFile();
            for (final Enumeration<JarEntry> enumJar = jarFile.entries(); enumJar.hasMoreElements(); ) {
                JarEntry entry = enumJar.nextElement();
                filterClass(entry.getName(), packagePath, classes);
            }
            jarFile.close();
        }
    }

    private static void findFileInJarWithinJar(String filePath, String packagePath, Set<String> classes) throws IOException {
        URL url = new URL("jar", null, 0, filePath);
        URLConnection con = url.openConnection();
        if (con instanceof JarURLConnection) {
            JarURLConnection jarURLConnection = (JarURLConnection) con;
            JarInputStream jarInputStream = new JarInputStream(jarURLConnection.getInputStream());
            JarEntry entry;
            while ((entry = jarInputStream.getNextJarEntry()) != null) {
                filterClass(entry.getName(), packagePath, classes);
            }
            IOUtils.closeQuietly(jarInputStream);
        }
    }

    private static void filterClass(String filePath, String packageName, Set<String> classes) {
        if (filePath.startsWith(packageName) && filePath.endsWith(".class")) {
            classes.add(getWholeClassName(filePath));
        }
    }

    private static void getFromFile(String filePath, Set<String> contents, String fileSuffix, TransformHandler handler) throws Exception {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("check config, file or directory not found, path is " + filePath);
        }
        for (String fileName : file.list()) {
            String childFilePath = filePath;
            if (filePath.endsWith("/")) {
                childFilePath = childFilePath + fileName;
            } else {
                childFilePath = childFilePath + File.separator + fileName;
            }
            if (new File(childFilePath).isDirectory()) {
                getFromFile(childFilePath, contents, fileSuffix, handler);
            } else if (fileName.endsWith(fileSuffix)) {
                if (handler != null) {
                    contents.add(handler.handle(childFilePath));
                } else {
                    contents.add(childFilePath);
                }
            }
        }
    }


    private static String getWholeClassName(String filePath) {
        if (filePath.indexOf("classes/") != -1) {
            return filePath.substring(filePath.indexOf("classes/") + "classes/".length(),
                    filePath.indexOf(".class")).replaceAll("/", ".");
        }
        return filePath.substring(0, filePath.indexOf(".class")).replaceAll("/", ".");
    }


    interface TransformHandler {
        String handle(String s);
    }

    static class WholeClassNameHandler implements TransformHandler {
        @Override
        public String handle(String s) {
            if (s.indexOf("classes/") != -1) {
                return s.substring(s.indexOf("classes/") + "classes/".length(),
                        s.indexOf(".class")).replaceAll("/", ".");
            }
            return s.substring(0, s.indexOf(".class")).replaceAll("/", ".");
        }
    }
}
