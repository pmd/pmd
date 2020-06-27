/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionDiscoverer;
import net.sourceforge.pmd.lang.java.ast.BaseParserTest;

public class LanguageVersionDiscovererTest extends BaseParserTest {

    /**
     * Test on Java file with default options.
     */
    @Test
    public void testJavaFileUsingDefaults() {
        LanguageVersionDiscoverer discoverer = new LanguageVersionDiscoverer(languageRegistry());
        File javaFile = new File("/path/to/MyClass.java");

        LanguageVersion languageVersion = discoverer.getDefaultLanguageVersionForFile(javaFile);
        assertEquals("LanguageVersion must be Java 14 !", java.getLanguage().getVersion("14"), languageVersion);
    }

    /**
     * Test on Java file with Java version set to 1.4.
     */
    @Test
    public void testJavaFileUsing14() {
        LanguageVersionDiscoverer discoverer = new LanguageVersionDiscoverer(languageRegistry());
        discoverer.setDefaultLanguageVersion(java.getLanguage().getVersion("1.4"));
        File javaFile = new File("/path/to/MyClass.java");

        LanguageVersion languageVersion = discoverer.getDefaultLanguageVersionForFile(javaFile);
        assertEquals("LanguageVersion must be Java 1.4!",
                     java.getLanguage().getVersion("1.4"), languageVersion);
    }

    @Test
    public void testLanguageVersionDiscoverer() {
        PMDConfiguration configuration = new PMDConfiguration(languageRegistry());
        LanguageVersionDiscoverer languageVersionDiscoverer = configuration.getLanguageVersionDiscoverer();
        Language javaLang = java.getLanguage();
        assertEquals("Default Java version", javaLang.getVersion("14"), languageVersionDiscoverer.getDefaultLanguageVersion(javaLang));
        configuration.setDefaultLanguageVersion(javaLang.getVersion("1.5"));
        assertEquals("Modified Java version", javaLang.getVersion("1.5"), languageVersionDiscoverer.getDefaultLanguageVersion(javaLang));
    }
}
