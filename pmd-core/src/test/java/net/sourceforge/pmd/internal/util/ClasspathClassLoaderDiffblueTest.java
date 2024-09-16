package net.sourceforge.pmd.internal.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.management.loading.MLet;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ClasspathClassLoaderDiffblueTest {
    /**
     * Method under test:
     * {@link ClasspathClassLoader#ClasspathClassLoader(String, ClassLoader)}
     */
    @Test
    void testNewClasspathClassLoader() throws IOException {
        // Arrange
        ArrayList<File> files = new ArrayList<>();

        // Act and Assert
        assertNotNull(new ClasspathClassLoader("Classpath", new ClasspathClassLoader(files, new MLet())));
    }

    /**
     * Method under test:
     * {@link ClasspathClassLoader#ClasspathClassLoader(String, ClassLoader)}
     */
    @Test
    void testNewClasspathClassLoader2() throws IOException {
        // Arrange
        ArrayList<File> files = new ArrayList<>();

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> new ClasspathClassLoader("file:",
                new ClasspathClassLoader("Classpath", new ClasspathClassLoader(files, new MLet()))));

    }

    /**
     * Method under test:
     * {@link ClasspathClassLoader#ClasspathClassLoader(List, ClassLoader)}
     */
    @Test
    void testNewClasspathClassLoader3() throws IOException {
        // Arrange
        ArrayList<File> files = new ArrayList<>();

        // Act and Assert
        assertNotNull(new ClasspathClassLoader(files, new ClasspathClassLoader("Classpath", new MLet())));
    }

    /**
     * Method under test:
     * {@link ClasspathClassLoader#ClasspathClassLoader(List, ClassLoader)}
     */
    @Test
    void testNewClasspathClassLoader4() throws IOException {
        // Arrange
        ArrayList<File> files = new ArrayList<>();
        files.add(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt").toFile());
        ArrayList<File> files2 = new ArrayList<>();

        // Act and Assert
        assertNotNull(new ClasspathClassLoader(files,
                new ClasspathClassLoader("Classpath", new ClasspathClassLoader(files2, new MLet()))));
    }

    /**
     * Method under test:
     * {@link ClasspathClassLoader#ClasspathClassLoader(List, ClassLoader)}
     */
    @Test
    void testNewClasspathClassLoader5() throws IOException {
        // Arrange
        ArrayList<File> files = new ArrayList<>();
        URLStreamHandlerFactory urlStreamHandlerFactory = mock(URLStreamHandlerFactory.class);
        when(urlStreamHandlerFactory.createURLStreamHandler(Mockito.<String>any())).thenReturn(null);

        // Act
        ClasspathClassLoader actualClasspathClassLoader = new ClasspathClassLoader(files,
                new ClasspathClassLoader("Classpath",
                        new URLClassLoader(new URL[]{Paths.get(System.getProperty("java.io.tmpdir"), "test.txt").toUri().toURL()},
                                new ClasspathClassLoader("Classpath", new MLet()), urlStreamHandlerFactory)));

        // Assert
        verify(urlStreamHandlerFactory).createURLStreamHandler(eq("jar"));
        assertNotNull(actualClasspathClassLoader);
    }

    /**
     * Method under test: {@link ClasspathClassLoader#toString()}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testToString() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Missing observers.
        //   Diffblue Cover was unable to create an assertion.
        //   Add getters for the following fields or make them package-private:
        //     ClasspathClassLoader.fileSystem
        //     ClasspathClassLoader.javaHome
        //     ClasspathClassLoader.moduleNameToModuleInfoUrls
        //     ClasspathClassLoader.packagesDirsToModules
        //     URLClassLoader.acc
        //     URLClassLoader.closeables
        //     URLClassLoader.ucp
        //     SecureClassLoader.pdcache

        // Arrange
        // TODO: Populate arranged inputs
        ClasspathClassLoader classpathClassLoader = null;

        // Act
        String actualToStringResult = classpathClassLoader.toString();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ClasspathClassLoader#getResourceAsStream(String)}
     */
    @Test
    void testGetResourceAsStream() throws IOException {
        // Arrange
        ArrayList<File> files = new ArrayList<>();

        // Act and Assert
        assertNull((new ClasspathClassLoader("Classpath", new ClasspathClassLoader(files, new MLet())))
                .getResourceAsStream("Name"));
    }

    /**
     * Method under test: {@link ClasspathClassLoader#getResourceAsStream(String)}
     */
    @Test
    void testGetResourceAsStream2() throws IOException {
        // Arrange
        URLStreamHandlerFactory urlStreamHandlerFactory = mock(URLStreamHandlerFactory.class);
        when(urlStreamHandlerFactory.createURLStreamHandler(Mockito.<String>any())).thenReturn(null);
        URLClassLoader parent = new URLClassLoader(
                new URL[]{Paths.get(System.getProperty("java.io.tmpdir"), "test.txt").toUri().toURL()},
                new ClasspathClassLoader("/module-info.class", new MLet()), urlStreamHandlerFactory);

        // Act
        InputStream actualResourceAsStream = (new ClasspathClassLoader("Classpath",
                new ClasspathClassLoader(new ArrayList<>(), parent))).getResourceAsStream("Name");

        // Assert
        verify(urlStreamHandlerFactory).createURLStreamHandler(eq("jar"));
        assertNull(actualResourceAsStream);
    }

    /**
     * Method under test: {@link ClasspathClassLoader#getResourceAsStream(String)}
     */
    @Test
    void testGetResourceAsStream3() throws IOException {
        // Arrange
        URLStreamHandlerFactory urlStreamHandlerFactory = mock(URLStreamHandlerFactory.class);
        when(urlStreamHandlerFactory.createURLStreamHandler(Mockito.<String>any())).thenReturn(null);
        URLClassLoader parent = new URLClassLoader(
                new URL[]{Paths.get(System.getProperty("java.io.tmpdir"), "test.txt").toUri().toURL()},
                new ClasspathClassLoader("/module-info.class", new MLet()), urlStreamHandlerFactory);

        // Act
        InputStream actualResourceAsStream = (new ClasspathClassLoader("Classpath",
                new ClasspathClassLoader(new ArrayList<>(), parent))).getResourceAsStream("/module-info.class");

        // Assert
        verify(urlStreamHandlerFactory).createURLStreamHandler(eq("jar"));
        assertNull(actualResourceAsStream);
    }

    /**
     * Method under test: {@link ClasspathClassLoader#getResource(String)}
     */
    @Test
    void testGetResource() throws IOException {
        // Arrange
        ArrayList<File> files = new ArrayList<>();

        // Act and Assert
        assertNull(
                (new ClasspathClassLoader("Classpath", new ClasspathClassLoader(files, new MLet()))).getResource("Name"));
    }

    /**
     * Method under test: {@link ClasspathClassLoader#getResource(String)}
     */
    @Test
    void testGetResource2() throws IOException {
        // Arrange
        URLStreamHandlerFactory urlStreamHandlerFactory = mock(URLStreamHandlerFactory.class);
        when(urlStreamHandlerFactory.createURLStreamHandler(Mockito.<String>any())).thenReturn(null);
        URLClassLoader parent = new URLClassLoader(
                new URL[]{Paths.get(System.getProperty("java.io.tmpdir"), "test.txt").toUri().toURL()},
                new ClasspathClassLoader("/module-info.class", new MLet()), urlStreamHandlerFactory);

        // Act
        URL actualResource = (new ClasspathClassLoader("Classpath", new ClasspathClassLoader(new ArrayList<>(), parent)))
                .getResource("Name");

        // Assert
        verify(urlStreamHandlerFactory).createURLStreamHandler(eq("jar"));
        assertNull(actualResource);
    }

    /**
     * Method under test: {@link ClasspathClassLoader#getResource(String)}
     */
    @Test
    void testGetResource3() throws IOException {
        // Arrange
        URLStreamHandlerFactory urlStreamHandlerFactory = mock(URLStreamHandlerFactory.class);
        when(urlStreamHandlerFactory.createURLStreamHandler(Mockito.<String>any())).thenReturn(null);
        URLClassLoader parent = new URLClassLoader(
                new URL[]{Paths.get(System.getProperty("java.io.tmpdir"), "test.txt").toUri().toURL()},
                new ClasspathClassLoader("/module-info.class", new MLet()), urlStreamHandlerFactory);

        // Act
        URL actualResource = (new ClasspathClassLoader("Classpath", new ClasspathClassLoader(new ArrayList<>(), parent)))
                .getResource("/module-info.class");

        // Assert
        verify(urlStreamHandlerFactory).createURLStreamHandler(eq("jar"));
        assertNull(actualResource);
    }

    /**
     * Method under test: {@link ClasspathClassLoader#loadClass(String, boolean)}
     */
    @Test
    void testLoadClass() throws IOException, ClassNotFoundException {
        // Arrange
        ArrayList<File> files = new ArrayList<>();

        // Act and Assert
        assertThrows(IllegalStateException.class,
                () -> (new ClasspathClassLoader("Classpath", new ClasspathClassLoader(files, new MLet()))).loadClass("Name",
                        true));
    }

    /**
     * Method under test: {@link ClasspathClassLoader#close()}
     */
    @Test
    void testClose() throws IOException {
        // Arrange
        URLStreamHandlerFactory urlStreamHandlerFactory = mock(URLStreamHandlerFactory.class);
        when(urlStreamHandlerFactory.createURLStreamHandler(Mockito.<String>any())).thenReturn(null);
        URLClassLoader parent = new URLClassLoader(
                new URL[]{Paths.get(System.getProperty("java.io.tmpdir"), "test.txt").toUri().toURL()},
                new ClasspathClassLoader("Classpath", new MLet()), urlStreamHandlerFactory);

        // Act
        (new ClasspathClassLoader("Classpath", new ClasspathClassLoader(new ArrayList<>(), parent))).close();

        // Assert that nothing has changed
        verify(urlStreamHandlerFactory).createURLStreamHandler(eq("jar"));
    }
}
