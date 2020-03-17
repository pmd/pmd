/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionDiscoverer;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;

public class LanguageVersionDiscovererTest {

    /**
     * Test on Java file with default options.
     */
    @Test
    public void testJavaFileUsingDefaults() {
        LanguageVersionDiscoverer discoverer = new LanguageVersionDiscoverer();
        File javaFile = new File("/path/to/MyClass.java");

        LanguageVersion languageVersion = discoverer.getDefaultLanguageVersionForFile(javaFile);
        assertEquals("LanguageVersion must be Java 14 !",
                LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("14"), languageVersion);
    }

    /**
     * Test on Java file with Java version set to 1.4.
     */
    @Test
    public void testJavaFileUsing14() {
        LanguageVersionDiscoverer discoverer = new LanguageVersionDiscoverer();
        discoverer.setDefaultLanguageVersion(LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("1.4"));
        File javaFile = new File("/path/to/MyClass.java");

        LanguageVersion languageVersion = discoverer.getDefaultLanguageVersionForFile(javaFile);
        assertEquals("LanguageVersion must be Java 1.4!",
                LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("1.4"), languageVersion);
    }

    @Test
    public void testLanguageVersionDiscoverer() {
        PMDConfiguration configuration = new PMDConfiguration();
        LanguageVersionDiscoverer languageVersionDiscoverer = configuration.getLanguageVersionDiscoverer();
        assertEquals("Default Java version", LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("14"),
                languageVersionDiscoverer
                        .getDefaultLanguageVersion(LanguageRegistry.getLanguage(JavaLanguageModule.NAME)));
        configuration
                .setDefaultLanguageVersion(LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("1.5"));
        assertEquals("Modified Java version", LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("1.5"),
                languageVersionDiscoverer
                        .getDefaultLanguageVersion(LanguageRegistry.getLanguage(JavaLanguageModule.NAME)));
    }
}
