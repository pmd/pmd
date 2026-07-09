/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.DataInputStream;
import java.io.File;
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

import net.sourceforge.pmd.internal.util.IOUtil;
import net.sourceforge.pmd.util.CollectionUtil;

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
            assertEquals(expectedContent, IOUtil.readToString(resource, StandardCharsets.UTF_8));
        }
    }
}
