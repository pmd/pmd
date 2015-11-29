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

import org.apache.commons.io.IOUtils;

/**
 * Create a ClassLoader which loads classes using a CLASSPATH like String.
 * If the String looks like a URL to a file (e.g. starts with <code>file://</code>)
 * the file will be read with each line representing an path on the classpath.
 *
 * @author Edwin Chan
 */
public class ClasspathClassLoader extends URLClassLoader {

    private static final Logger LOG = Logger.getLogger(ClasspathClassLoader.class.getName());

    public ClasspathClassLoader(String classpath, ClassLoader parent) throws IOException {
        super(initURLs(classpath), parent);
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
        return urls.toArray(new URL[urls.size()]);
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
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(fileURL.openStream()));
            String line;
            while ((line = in.readLine()) != null) {
                LOG.log(Level.FINE, "Read classpath entry line: <{0}>", line);
                line = line.trim();
                if (line.length() > 0) {
                    LOG.log(Level.FINE, "Adding classpath entry: <{0}>", line);
                    urls.add(createURLFromPath(line));
                }
            }
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    private static URL createURLFromPath(String path) throws MalformedURLException {
        File file = new File(path);
        return file.getAbsoluteFile().toURI().toURL();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName());
        sb.append("[[");
        StringUtil.asStringOn(sb, getURLs(), ":");
        sb.append("] parent: ")
          .append(getParent())
          .append(']');

        return sb.toString();
    }
}
