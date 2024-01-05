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

    @Test
    void loadFromJar() throws IOException {
        final String RESOURCE_NAME = "net/sourceforge/pmd/Sample.txt";
        final String TEST_CONTENT = "Test\n";

        Path jarPath = tempDir.resolve("custom.jar");
        try (ZipOutputStream out = new ZipOutputStream(Files.newOutputStream(jarPath))) {
            out.putNextEntry(new ZipEntry(RESOURCE_NAME));
            out.write(TEST_CONTENT.getBytes(StandardCharsets.UTF_8));
        }
        String classpath = jarPath.toString();

        try (ClasspathClassLoader loader = new ClasspathClassLoader(classpath, null)) {
            try (InputStream in = loader.getResourceAsStream(RESOURCE_NAME)) {
                assertNotNull(in);
                String s = IOUtil.readToString(in, StandardCharsets.UTF_8);
                assertEquals(TEST_CONTENT, s);
            }
        }
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
