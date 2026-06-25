/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.internal.util.ClasspathClassLoader;
import net.sourceforge.pmd.internal.util.ClasspathClassLoaderTestUtil;

class PmdClasspathConfigTest {

    @Test
    void testCpConfigJustClassloader() throws Exception {
        FakeClassloader cl = mock(FakeClassloader.class);
        PmdClasspathConfig config = PmdClasspathConfig.thisClassLoaderWillNotBeClosedByPmd(cl);
        ClasspathClassLoaderTestUtil.assertClasspathContainsExactly(config.getClasspath());

        try (PmdClasspathConfig.OpenClasspath cp = config.open()) {
            assertInstanceOf(ClasspathClassLoader.class, cp.classLoader());
            assertSame(cl, cp.classLoader().getParent());
            assertTrue(cp.shouldClose());
        }

        verify(cl, never()).close();
    }

    @Test
    void testCpConfigPrependEmpty() throws Exception {
        FakeClassloader cl = mock(FakeClassloader.class);
        PmdClasspathConfig config = PmdClasspathConfig.thisClassLoaderWillNotBeClosedByPmd(cl);
        config = config.prependClasspath("   ");

        ClasspathClassLoaderTestUtil.assertClasspathContainsExactly(config.getClasspath());

        try (PmdClasspathConfig.OpenClasspath cp = config.open()) {
            assertInstanceOf(ClasspathClassLoader.class, cp.classLoader());
            assertSame(cl, cp.classLoader().getParent());
            assertTrue(cp.shouldClose());
        }

        verify(cl, never()).close();
    }

    @Test
    void testCpConfigPrependSomething() throws Exception {
        FakeClassloader cl = mock(FakeClassloader.class);
        PmdClasspathConfig config = PmdClasspathConfig.thisClassLoaderWillNotBeClosedByPmd(cl);
        config = config.prependClasspath("a/foo.jar");

        ClasspathClassLoaderTestUtil.assertClasspathContainsExactly(config.getClasspath(),
            Paths.get("a/foo.jar").toAbsolutePath().toUri().getPath());

        try (PmdClasspathConfig.OpenClasspath cp = config.open()) {
            assertNotSame(cl, cp.classLoader());
            assertInstanceOf(ClasspathClassLoader.class, cp.classLoader());
            assertTrue(cp.shouldClose());
            cp.findResource("something");
        }

        verify(cl, never()).close();
        verify(cl).getResource("something");
    }


    @Test
    void testEquals() {
        PmdClasspathConfig defaultConfig = new PMDConfiguration().getAnalysisClasspath();
        assertNotSame(defaultConfig, PmdClasspathConfig.defaultClasspath());
        assertEquals(defaultConfig, PmdClasspathConfig.defaultClasspath());
    }


    static class FakeClassloader extends ClassLoader implements AutoCloseable {

        @Override
        public void close() throws Exception {

        }
    }
}
