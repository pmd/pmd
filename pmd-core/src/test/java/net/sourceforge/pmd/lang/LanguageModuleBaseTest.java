/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.LanguageModuleBase.LanguageMetadata;
import net.sourceforge.pmd.lang.impl.SimpleLanguageModuleBase;

/**
 * @author Clément Fournier
 */
class LanguageModuleBaseTest {


    @Test
    void testInvalidId() {
        assertInvalidId("");
        assertInvalidId("two words");
        assertInvalidId("CapitalLetters");
        assertInvalidId("C");
        assertInvalidId("ab-c");
        assertThrows(NullPointerException.class, () -> LanguageMetadata.withId(null));

        Exception e = assertThrows(IllegalArgumentException.class, () -> LanguageMetadata.withId("dummy").addVersion(""),
                "Empty versions should not be allowed.");
        assertEquals("Invalid version name: ''", e.getMessage());
        assertThrows(IllegalArgumentException.class, () -> LanguageMetadata.withId("dummy").addVersion(" "),
                "Empty versions should not be allowed.");
        assertThrows(IllegalArgumentException.class, () -> LanguageMetadata.withId("dummy").addVersion(null),
                "Empty versions should not be allowed.");
        assertThrows(IllegalArgumentException.class, () -> LanguageMetadata.withId("dummy").addVersion("1.0", ""),
                "Empty versions should not be allowed.");
    }

    @Test
    void testVersions() {
        LanguageModuleBase lang = makeLanguage(LanguageMetadata.withId("dumdum").name("Name").extensions("o").addDefaultVersion("abc"));
        assertThat(lang.getDefaultVersion(), equalTo(lang.getVersion("abc")));
    }

    @Test
    void testMissingVersions() {
        Exception e = assertThrows(IllegalStateException.class, () -> makeLanguage(LanguageMetadata.withId("dumdum").name("Name").extensions("o")),
                "Languages without versions should not be allowed.");
        assertEquals("No versions for 'dumdum'", e.getMessage());
    }

    @Test
    void testNoExtensions() {
        Exception ex = assertThrows(IllegalStateException.class, () -> makeLanguage(LanguageMetadata.withId("dumdum").name("Name").addVersion("abc")));
        assertThat(ex.getMessage(), containsString("extension"));
    }

    @Test
    void testShortNameDefault() {
        LanguageMetadata meta = LanguageMetadata.withId("java").name("Java");

        assertEquals("Java", meta.getShortName());
    }

    @Test
    void testInvalidDependency() {
        LanguageMetadata meta = LanguageMetadata.withId("java").name("Java");

        assertThrows(IllegalArgumentException.class, () -> meta.dependsOnLanguage("not an id"));
    }

    private static LanguageModuleBase makeLanguage(LanguageMetadata meta) {
        return new SimpleLanguageModuleBase(meta, p -> {
            throw new UnsupportedOperationException("fake instance");
        });
    }


    private static void assertInvalidId(String id) {
        assertThrows(IllegalArgumentException.class, () -> LanguageMetadata.withId(id));
    }
}
