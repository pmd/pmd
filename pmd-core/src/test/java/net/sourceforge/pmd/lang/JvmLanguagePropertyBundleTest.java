/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.internal.util.ClasspathClassLoader;

class JvmLanguagePropertyBundleTest {

    @Test
    void setAuxClasspathProperty() throws IOException {
        JvmLanguagePropertyBundle bundle = new JvmLanguagePropertyBundle(DummyLanguageModule.getInstance());

        String libJar = "path/to/lib.jar";
        bundle.setProperty(JvmLanguagePropertyBundle.AUX_CLASSPATH, libJar);
        assertEquals(libJar, bundle.getProperty(JvmLanguagePropertyBundle.AUX_CLASSPATH));

        // Note: this tests the deprecated method getAnalysisClassloader, which should use
        // the AUX_CLASSPATH property
        ClassLoader classLoader = bundle.getAnalysisClassLoader();
        assertNotNull(classLoader);
        assertInstanceOf(ClasspathClassLoader.class, classLoader);
        try (ClasspathClassLoader analysisClassLoader = (ClasspathClassLoader) classLoader) {
            URL[] urls = analysisClassLoader.getURLs();
            assertEquals(1, urls.length);
            assertEquals(Paths.get(libJar).toUri().toURL().toString(), urls[0].toString());
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
