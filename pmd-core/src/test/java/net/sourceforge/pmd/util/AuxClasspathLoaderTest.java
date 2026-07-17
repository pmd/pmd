/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.platform.suite.api.Suite;

import net.sourceforge.pmd.internal.util.IOUtil;
import net.sourceforge.pmd.util.internal.AuxClasspathUtil;

class AuxClasspathLoaderTest {
    @TempDir
    private Path tempDir;

    @Test
    void findResourcesInMultipleJars() throws Exception {
        Path lib1 = createLib1();
        Path lib2 = createLib2();

        try (AuxClasspathLoader classpathLoader = new AuxClasspathLoader(lib1 + File.pathSeparator + lib2)) {
            assertResource(classpathLoader, "my/package/MyClass.class", "my.package.MyClass in lib1.jar");
            assertResource(classpathLoader, "my/other/package/Other.class", "my.other.package.Other in lib1.jar");
            assertResource(classpathLoader, "my/package/Other.class", "my.package.Other in lib2.jar");
            assertNull(classpathLoader.findResource("does/not/exist.class"));
        }
    }

    @Test
    void findResourcesInDirectories() throws Exception {
        Path classesDir = createClassesDir();
        Path lib1 = createLib1();

        // first directory "classes", then lib1
        try (AuxClasspathLoader classpathLoader = new AuxClasspathLoader(classesDir + File.pathSeparator + lib1)) {
            assertResource(classpathLoader, "my/package/MyClass.class", "my.package.MyClass in classes");
            assertResource(classpathLoader, "OtherResource.class", "OtherResource in classes");
            assertResource(classpathLoader, "my/other/package/Other.class", "my.other.package.Other in lib1.jar");
            assertNull(classpathLoader.findResource("does/not/exist.class"));
        }

        // the other way round: lib1 first, then directory "classes"
        try (AuxClasspathLoader classpathLoader = new AuxClasspathLoader(lib1 + File.pathSeparator + classesDir)) {
            assertResource(classpathLoader, "my/package/MyClass.class", "my.package.MyClass in lib1.jar");
            assertResource(classpathLoader, "OtherResource.class", "OtherResource in classes");
            assertResource(classpathLoader, "my/other/package/Other.class", "my.other.package.Other in lib1.jar");
            assertNull(classpathLoader.findResource("does/not/exist.class"));
        }
    }

    @Test
    void findWithClasspathList() throws Exception {
        Path lib1 = createLib1();
        Path lib2 = createLib2();
        Path classesDir = createClassesDir();

        Path classPathList = tempDir.resolve("auxclasspath.cp");
        Files.write(classPathList, CollectionUtil.listOf(
                "# jars",
                lib1.toString(),
                lib2.toString(),
                "# directories",
                classesDir.toString()
        ));

        try (AuxClasspathLoader classpathLoader = new AuxClasspathLoader(classPathList.toUri().toString())) {
            assertResource(classpathLoader, "my/package/MyClass.class", "my.package.MyClass in lib1.jar");
            assertResource(classpathLoader, "OtherResource.class", "OtherResource in classes");
            assertResource(classpathLoader, "my/other/package/Other.class", "my.other.package.Other in lib1.jar");
            assertResource(classpathLoader, "my/package/Other.class", "my.package.Other in lib2.jar");
            assertNull(classpathLoader.findResource("does/not/exist.class"));
        }
    }

    @Test
    void loadFromJrtFsCurrent() throws Exception {
        Path javaHome = Paths.get(System.getProperty("java.home"));
        int javaVersion = Integer.parseInt(System.getProperty("java.vm.specification.version"));
        Path jrtfsPath = javaHome.resolve("lib/jrt-fs.jar");
        assertTrue(Files.isRegularFile(jrtfsPath), "java" + javaVersion + " installation is incomplete. " + jrtfsPath + " not found!");
        String classPath = jrtfsPath.toString();

        try (AuxClasspathLoader loader = new AuxClasspathLoader(classPath)) {
            assertEquals(javaHome.toString(), loader.javaHome);
            try (InputStream stream = loader.findResource("java/lang/Object.class")) {
                assertClassFile(stream, javaVersion);
            }

            // should not fail for resources without a package
            assertNull(loader.findResource("ClassInDefaultPackage.class"));

            // load module java.base
            try (InputStream stream = loader.findResource("java.base/module-info.class")) {
                assertClassFile(stream, javaVersion);
            }
        }
    }

