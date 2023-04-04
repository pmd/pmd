/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionDiscoverer;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;

class LanguageVersionDiscovererTest {

    /**
     * Test on Java file with default options.
     * Always the latest non-preview version will be the default version.
     */
    @Test
    void testJavaFileUsingDefaults() {
        LanguageVersionDiscoverer discoverer = new LanguageVersionDiscoverer(LanguageRegistry.PMD);
        File javaFile = new File("/path/to/MyClass.java");

        LanguageVersion latest = determineLatestNonPreviewVersion();

        LanguageVersion languageVersion = discoverer.getDefaultLanguageVersionForFile(javaFile);
        assertEquals(latest, languageVersion, "Latest language version must be default");
    }

    private LanguageVersion determineLatestNonPreviewVersion() {
        LanguageVersion latest = null;
        for (LanguageVersion lv : JavaLanguageModule.getInstance().getVersions()) {
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
    void testJavaFileUsing14() {
        LanguageVersionDiscoverer discoverer = new LanguageVersionDiscoverer(LanguageRegistry.PMD);
        Language java = JavaLanguageModule.getInstance();
        discoverer.setDefaultLanguageVersion(java.getVersion("1.4"));
        File javaFile = new File("/path/to/MyClass.java");

        LanguageVersion languageVersion = discoverer.getDefaultLanguageVersionForFile(javaFile);
        assertEquals(java.getVersion("1.4"), languageVersion);
    }

    @Test
    void testLanguageVersionDiscoverer() {
        PMDConfiguration configuration = new PMDConfiguration();
        LanguageVersionDiscoverer languageVersionDiscoverer = configuration.getLanguageVersionDiscoverer();
        Language java = JavaLanguageModule.getInstance();
        assertEquals(determineLatestNonPreviewVersion(),
                     languageVersionDiscoverer.getDefaultLanguageVersion(java),
                     "Default Java version");
        configuration
                .setDefaultLanguageVersion(java.getVersion("1.5"));
        assertEquals(java.getVersion("1.5"),
                     languageVersionDiscoverer.getDefaultLanguageVersion(java),
                     "Modified Java version");
    }
}
