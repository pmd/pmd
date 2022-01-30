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
     * Always the latest non-preview version will be the default version.
     */
    @Test
    public void testJavaFileUsingDefaults() {
        LanguageVersionDiscoverer discoverer = new LanguageVersionDiscoverer();
        File javaFile = new File("/path/to/MyClass.java");

        LanguageVersion latest = determineLatestNonPreviewVersion();

        LanguageVersion languageVersion = discoverer.getDefaultLanguageVersionForFile(javaFile);
        assertEquals("Latest language version must be default", latest, languageVersion);
    }

    private LanguageVersion determineLatestNonPreviewVersion() {
        LanguageVersion latest = null;
        for (LanguageVersion lv : LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersions()) {
            if (!lv.getName().endsWith("preview")) {
                latest = lv;
            }
        }
        return latest;
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
        assertEquals("Default Java version", determineLatestNonPreviewVersion(),
                languageVersionDiscoverer
                        .getDefaultLanguageVersion(LanguageRegistry.getLanguage(JavaLanguageModule.NAME)));
        configuration
                .setDefaultLanguageVersion(LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("1.5"));
        assertEquals("Modified Java version", LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("1.5"),
                languageVersionDiscoverer
                        .getDefaultLanguageVersion(LanguageRegistry.getLanguage(JavaLanguageModule.NAME)));
    }
}
