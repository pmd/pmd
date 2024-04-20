/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.internal.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ClasspathClassLoaderTest {
    @TempDir
    private Path tempDir;

    @Test
    void loadEmptyClasspathWithParent() throws IOException {
        try (ClasspathClassLoader loader = new ClasspathClassLoader("", ClasspathClassLoader.class.getClassLoader())) {
            try (InputStream resource = loader.getResourceAsStream("java/lang/Object.class")) {
                assertNotNull(resource);
                try (DataInputStream data = new DataInputStream(resource)) {
                    assertClassFile(data, Integer.valueOf(System.getProperty("java.specification.version")));
                }
            }
        }
    }

    /**
     * This test case just documents the current behavior: Eventually we load
     * the class files from the system class loader, even if the auxclasspath
     * is essentially empty and no parent is provided. This is an unavoidable
     * behavior of {@link java.lang.ClassLoader#getResource(java.lang.String)}, which will
     * search the class loader built into the VM (BootLoader).
     */
    @Test
    void loadEmptyClasspathNoParent() throws IOException {
        try (ClasspathClassLoader loader = new ClasspathClassLoader("", null)) {
            try (InputStream resource = loader.getResourceAsStream("java/lang/Object.class")) {
                assertNotNull(resource);
                try (DataInputStream data = new DataInputStream(resource)) {
                    assertClassFile(data, Integer.valueOf(System.getProperty("java.specification.version")));
                }
            }
        }
    }

    private static final String CUSTOM_JAR_RESOURCE = "net/sourceforge/pmd/Sample.txt";
    private static final String CUSTOM_JAR_RESOURCE2 = "net/sourceforge/pmd/Sample2.txt";
    private static final String CUSTOM_JAR_RESOURCE_CONTENT = "Test\n";

    private Path prepareCustomJar() throws IOException {
        Path jarPath = tempDir.resolve("custom.jar");
        try (ZipOutputStream out = new ZipOutputStream(Files.newOutputStream(jarPath))) {
            out.putNextEntry(new ZipEntry(CUSTOM_JAR_RESOURCE));
            out.write(CUSTOM_JAR_RESOURCE_CONTENT.getBytes(StandardCharsets.UTF_8));
            out.putNextEntry(new ZipEntry(CUSTOM_JAR_RESOURCE2));
            out.write(CUSTOM_JAR_RESOURCE_CONTENT.getBytes(StandardCharsets.UTF_8));
        }
        return jarPath;
    }

    @Test
    void loadFromJar() throws IOException {
        Path jarPath = prepareCustomJar();
        String classpath = jarPath.toString();

        try (ClasspathClassLoader loader = new ClasspathClassLoader(classpath, null)) {
            try (InputStream in = loader.getResourceAsStream(CUSTOM_JAR_RESOURCE)) {
                assertNotNull(in);
                String s = IOUtil.readToString(in, StandardCharsets.UTF_8);
                assertEquals(CUSTOM_JAR_RESOURCE_CONTENT, s);
            }
        }
    }

    /**
     * @see <a href="https://github.com/pmd/pmd/issues/4899">[java] Parsing failed in ParseLock#doParse() java.io.IOException: Stream closed #4899</a>
     */
    @Test
    void loadMultithreadedFromJar() throws IOException, InterruptedException {
        Path jarPath = prepareCustomJar();
        String classpath = jarPath.toString();

        int numberOfThreads = 2;

        final CyclicBarrier waitForClosed = new CyclicBarrier(numberOfThreads);
        final Semaphore grabResource = new Semaphore(1);
        final List<Exception> caughtExceptions = new ArrayList<>();

        class ThreadRunnable extends Thread {
            private final int number;

            ThreadRunnable(int number) {
                super("Thread" + number);
                this.number = number;
            }

            @Override
            public void run() {
                try (ClasspathClassLoader loader = new ClasspathClassLoader(classpath, null)) {
                    // Make sure, the threads get the resource stream one after another, so that the
                    // underlying Jar File is definitively cached (if caching is enabled).
                    grabResource.acquire();
                    InputStream stream;
                    try {
                        stream = loader.getResourceAsStream(CUSTOM_JAR_RESOURCE);
                    } finally {
                        grabResource.release();
                    }
                    try (InputStream in = stream) {
                        assertNotNull(in);
                        if (number > 0) {
                            // all except the first thread should wait until the first thread is finished
                            // and has closed the ClasspathClassLoader
                            waitForClosed.await();
                        }
                        String s = IOUtil.readToString(in, StandardCharsets.UTF_8);
                        assertEquals(CUSTOM_JAR_RESOURCE_CONTENT, s);
                    }
                } catch (Exception e) {
                    caughtExceptions.add(e);
                    throw new RuntimeException(e);
                } finally {
                    try {
                        if (number == 0) {
                            // signal the other waiting threads to continue. Here, we have closed
                            // already the ClasspathClassLoader.
                            waitForClosed.await();
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } catch (BrokenBarrierException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        List<Thread> threads = new ArrayList<>(numberOfThreads);
        for (int i = 0; i < numberOfThreads; i++) {
            threads.add(new ThreadRunnable(i));
        }

        threads.forEach(Thread::start);
        for (Thread thread : threads) {
            thread.join();
        }

        assertTrue(caughtExceptions.isEmpty());
    }

    /**
     * Verifies, that we load the class files from the runtime image of the correct java home.
     * This tests multiple versions, in order to avoid that the test accidentally is successful when
     * testing e.g. java17 and running the build with java17. In that case, we might load java.lang.Object
     * from the system classloader and not from jrt-fs.jar.
     *
     * <p>
     *     This test only runs, if you have a folder ${HOME}/openjdk{javaVersion}.
     * </p>
     */
    @ParameterizedTest
    @ValueSource(ints = {11, 17, 21})
    void loadFromJava(int javaVersion) throws IOException {
        Path javaHome = Paths.get(System.getProperty("user.home"), "openjdk" + javaVersion);
        assumeTrue(Files.isDirectory(javaHome), "Couldn't find java" + javaVersion + " installation at " + javaHome);

        Path jrtfsPath = javaHome.resolve("lib/jrt-fs.jar");
        assertTrue(Files.isRegularFile(jrtfsPath), "java" + javaVersion + " installation is incomplete. " + jrtfsPath + " not found!");
        String classPath = jrtfsPath.toString();

        try (ClasspathClassLoader loader = new ClasspathClassLoader(classPath, null)) {
            assertEquals(javaHome.toString(), loader.javaHome);
            try (InputStream stream = loader.getResourceAsStream("java/lang/Object.class")) {
                assertNotNull(stream);
                try (DataInputStream data = new DataInputStream(stream)) {
                    assertClassFile(data, javaVersion);
                }
            }

            // should not fail for resources without a package
            assertNull(loader.getResourceAsStream("ClassInDefaultPackage.class"));
        }
    }

    private void assertClassFile(DataInputStream data, int javaVersion) throws IOException {
        int magicNumber = data.readInt();
        assertEquals(0xcafebabe, magicNumber);
        data.readUnsignedShort(); // minorVersion
        int majorVersion = data.readUnsignedShort();
        assertEquals(44 + javaVersion, majorVersion);
    }
}
