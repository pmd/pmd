/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.internal.util.ClasspathClassLoader;

class JvmLanguagePropertyBundleTest {
    @TempDir
    private Path tempDir;

    @Test
    void setAuxClasspathProperty() throws IOException {
        JvmLanguagePropertyBundle bundle = new JvmLanguagePropertyBundle(DummyLanguageModule.getInstance());

        Path libDir = Files.createDirectories(tempDir.resolve("path/to"));
        Path libJar = Files.createFile(libDir.resolve("lib.jar"));
        bundle.setProperty(JvmLanguagePropertyBundle.AUX_CLASSPATH, libJar.toString());
        assertEquals(libJar.toString(), bundle.getProperty(JvmLanguagePropertyBundle.AUX_CLASSPATH));

        // Note: this tests the deprecated method getAnalysisClassloader, which should use
        // the AUX_CLASSPATH property
        ClassLoader classLoader = bundle.getAnalysisClassLoader();
        assertNotNull(classLoader);
        assertInstanceOf(ClasspathClassLoader.class, classLoader);
        try (ClasspathClassLoader analysisClassLoader = (ClasspathClassLoader) classLoader) {
            assertThat(analysisClassLoader.toString(), containsString(libJar.toString()));
        }
    }

    @Test
    void setAuxClasspathPropertyEmpty() throws IOException {
        JvmLanguagePropertyBundle bundle = new JvmLanguagePropertyBundle(DummyLanguageModule.getInstance());
        assertEquals("", bundle.getProperty(JvmLanguagePropertyBundle.AUX_CLASSPATH), "wrong default value");

        // Note: this tests the deprecated method getAnalysisClassloader, which should use
        // the AUX_CLASSPATH property
        ClassLoader classLoader = bundle.getAnalysisClassLoader();
        assertSame(PMDConfiguration.class.getClassLoader(), classLoader);
        assertNotNull(classLoader);
        assertFalse(classLoader instanceof ClasspathClassLoader);
    }

    @Test
    void setClassLoader() {
        JvmLanguagePropertyBundle bundle = new JvmLanguagePropertyBundle(DummyLanguageModule.getInstance());
        bundle.setClassLoader(JvmLanguagePropertyBundleTest.class.getClassLoader());
        assertSame(JvmLanguagePropertyBundleTest.class.getClassLoader(), bundle.getAnalysisClassLoader());
    }
}
