/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.internal.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
     * is essentially empty.
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
     *
     * <p>
     *     This test only runs, if you have a folder ${HOME}/openjdk17.
     * </p>
     */
    @Test
    void loadFromJava17() throws IOException {
        Path java17Home = Paths.get(System.getProperty("user.home"), "openjdk17");
        assumeTrue(Files.isDirectory(java17Home), "Couldn't find java17 installation at " + java17Home);

        Path jrtfsPath = java17Home.resolve("lib/jrt-fs.jar");
        assertTrue(Files.isRegularFile(jrtfsPath), "java17 installation is incomplete. " + jrtfsPath + " not found!");
        String classPath = jrtfsPath.toString();

        try (ClasspathClassLoader loader = new ClasspathClassLoader(classPath, null)) {
            assertEquals(java17Home.toString(), loader.javaHome);
            try (InputStream stream = loader.getResourceAsStream("java/lang/Object.class")) {
                assertNotNull(stream);
                try (DataInputStream data = new DataInputStream(stream)) {
                    assertClassFile(data, 17);
                }
            }
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
