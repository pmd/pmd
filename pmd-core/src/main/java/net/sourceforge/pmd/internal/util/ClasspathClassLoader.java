/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.internal.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    String javaHome;

    private FileSystem fileSystem;
    private Map<String, Set<String>> packagesDirsToModules;

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
        for (URL url : fileToURL(files)) {
            addURL(url);
        }
    }

    public ClasspathClassLoader(String classpath, ClassLoader parent) throws IOException {
        super(new URL[0], parent);
        for (URL url : initURLs(classpath)) {
            addURL(url);
        }
    }

    private List<URL> fileToURL(List<File> files) throws IOException {
        List<URL> urlList = new ArrayList<>();
        for (File f : files) {
            urlList.add(createURLFromPath(f.getAbsolutePath()));
        }
        return urlList;
    }

    private List<URL> initURLs(String classpath) {
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
        return urls;
    }

    private void addClasspathURLs(final List<URL> urls, final String classpath) throws MalformedURLException {
        StringTokenizer toker = new StringTokenizer(classpath, File.pathSeparator);
        while (toker.hasMoreTokens()) {
            String token = toker.nextToken();
            LOG.debug("Adding classpath entry: <{}>", token);
            urls.add(createURLFromPath(token));
        }
    }

    private void addFileURLs(List<URL> urls, URL fileURL) throws IOException {
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

    private URL createURLFromPath(String path) throws MalformedURLException {
        Path filePath = Paths.get(path).toAbsolutePath();
        if (filePath.endsWith(Paths.get("lib", "jrt-fs.jar"))) {
            initializeJrtFilesystem(filePath);
            // don't add jrt-fs.jar to the normal aux classpath
            return null;
        }

        return filePath.toUri().normalize().toURL();
    }

    /**
     * Initializes a Java Runtime Filesystem that will be used to load class files.
     * This allows end users to provide in the aux classpath another Java Runtime version
     * than the one used for executing PMD.
     *
     * @param filePath path to the file "lib/jrt-fs.jar" inside the java installation directory.
     * @see <a href="https://openjdk.org/jeps/220">JEP 220: Modular Run-Time Images</a>
     */
    private void initializeJrtFilesystem(Path filePath) {
        try {
            LOG.debug("Detected Java Runtime Filesystem Provider in {}", filePath);

            if (fileSystem != null) {
                throw new IllegalStateException("There is already a jrt filesystem. Do you have multiple jrt-fs.jar files on the classpath?");
            }

            if (filePath.getNameCount() < 2) {
                throw new IllegalArgumentException("Can't determine java home from " + filePath + " - please provide a complete path.");
            }

            try (URLClassLoader loader = new URLClassLoader(new URL[] { filePath.toUri().toURL() })) {
                Map<String, String> env = new HashMap<>();
                // note: providing java.home here is crucial, so that the correct runtime image is loaded.
                // the class loader is only used to provide an implementation of JrtFileSystemProvider, if the current
                // Java runtime doesn't provide one (e.g. if running in Java 8).
                javaHome = filePath.getParent().getParent().toString();
                env.put("java.home", javaHome);
                LOG.debug("Creating jrt-fs with env {}", env);
                fileSystem = FileSystems.newFileSystem(URI.create("jrt:/"), env, loader);
            }

            packagesDirsToModules = new HashMap<>();
            Path packages = fileSystem.getPath("packages");
            try (Stream<Path> packagesStream = Files.list(packages)) {
                packagesStream.forEach(p -> {
                    String packageName = p.getFileName().toString().replace('.', '/');
                    try (Stream<Path> modulesStream = Files.list(p)) {
                        Set<String> modules = modulesStream
                                .map(Path::getFileName)
                                .map(Path::toString)
                                .collect(Collectors.toSet());
                        packagesDirsToModules.put(packageName, modules);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()
            + "[["
            + StringUtils.join(getURLs(), ":")
            + "] jrt-fs: " + javaHome + " parent: " + getParent() + ']';
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        // always first search in jrt-fs, if available
        // note: we can't override just getResource(String) and return a jrt:/-URL, because the URL itself
        // won't be connected to the correct JrtFileSystem and would just load using the system classloader.
        if (fileSystem != null) {
            int lastSlash = name.lastIndexOf('/');
            String packageName = name.substring(0, Math.max(lastSlash, 0));
            Set<String> moduleNames = packagesDirsToModules.get(packageName);
            if (moduleNames != null) {
                LOG.trace("Trying to find {} in jrt-fs with packageName={} and modules={}",
                        name, packageName, moduleNames);

                for (String moduleCandidate : moduleNames) {
                    Path candidate = fileSystem.getPath("modules", moduleCandidate, name);
                    if (Files.exists(candidate)) {
                        LOG.trace("Found {}", candidate);
                        try {
                            // Note: The input streams from JrtFileSystem are ByteArrayInputStreams and do not
                            // need to be closed - we don't need to track these. The filesystem itself needs to be closed at the end.
                            // See https://github.com/openjdk/jdk/blob/970cd202049f592946f9c1004ea92dbd58abf6fb/src/java.base/share/classes/jdk/internal/jrtfs/JrtFileSystem.java#L334
                            return Files.newInputStream(candidate);
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    }
                }
            }
        }

        // search in the other jars of the aux classpath.
        // this will call this.getResource, which will do a child-first search, see below.
        return super.getResourceAsStream(name);
    }

    @Override
    public URL getResource(String name) {
        // Override to make it child-first. This is the method used by
        // pmd-java's type resolution to fetch classes, instead of loadClass.
        Objects.requireNonNull(name);

        URL url = findResource(name);
        if (url == null) {
            // note this will actually call back into this.findResource, but
            // we can't avoid this as the super implementation uses JDK internal
            // stuff that we can't copy down here.
            return super.getResource(name);
        }
        return url;
    }

    @Override
    protected Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
        throw new IllegalStateException("This class loader shouldn't be used to load classes");
    }

    @Override
    public void close() throws IOException {
        if (fileSystem != null) {
            fileSystem.close();
            // jrt created an own classloader to load the JrtFileSystemProvider class out of the
            // jrt-fs.jar. This needs to be closed manually.
            ClassLoader classLoader = fileSystem.getClass().getClassLoader();
            if (classLoader instanceof URLClassLoader) {
                ((URLClassLoader) classLoader).close();
            }
            packagesDirsToModules = null;
            fileSystem = null;
        }
        super.close();
    }
}
