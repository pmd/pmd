/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util;

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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.checkerframework.checker.lock.qual.GuardedBy;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ModuleVisitor;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.internal.util.IOUtil;
import net.sourceforge.pmd.util.internal.AuxClasspathUtil;

/**
 * This class allows to load resources from a given classpath. Unlike a real classloader
 * like {@link URLClassLoader}, the Jar files on the classpath are not opened with JarFile and
 * their signature is not verified which saves memory. The Jar files are opened directly
 * with {@link ZipFile}.
 *
 * <p>This classpath loader also supports loading platform classes (e.g. {@code java.lang}) from
 * the jrt-fs filesystem. All zip files and the jrt-fs are kept open, until this classpath loader
 * is closed.</p>
 *
 * <p>To create a new instance, use {@link #create(String)}. Because verifying the classpath and
 * opening the platform classloader via the jrt-fs filesystem is expensive, it supports a simple
 * caching mechanism. See {@link #enableReuse(int)} and {@link #disableReuse()}.</p>
 *
 * <p>To load resources, use {@link #findResource(String)}.</p>
 *
 * <p>This class is usually not used directly for calling PMD but rather by language implementations
 * such as Java. The auxClasspath should be configured
 * via {@link net.sourceforge.pmd.PMDConfiguration#setAuxClasspath(String)}</p>
 *
 * @since 7.27.0
 * @experimental Replacement for {@link net.sourceforge.pmd.internal.util.ClasspathClassLoader}.
 */
