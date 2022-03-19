/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.internal.util;

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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.util.AssertionUtil;

/**
 * Create a ClassLoader which loads classes using a CLASSPATH like String. If
 * the String looks like a URL to a file (e.g. starts with <code>file://</code>)
 * the file will be read with each line representing an path on the classpath.
 *
 * @author Edwin Chan
 */
public class ClasspathClassLoader extends URLClassLoader {

    private static final Logger LOG = LoggerFactory.getLogger(ClasspathClassLoader.class);

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

    private static URL[] initURLs(String classpath) {
        AssertionUtil.requireParamNotNull("classpath", classpath);
        final List<URL> urls = new ArrayList<>();
        try {
            if (classpath.startsWith("file:")) {
                // Treat as file URL
                addFileURLs(urls, new URL(classpath));
            } else {
                // Treat as classpath
                addClasspathURLs(urls, classpath);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot prepend classpath " + classpath + "\n" + e.getMessage(), e);
        }
        return urls.toArray(new URL[0]);
    }

    private static void addClasspathURLs(final List<URL> urls, final String classpath) throws MalformedURLException {
        StringTokenizer toker = new StringTokenizer(classpath, File.pathSeparator);
        while (toker.hasMoreTokens()) {
            String token = toker.nextToken();
            LOG.debug("Adding classpath entry: <{}>", token);
            urls.add(createURLFromPath(token));
        }
    }

    private static void addFileURLs(List<URL> urls, URL fileURL) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(fileURL.openStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                LOG.debug("Read classpath entry line: <{}>", line);
                line = line.trim();
                if (line.length() > 0 && line.charAt(0) != '#') {
                    LOG.debug("Adding classpath entry: <{}>", line);
                    urls.add(createURLFromPath(line));
                }
            }
        }
    }

    private static URL createURLFromPath(String path) throws MalformedURLException {
        File file = new File(path);
        return file.getAbsoluteFile().toURI().normalize().toURL();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()
            + "[["
            + StringUtils.join(getURLs(), ":")
            + "] parent: " + getParent() + ']';
    }

    @Override
    protected Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            // First, check if the class has already been loaded
            Class<?> c = findLoadedClass(name);
            if (c == null) {
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
