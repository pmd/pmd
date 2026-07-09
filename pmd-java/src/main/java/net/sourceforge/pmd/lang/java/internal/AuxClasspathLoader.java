/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.internal;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.internal.util.IOUtil;
import net.sourceforge.pmd.lang.java.symbols.internal.asm.Classpath;

/**
 * This class allows to load resources from a given classpath. Unlike a real classloader
 * like {@link URLClassLoader}, the jar files on the classpath are not opened with JarFile and
 * verified, but just with {@link ZipFile}. This should save memory.
 *
 * <p>This classpath loader also supports loading platform classes (e.g. {@code java.lang}) from
 * the jrt-fs filesystem. All zip files and the jrt-fs are kept open, until this classpath loader
 * is closed.
 *
 * @since 7.27.0
 */
public class AuxClasspathLoader implements Classpath, AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(AuxClasspathLoader.class);

    private static final Object LOCK = new Object();
    private static AuxClasspathLoader previousAuxClasspathLoader;

    private final String rawAuxClasspath;
    private volatile boolean closeRequested;
    private final List<Path> auxClasspath = new ArrayList<>();
    private final ConcurrentMap<Path, ZipFile> zipFiles = new ConcurrentHashMap<>();

    String javaHome;

    private FileSystem fileSystem;
    private Map<String, Set<String>> packagesDirsToModules;

    AuxClasspathLoader(String rawAuxClasspath) {
        LOG.debug("Creating new AuxClasspathLoader for {}", rawAuxClasspath);
        this.rawAuxClasspath = rawAuxClasspath;
        expandAuxClasspath(rawAuxClasspath);

        Iterator<Path> iterator = auxClasspath.iterator();
        while (iterator.hasNext()) {
            Path filePath = iterator.next().toAbsolutePath();
            if (filePath.endsWith(Paths.get("lib", "jrt-fs.jar"))) {
                initializeJrtFilesystem(filePath);
                // don't add jrt-fs.jar to the normal aux classpath
                iterator.remove();
            }
        }
    }

    public static AuxClasspathLoader create(String rawAuxClasspath, boolean reuse) {
        if (!reuse) {
            return new AuxClasspathLoader(rawAuxClasspath);
        }

        synchronized (LOCK) {
            if (previousAuxClasspathLoader != null) {
                if (previousAuxClasspathLoader.rawAuxClasspath.equals(rawAuxClasspath)) {
                    LOG.debug("Reusing previously cached AuxClasspathLoader");
                } else {
                    LOG.debug("Can't reuse previous AuxClasspathLoader due to different auxClasspath");
                    try {
                        previousAuxClasspathLoader.close();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    previousAuxClasspathLoader = new AuxClasspathLoader(rawAuxClasspath);
                }
            } else {
                previousAuxClasspathLoader = new AuxClasspathLoader(rawAuxClasspath);
            }
            return previousAuxClasspathLoader;
        }
    }

    /**
     * If {@link JavaLanguageProperties#REUSE_AUX_CLASSLOADER} is enabled, then this method can be used to explicitly
     * close the auxiliary classpath classloader, when it is known, that PMD won't be executed
     * anymore.
     *
     * @since 7.27.0
     * @experimental
     */
    @Experimental
    public static void closePreviousAuxClasspathLoader() {
        synchronized (LOCK) {
            if (previousAuxClasspathLoader != null) {
                AuxClasspathLoader toBeClosed = previousAuxClasspathLoader;
                previousAuxClasspathLoader = null;
                try {
                    toBeClosed.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    // TODO: use AuxClasspathUtil from #6841
    private void expandAuxClasspath(String classpath) {
        if (classpath == null) {
            return;
        }

        if (classpath.startsWith("file:")) {
            try {
                URI uri = new URI(classpath);
                String uriPath = uri.getPath();
                if (uriPath == null) {
                    // to support relative paths, only the scheme specific part is available
                    uriPath = uri.getSchemeSpecificPart();
                }
                Path path = Paths.get(uriPath);

                try (Stream<String> lines = Files.lines(path, Charset.defaultCharset())) {
                    auxClasspath.addAll(lines
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .filter(s -> !s.startsWith("#"))
                            .map(Paths::get)
                            .filter(Files::exists)
                            .collect(Collectors.toList()));
                }
            } catch (IOException | URISyntaxException e) {
                throw new IllegalArgumentException(e);
            }
        } else {
            StringTokenizer toker = new StringTokenizer(classpath, File.pathSeparator);
            while (toker.hasMoreTokens()) {
                String token = toker.nextToken();
                Path path = Paths.get(token);
                if (Files.exists(path)) {
                    auxClasspath.add(path);
                }
            }
        }
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

            javaHome = filePath.getParent().getParent().toString();

            try (URLClassLoader loader = new URLClassLoader(new URL[] { filePath.toUri().toURL() })) {
                Map<String, String> env = new HashMap<>();
                // note: providing java.home here is crucial, so that the correct runtime image is loaded.
                // the class loader is only used to provide an implementation of JrtFileSystemProvider, if the current
                // Java runtime doesn't provide one (e.g. if running in Java 8).
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

    private static final String MODULE_INFO_SUFFIX = "module-info.class";
    private static final String MODULE_INFO_SUFFIX_SLASH = "/" + MODULE_INFO_SUFFIX;

    private static @Nullable String extractModuleName(String name) {
        if (!name.endsWith(MODULE_INFO_SUFFIX_SLASH)) {
            return null;
        }
        return name.substring(0, name.length() - MODULE_INFO_SUFFIX_SLASH.length());
    }

    private static InputStream newInputStreamFromJrtFilesystem(Path path) {
        LOG.trace("Found {}", path);
        try {
            // Note: The input streams from JrtFileSystem are ByteArrayInputStreams and do not
            // need to be closed - we don't need to track these. The filesystem itself needs to be closed at the end.
            // See https://github.com/openjdk/jdk/blob/970cd202049f592946f9c1004ea92dbd58abf6fb/src/java.base/share/classes/jdk/internal/jrtfs/JrtFileSystem.java#L334
            return Files.newInputStream(path);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public @Nullable InputStream findResource(String name) {
        assert name != null;
        assert name.charAt(0) != '/'; // assuming only relative paths

        if (closeRequested) {
            throw new IllegalStateException("AuxClasspathLoader is closed");
        }

        // always search first in the jars of the aux classpath.
        // this allows to override platform classes (java.lang.*) - which java wouldn't allow
        for (Path path : auxClasspath) {
            if (Files.isRegularFile(path)) {
                @SuppressWarnings("PMD.CloseResource") // we keep the zip file open and close all at the end, see #close
                ZipFile jarFile = openJarFile(path);
                ZipEntry entry = jarFile.getEntry(name);
                if (entry != null) {
                    try {
                        return jarFile.getInputStream(entry);
                    } catch (IOException e) {
                        return null;
                    }
                }
            } else if (Files.isDirectory(path)) {
                Path classFile = path.resolve(name);
                if (Files.isRegularFile(classFile)) {
                    try {
                        return Files.newInputStream(classFile);
                    } catch (IOException e) {
                        return null;
                    }
                }
            }
        }

        // then search in jrt-fs, if available
        if (fileSystem != null) {
            String moduleName = extractModuleName(name);
            if (moduleName != null) {
                LOG.trace("Trying to load module-info.class for module {} in jrt-fs", moduleName);
                Path candidate = fileSystem.getPath("modules", moduleName, MODULE_INFO_SUFFIX);
                if (Files.exists(candidate)) {
                    return newInputStreamFromJrtFilesystem(candidate);
                }
            }

            int lastSlash = name.lastIndexOf('/');
            String packageName = name.substring(0, Math.max(lastSlash, 0));
            Set<String> moduleNames = packagesDirsToModules.get(packageName);
            if (moduleNames != null) {
                LOG.trace("Trying to find {} in jrt-fs with packageName={} and modules={}",
                        name, packageName, moduleNames);

                for (String moduleCandidate : moduleNames) {
                    Path candidate = fileSystem.getPath("modules", moduleCandidate, name);
                    if (Files.exists(candidate)) {
                        return newInputStreamFromJrtFilesystem(candidate);
                    }
                }
            }
        }

        return null;
    }

    private ZipFile openJarFile(Path path) {
        return zipFiles.computeIfAbsent(path, (p) -> {
            try {
                return new ZipFile(p.toFile());
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }

    @Override
    public String toString() {
        return "AuxClasspathLoader [auxClasspath=" + auxClasspath + ", jrt-fs: " + javaHome + ']';
    }

    @Override
    public void close() throws Exception {
        synchronized (LOCK) {
            if (previousAuxClasspathLoader == this) {
                LOG.debug("Not closing AuxClasspathLoader as it is cached and might be reused");
                return;
            }
        }

        closeRequested = true;
        IOUtil.ensureClosed(new ArrayList<>(zipFiles.values()), null);
        zipFiles.clear();
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
    }
}
