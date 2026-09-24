/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.internal.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.util.AuxClasspathLoader;

/**
 * Create a ClassLoader which loads classes using a CLASSPATH like String. If
 * the String looks like a URL to a file (e.g. starts with <code>file://</code>)
 * the file will be read with each line representing an path on the classpath.
 *
 * @author Edwin Chan
 * @deprecated Since 7.27.0. Using ClassLoaders directly is discouraged, as it is unclear, if and
 *     when the ClassLoaders should be closed to release their resources. By just configuring the auxClasspath
 *     via {@link net.sourceforge.pmd.PMDConfiguration#setAuxClasspath(String)}, PMD internally can deal with that.
 */
@Deprecated
public class ClasspathClassLoader extends URLClassLoader {
    private final AuxClasspathLoader loader;

    static {
        registerAsParallelCapable();

        // Disable caching for jar files to prevent issues like #4899
        try {
            // Uses a pseudo URL to be able to call URLConnection#setDefaultUseCaches
            // with Java9+ there is a static method for that per protocol:
            // https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/net/URLConnection.html#setDefaultUseCaches(java.lang.String,boolean)
            URI.create("jar:file:file.jar!/").toURL().openConnection().setDefaultUseCaches(false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ClasspathClassLoader(List<File> files, ClassLoader parent) throws IOException {
        super(new URL[0], parent);
        String rawClasspath = files.stream()
                .map(File::toString)
                .collect(Collectors.joining(File.pathSeparator));
        this.loader = AuxClasspathLoader.create(rawClasspath);
    }

    public ClasspathClassLoader(String rawClasspath, ClassLoader parent) throws IOException {
        super(new URL[0], parent);
        this.loader = AuxClasspathLoader.create(rawClasspath);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()
            + "[["
            + StringUtils.join(getURLs(), ":")
            + "] loader: " + loader + " parent: " + getParent() + ']';
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        // first delegate to our own AuxClasspathLoader
        InputStream resource = loader.findResource(name);
        if (resource != null) {
            return resource;
        }
        // delegate to parents
        return super.getResourceAsStream(name);
    }

    @Override
    protected Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
        throw new IllegalStateException("This class loader shouldn't be used to load classes");
    }

    @Override
    public void close() throws IOException {
        loader.close();
        super.close();
    }
}
