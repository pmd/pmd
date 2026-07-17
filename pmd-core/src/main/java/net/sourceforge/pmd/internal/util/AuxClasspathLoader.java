/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.internal.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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
import net.sourceforge.pmd.util.internal.AuxClasspathUtil;

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
public class AuxClasspathLoader implements AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(AuxClasspathLoader.class);

    private static final Object LOCK = new Object();
    private static Map<String, AuxClasspathLoader> cache;

    private boolean closeRequested;

    private static final class Entry {
        private final Path path;
        private final boolean isFile;

        private Entry(Path path, boolean isFile) {
            this.path = path;
            this.isFile = isFile;
        }

        private static Entry create(Path path) {
            if (Files.isRegularFile(path)) {
                return new Entry(path, true);
            } else if (Files.isDirectory(path)) {
                return new Entry(path, false);
            }
            throw new IllegalArgumentException("Path '" + path + "' does not exist");
        }

        public Path getPath() {
            return path;
        }

        public boolean isFile() {
            return isFile;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Entry entry = (Entry) o;
            return isFile == entry.isFile && Objects.equals(path, entry.path);
        }

        @Override
        public int hashCode() {
            return Objects.hash(path, isFile);
        }
    }

    private final List<Entry> auxClasspath;
    private final ConcurrentMap<Path, ZipFile> zipFiles = new ConcurrentHashMap<>();

    String javaHome;

    private FileSystem fileSystem;
    private Map<String, Set<String>> packagesDirsToModules;

    AuxClasspathLoader(String rawAuxClasspath) {
        LOG.debug("Creating new AuxClasspathLoader for {}", rawAuxClasspath);
        this.auxClasspath = expandAuxClasspath(rawAuxClasspath);

        Iterator<Entry> iterator = auxClasspath.iterator();
        while (iterator.hasNext()) {
            Path filePath = iterator.next().getPath().toAbsolutePath();
            if (filePath.endsWith(Paths.get("lib", "jrt-fs.jar"))) {
                initializeJrtFilesystem(filePath);
                // don't add jrt-fs.jar to the normal aux classpath
                iterator.remove();
            }
        }
    }

    public static AuxClasspathLoader create(String rawAuxClasspath) {
        synchronized (LOCK) {
            if (cache == null) {
                return new AuxClasspathLoader(rawAuxClasspath);
            }

            AuxClasspathLoader cachedAuxClasspathLoader = cache.get(rawAuxClasspath);
            if (cachedAuxClasspathLoader != null) {
                LOG.debug("Reusing previously cached AuxClasspathLoader");
                return cachedAuxClasspathLoader;
            }

            LOG.debug("Creating new AuxClasspathLoader");
            AuxClasspathLoader newAuxClasspathLoader = new AuxClasspathLoader(rawAuxClasspath);
            cache.put(rawAuxClasspath, newAuxClasspathLoader);
            return newAuxClasspathLoader;
        }
    }

    /**
     * Enables caching of AuxClasspathLoader instances. This is useful for unit tests or IDE plugins,
     * when PMD is executed multiple times within one JVM instance.
     *
     * @param count Maximum number of instances to be cached. The oldest instances are
     *              evicted first.
     *
     * @see #disableReuse()
     * @since 7.27.0
     * @experimental
     */
    @Experimental
    public static void enableReuse(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("count must be >= 1");
        }

        synchronized (LOCK) {
            if (cache == null) {
                cache = new LinkedHashMap<String, AuxClasspathLoader>() {
                    @Override
                    protected boolean removeEldestEntry(Map.Entry<String, AuxClasspathLoader> eldest) {
                        return size() > count;
                    }
                };
            }
        }
    }

    /**
     * If {@link #enableReuse(int)} is used, then this method can be used to explicitly
     * close the cached auxiliary classpath classloaders.
     *
     * <p>Call this only, when it is known, that PMD is not currently
     * executing (e.g. PmdAnalysis is finished).</p>
     *
     * @since 7.27.0
     * @experimental
     */
    @Experimental
    public static void disableReuse() {
        synchronized (LOCK) {
            if (cache == null) {
                return;
            }

            Collection<AuxClasspathLoader> toBeClosed = cache.values();
            cache = null; // disable reuse, make sure, we actually close (see #close())
            Exception exception = IOUtil.closeAll(toBeClosed);
            if (exception != null) {
                throw new RuntimeException(exception);
            }
        }
    }

    private List<Entry> expandAuxClasspath(String classpath) {
        if (classpath == null) {
            return Collections.emptyList();
        }

        List<Path> paths = AuxClasspathUtil.expandClasspath(classpath);
        return paths.stream()
                        .filter(Files::exists)
                        .map(Entry::create)
                        .collect(Collectors.toList());
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

    public @Nullable InputStream findResource(String name) {
        assert name != null;
        assert name.charAt(0) != '/'; // assuming only relative paths

        if (closeRequested) {
            throw new IllegalStateException("AuxClasspathLoader is closed");
        }

        // always search first in the jars of the aux classpath.
        // this allows to override platform classes (java.lang.*) - which java wouldn't allow
        for (Entry classpathEntry : auxClasspath) {
            if (classpathEntry.isFile()) {
                @SuppressWarnings("PMD.CloseResource") // we keep the zip file open and close all at the end, see #close
                ZipFile jarFile = openJarFile(classpathEntry.getPath());
                ZipEntry entry = jarFile.getEntry(name);
                if (entry != null) {
                    try {
                        return jarFile.getInputStream(entry);
                    } catch (IOException e) {
                        return null;
                    }
                }
            } else {
                Path classFile = classpathEntry.getPath().resolve(name);
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
            if (cache != null) {
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
