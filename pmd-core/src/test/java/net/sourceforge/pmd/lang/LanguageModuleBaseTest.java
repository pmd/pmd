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
    }

    @Test
    void testVersions() {
        LanguageModuleBase lang = makeLanguage(LanguageMetadata.withId("dumdum").name("Name").extensions("o").addDefaultVersion("abc"));
        assertThat(lang.getDefaultVersion(), equalTo(lang.getVersion("abc")));
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
        return new LanguageModuleBase(meta) {
            @Override
            public LanguageProcessor createProcessor(LanguagePropertyBundle bundle) {
                throw new UnsupportedOperationException("fake instance");
            }
        };
    }


    private static void assertInvalidId(String id) {
        assertThrows(IllegalArgumentException.class, () -> LanguageMetadata.withId(id));
    }
}
