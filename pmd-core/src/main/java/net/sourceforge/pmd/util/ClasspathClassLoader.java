/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

/**
 * Create a ClassLoader which loads classes using a CLASSPATH like String. If
 * the String looks like a URL to a file (e.g. starts with <code>file://</code>)
 * the file will be read with each line representing an path on the classpath.
 *
 * @author Edwin Chan
 */
public class ClasspathClassLoader extends URLClassLoader {

    private static final Logger LOG = Logger.getLogger(ClasspathClassLoader.class.getName());
    
    static {
        registerAsParallelCapable();
    }

    public ClasspathClassLoader(List<File> files, ClassLoader parent) throws IOException {
        super(fileToURL(files), parent);
    }

    public ClasspathClassLoader(String classpath, ClassLoader parent) throws IOException {
        super(initURLs(classpath), parent);
    }

    private static URL[] fileToURL(List<File> files) throws IOException {

        List<URL> urlList = new ArrayList<>();

        for (File f : files) {
            urlList.add(f.toURI().toURL());
        }
        return urlList.toArray(new URL[0]);
    }

    private static URL[] initURLs(String classpath) throws IOException {
        if (classpath == null) {
            throw new IllegalArgumentException("classpath argument cannot be null");
        }
        final List<URL> urls = new ArrayList<>();
        if (classpath.startsWith("file://")) {
            // Treat as file URL
            addFileURLs(urls, new URL(classpath));
        } else {
            // Treat as classpath
            addClasspathURLs(urls, classpath);
        }
        return urls.toArray(new URL[0]);
    }

    private static void addClasspathURLs(final List<URL> urls, final String classpath) throws MalformedURLException {
        StringTokenizer toker = new StringTokenizer(classpath, File.pathSeparator);
        while (toker.hasMoreTokens()) {
            String token = toker.nextToken();
            LOG.log(Level.FINE, "Adding classpath entry: <{0}>", token);
            urls.add(createURLFromPath(token));
        }
    }

    private static void addFileURLs(List<URL> urls, URL fileURL) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(fileURL.openStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                LOG.log(Level.FINE, "Read classpath entry line: <{0}>", line);
                line = line.trim();
                if (line.length() > 0) {
                    LOG.log(Level.FINE, "Adding classpath entry: <{0}>", line);
                    urls.add(createURLFromPath(line));
                }
            }
        }
    }

    private static URL createURLFromPath(String path) throws MalformedURLException {
        File file = new File(path);
        return file.getAbsoluteFile().toURI().toURL();
    }

    @Override
    public String toString() {
        return new StringBuilder(getClass().getSimpleName())
                .append("[[")
                .append(StringUtils.join(getURLs(), ":"))
                .append("] parent: ").append(getParent()).append(']').toString();
    }

    /**
     * Tries to load the given class. Note: This classloader first tries to load the class
     * itself and if that fails, delegates to the parent class loader.
     * This is needed, if the project being analyzed by PMD uses the same dependencies as PMD itself
     * (e.g. apache commons) but in a different version. Trying to resolve the class first
     * within this classloader makes sure, to use the correct version for type resolution.
     */
    @Override
    protected Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            // First, check if the class has already been loaded
            Class<?> c = findLoadedClass(name);
            if (c == null) {

                // exception for org.w3c.dom.UserDataHandler, which is on the JDK classpath
                // and on jaxen
                // see https://github.com/pmd/pmd/issues/1074
                // we can directly return here, since PMD needs to run with java 7+, which
                // for sure has this interface on the JDK classpath
                if ("org.w3c.dom.UserDataHandler".equals(name)) {
                    return super.loadClass(name, resolve);
                }

                try {
                    // checking local
                    c = findClass(name);
                } catch (final ClassNotFoundException | SecurityException e) {
                    // checking parent
                    // This call to loadClass may eventually call findClass again, in case the parent doesn't find anything.
                    c = super.loadClass(name, resolve);
                }
            }

            if (resolve) {
                resolveClass(c);
            }
            return c;
        }
    }
}