@Experimental
public class AuxClasspathLoader implements AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(AuxClasspathLoader.class);

    private static final String MODULE_INFO_SUFFIX = "module-info.class";
    private static final String MODULE_INFO_SUFFIX_SLASH = "/" + MODULE_INFO_SUFFIX;

    private static final Object LOCK = new Object();
    private static @GuardedBy("LOCK") Map<String, AuxClasspathLoader> cache;

    private final AtomicBoolean closeRequested = new AtomicBoolean(false);

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

        @Override
        public String toString() {
            return path.toString();
        }
    }

    private final List<Entry> auxClasspath;
    private final ConcurrentMap<Path, ZipFile> zipFiles = new ConcurrentHashMap<>();

    private final String javaHome;
    private final FileSystem fileSystem;
    private final Map<String, Set<String>> packagesDirsToModules;

    // this is lazily initialized on first query of a module-info.class
    private Map<String, ZipFile> moduleNameToZipFile;

    AuxClasspathLoader(String rawAuxClasspath) {
        LOG.debug("Creating new AuxClasspathLoader for {}", rawAuxClasspath);
        this.auxClasspath = expandAuxClasspath(rawAuxClasspath);

        List<Path> jrtJars = new ArrayList<>();
        Iterator<Entry> iterator = auxClasspath.iterator();
        while (iterator.hasNext()) {
            Path filePath = iterator.next().getPath().toAbsolutePath();
            if (filePath.endsWith(Paths.get("lib", "jrt-fs.jar"))) {
                jrtJars.add(filePath);
                // don't add jrt-fs.jar to the normal aux classpath
                iterator.remove();
            }
        }

        if (jrtJars.isEmpty()) {
            // no jrt filesystem
            javaHome = null;
            fileSystem = null;
            packagesDirsToModules = null;
        } else if (jrtJars.size() == 1) {
            Path jrtJarPath = jrtJars.get(0);
            javaHome = jrtJarPath.getParent().getParent().toString();
            fileSystem = initializeJrtFilesystem(javaHome, jrtJarPath);
            packagesDirsToModules = Collections.unmodifiableMap(initializePackagesDirsToModules());
        } else {
            throw new IllegalStateException("Multiple jrt-fs.jar files on the auxClasspath: " + jrtJars);
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
                        boolean remove = size() > count;
                        if (remove) {
                            this.remove(eldest.getKey());
                            try {
                                eldest.getValue().close();
                            } catch (IOException e) {
                                throw new UncheckedIOException(e);
                            }
                        }
                        return false;
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

            List<AuxClasspathLoader> toBeClosed = new ArrayList<>(cache.values());
            cache = null; // disable reuse, make sure, we actually close (see #close())
            try {
                IOUtil.ensureClosed(toBeClosed, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private List<Entry> expandAuxClasspath(String classpath) {
        if (classpath == null) {
            return Collections.emptyList();
        }

        List<Path> paths = AuxClasspathUtil.expandClasspath(classpath);
        return paths.stream()
                        .filter(path -> {
                            boolean exists = Files.exists(path);
                            if (!exists) {
                                LOG.warn("Ignoring not existing auxClasspath entry '{}'", path);
                            }
                            return exists;
                        })
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
    private FileSystem initializeJrtFilesystem(String javaHome, Path filePath) {
        try {
            LOG.debug("Detected Java Runtime Filesystem Provider in {}", filePath);

            if (fileSystem != null) {
                throw new IllegalStateException("There is already a jrt filesystem. Do you have multiple jrt-fs.jar files on the classpath?");
            }

            if (filePath.getNameCount() < 2) {
                throw new IllegalArgumentException("Can't determine java home from " + filePath + " - please provide a complete path.");
            }

            if (!filePath.getParent().getParent().toString().equals(javaHome)) {
                throw new IllegalArgumentException("Invalid javaHome/jrt-fs.jar file combination. JavaHome=" + javaHome + " jrt-fs.jar=" + filePath);
            }

            try (URLClassLoader loader = new URLClassLoader(new URL[]{filePath.toUri().toURL()})) {
                Map<String, String> env = new HashMap<>();
                // note: providing java.home here is crucial, so that the correct runtime image is loaded.
                // the class loader is only used to provide an implementation of JrtFileSystemProvider, if the current
                // Java runtime doesn't provide one (e.g. if running in Java 8).
                env.put("java.home", javaHome);
                LOG.debug("Creating jrt-fs with env {}", env);
                return FileSystems.newFileSystem(URI.create("jrt:/"), env, loader);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private Map<String, Set<String>> initializePackagesDirsToModules() {
        try {
            final Map<String, Set<String>> result = new HashMap<>();
            Path packages = fileSystem.getPath("packages");
            try (Stream<Path> packagesStream = Files.list(packages)) {
                packagesStream.forEach(p -> {
                    String packageName = p.getFileName().toString().replace('.', '/');
                    try (Stream<Path> modulesStream = Files.list(p)) {
                        Set<String> modules = modulesStream
                                .map(Path::getFileName)
                                .map(Path::toString)
                                .collect(Collectors.toSet());
                        result.put(packageName, modules);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
            }
            return result;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

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

    /**
     * Finds the first resource with the given name (e.g. {@code package/A.class})
     * in the jars of the auxClasspath.
     * If there is no match in the jar files, then the Java Runtime Image is searched
     * via the given "jrt-fs.jar". This allows to find e.g. {@code java/lang/Object.class}.
     * If no resource is found, {@code null} is returned.
     *
     * <p>The name is expected to be a valid, relative path name within a jar file without a leading slash, e.g.
     * {@code package/A.class}.</p>
     *
     * <p>In order to load {@code module-info.class} files, which are all at the root of the jar files,
     * these can be referenced by prefixing the module name, e.g. {@code full.module.name/module-info.class}
     * or {@code java.base/module-info.class}.</p>
     *
     * <p>Note: Multi-Release Jars are not handled in a special way. This method does not automatically
     * search for versioned resources.</p>
     *
     * @param name Name of the resource to load, e.g. {@code package/A.class}
     * @return an open {@link InputStream} or {@code null} if the resource is not found.
     *
     * @see <a href="https://openjdk.org/jeps/220">JEP 220: Modular Run-Time Images</a>
     */
    public @Nullable InputStream findResource(String name) {
        assert name != null;
        assert name.charAt(0) != '/'; // assuming only relative paths

        if (closeRequested.get()) {
            throw new IllegalStateException("AuxClasspathLoader is closed");
        }

        String moduleName = extractModuleName(name);
        if (moduleName != null) {
            collectAllModules();
            assert moduleNameToZipFile != null : "Modules should have been detected by collectAllModules()";

            @SuppressWarnings("PMD.CloseResource") // we keep the zip file open and close all at the end, see #close
            // the zip files in #moduleNameToZipFile are the same as in #zipFiles
            ZipFile moduleZipFile = moduleNameToZipFile.get(moduleName);
            if (moduleZipFile != null) {
                ZipEntry moduleZipFileEntry = moduleZipFile.getEntry(MODULE_INFO_SUFFIX);
                try {
                    return moduleZipFile.getInputStream(moduleZipFileEntry);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
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
                        throw new UncheckedIOException(e);
                    }
                }
            } else {
                Path classFile = classpathEntry.getPath().resolve(name);
                if (Files.isRegularFile(classFile)) {
                    try {
                        return Files.newInputStream(classFile);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }
            }
        }

        // then search in jrt-fs, if available
        if (fileSystem != null) {
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

    private static class ModuleNameExtractor extends ClassVisitor {
        private String moduleName;

        protected ModuleNameExtractor() {
            super(Opcodes.ASM9);
        }

        @Override
        public ModuleVisitor visitModule(String name, int access, String version) {
            moduleName = name;
            return null;
        }

        public String getModuleName() {
            return moduleName;
        }
    }

    private void collectAllModules() {
        if (moduleNameToZipFile != null) {
            return;
        }

        Map<String, ZipFile> allModules = new HashMap<>();
        for (Entry classpathEntry : auxClasspath) {
            if (classpathEntry.isFile()) {
                ZipFile jarFile = openJarFile(classpathEntry.getPath());
                ZipEntry entry = jarFile.getEntry(MODULE_INFO_SUFFIX);
                if (entry != null) {
                    try {
                        ModuleNameExtractor finder = new ModuleNameExtractor();
                        try (InputStream inputStream = jarFile.getInputStream(entry)) {
                            ClassReader classReader = new ClassReader(inputStream);
                            classReader.accept(finder, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                        }
                        allModules.putIfAbsent(finder.getModuleName(), jarFile);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }
            }
        }
        LOG.debug("Found {} modules on auxClasspath", allModules.size());
        moduleNameToZipFile = Collections.unmodifiableMap(allModules);
    }

    @Override
    public String toString() {
        return "AuxClasspathLoader [auxClasspath=" + auxClasspath + ", jrt-fs: " + javaHome + ']';
    }

    @Override
    public void close() throws IOException {
        synchronized (LOCK) {
            if (cache != null && cache.containsValue(this)) {
                LOG.debug("Not closing AuxClasspathLoader as it is cached and might be reused");
                return;
            }
        }

        closeRequested.set(true);
        try {
            IOUtil.ensureClosed(new ArrayList<>(zipFiles.values()), null);
        } catch (Exception e) {
            throw new IOException(e);
        }
        zipFiles.clear();
        if (fileSystem != null) {
            fileSystem.close();
            // jrt created an own classloader to load the JrtFileSystemProvider class out of the
            // jrt-fs.jar. This needs to be closed manually.
            ClassLoader classLoader = fileSystem.getClass().getClassLoader();
            if (classLoader instanceof URLClassLoader) {
                ((URLClassLoader) classLoader).close();
            }
        }
    }

    String getJavaHome() {
        return javaHome;
    }
}
