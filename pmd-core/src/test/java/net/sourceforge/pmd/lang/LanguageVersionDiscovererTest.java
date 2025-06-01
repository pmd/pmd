/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class LanguageVersionDiscovererTest {

    @Test
    void testFileLanguageIsUnknown() {
        DummyLanguageModule lang = DummyLanguageModule.getInstance();
        LanguageRegistry lr = LanguageRegistry.singleton(lang);
        LanguageVersionDiscoverer lvDicoverer = new LanguageVersionDiscoverer(lr);
        LanguageVersion versionForFile =
                lvDicoverer.getDefaultLanguageVersionForFile(new File("file.unknown-extension"));

        assertNull(versionForFile);
    }

    @Test
    void testFileLanguageIsDetected() {
        DummyLanguageModule lang = DummyLanguageModule.getInstance();
        LanguageRegistry lr = LanguageRegistry.singleton(lang);
        LanguageVersionDiscoverer lvDicoverer = new LanguageVersionDiscoverer(lr);
        LanguageVersion versionForFile = lvDicoverer.getDefaultLanguageVersionForFile(new File("file.txt"));

        assertNotNull(versionForFile);
        assertSame(lang, versionForFile.getLanguage());
        assertEquals(lang.getDefaultVersion().getVersion(), versionForFile.getVersion());
    }

    @Test
    void testDialectTakesPrecedence() {
        DummyLanguageModule lang = DummyLanguageModule.getInstance();
        DummyLanguageDialectModule dialect = DummyLanguageDialectModule.getInstance();
        Set<Language> langSet = new HashSet<>();
        langSet.add(lang);
        langSet.add(dialect);

        LanguageRegistry lr = new LanguageRegistry(langSet);
        LanguageVersionDiscoverer lvDicoverer = new LanguageVersionDiscoverer(lr);
        LanguageVersion versionForFile = lvDicoverer.getDefaultLanguageVersionForFile(new File("file.txt"));

        assertNotNull(versionForFile);
        assertSame(dialect, versionForFile.getLanguage());
        assertEquals(dialect.getDefaultVersion().getVersion(), versionForFile.getVersion());
    }
}