    @Test
    void findModuleInfoFromJar() throws Exception {
        try (AuxClasspathLoader loader = AuxClasspathLoader.create(
                StringUtils.join(AuxClasspathUtil.getRuntimeClasspath(), File.pathSeparator))) {
            // search for module org.junit.platform.suite.api, which should be on the test-classpath in pmd-core...
            // inside a jar
            String junitPlatformSuiteApiModule = "org.junit.platform.suite.api/module-info.class";
            try (InputStream resource = loader.findResource(junitPlatformSuiteApiModule)) {
                assertNotNull(resource, "module " + junitPlatformSuiteApiModule + " not found");
                byte[] fromAuxClasspathLoader = readBytes(resource);

                // org.junit.platform.suite.api.Suite is located in the same JarFile as junitPlatformSuiteApiModule
                URL jarFile = Suite.class.getProtectionDomain().getCodeSource().getLocation();
                URL jarModuleInfoUrl = new URL("jar:" + jarFile.toExternalForm() + "!/module-info.class");
                try (InputStream jarStream = jarModuleInfoUrl.openStream()) {
                    byte[] fromJarStream = readBytes(jarStream);
                    assertArrayEquals(fromAuxClasspathLoader, fromJarStream, "wrong module-info.class loaded");
                }
            }
        }
    }

    private static byte[] readBytes(InputStream stream) throws IOException {
        assertNotNull(stream);
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        try (InputStream inputStream = stream) {
            byte[] buffer = new byte[8192];
            int count;
            while ((count = inputStream.read(buffer)) != -1) {
                data.write(buffer, 0, count);
            }
        }
        return data.toByteArray();
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
    @ValueSource(ints = {11, 17, 21, 25})
    void loadFromJava(int javaVersion) throws Exception {
        Path javaHome = Paths.get(System.getProperty("user.home"), "openjdk" + javaVersion);
        assumeTrue(Files.isDirectory(javaHome), "Couldn't find java" + javaVersion + " installation at " + javaHome);

        Path jrtfsPath = javaHome.resolve("lib/jrt-fs.jar");
        assertTrue(Files.isRegularFile(jrtfsPath), "java" + javaVersion + " installation is incomplete. " + jrtfsPath + " not found!");
        String classPath = jrtfsPath.toString();

        try (AuxClasspathLoader loader = AuxClasspathLoader.create(classPath)) {
            assertTrue(loader.toString().contains("jrt-fs: " + javaHome));
            try (InputStream stream = loader.findResource("java/lang/Object.class")) {
                assertClassFile(stream, javaVersion);
            }

            // should not fail for resources without a package
            assertNull(loader.findResource("ClassInDefaultPackage.class"));

            // load module java.base
            try (InputStream stream = loader.findResource("java.base/module-info.class")) {
                assertClassFile(stream, javaVersion);
            }
        }
    }

    private void assertClassFile(InputStream inputStream, int javaVersion) throws IOException {
        assertNotNull(inputStream);
        try (DataInputStream data = new DataInputStream(inputStream)) {
            int magicNumber = data.readInt();
            assertEquals(0xcafebabe, magicNumber);
            data.readUnsignedShort(); // minorVersion
            int majorVersion = data.readUnsignedShort();
            assertEquals(44 + javaVersion, majorVersion);
        }
    }

    private Path createLib1() throws IOException {
        Path lib1 = tempDir.resolve("lib1.jar");
        try (ZipOutputStream zip = new ZipOutputStream(Files.newOutputStream(lib1))) {
            writeEntry(zip, "my/package/MyClass.class", "my.package.MyClass in lib1.jar");
            writeEntry(zip, "my/other/package/Other.class", "my.other.package.Other in lib1.jar");
        }
        return lib1;
    }

    private Path createLib2() throws IOException {
        Path lib2 = tempDir.resolve("lib2.jar");
        try (ZipOutputStream zip = new ZipOutputStream(Files.newOutputStream(lib2))) {
            writeEntry(zip, "my/package/MyClass.class", "my.package.MyClass in lib2.jar");
            writeEntry(zip, "my/package/Other.class", "my.package.Other in lib2.jar");
        }
        return lib2;
    }

    private static void writeEntry(ZipOutputStream zip, String name, String content) throws IOException {
        ZipEntry entry = new ZipEntry(name);
        zip.putNextEntry(entry);
        zip.write(content.getBytes(StandardCharsets.UTF_8));
        zip.closeEntry();
    }

    private Path createClassesDir() throws IOException {
        Path classesDir = Files.createDirectory(tempDir.resolve("classes"));
        Path resource1 = Files.createDirectories(classesDir.resolve("my/package")).resolve("MyClass.class");
        Files.write(resource1, "my.package.MyClass in classes".getBytes(StandardCharsets.UTF_8));
        Path resource2 = classesDir.resolve("OtherResource.class");
        Files.write(resource2, "OtherResource in classes".getBytes(StandardCharsets.UTF_8));
        return classesDir;
    }

    private static void assertResource(AuxClasspathLoader classpathLoader, String name, String expectedContent) throws IOException {
        try (InputStream resource = classpathLoader.findResource(name)) {
            assertNotNull(resource);
            Assertions.assertEquals(expectedContent, IOUtil.readToString(resource, StandardCharsets.UTF_8));
        }
    }
}
