/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.PmdContextualizedTest;

class LanguageRegistryTest extends PmdContextualizedTest {

    @Test
    public void getDefaultVersionLanguageTest() {
        Language dummy = languageRegistry().findLanguageByTerseName("dummy");
        LanguageVersion dummy12 = dummy.getVersion("1.2");
        assertNotNull(dummy12);

        LanguageVersion dummyDefault = dummy.getDefaultVersion();
        assertNotNull(dummyDefault);

        assertNotSame(dummy12, dummyDefault);
    }

    @Test
    public void getLanguageVersionByAliasTest() {
        Language dummy = languageRegistry().findLanguageByTerseName("dummy");

        LanguageVersion dummy17 = dummy.getVersion("1.7");
        assertNotNull(dummy17);
        assertEquals("1.7", dummy17.getVersion());

        LanguageVersion dummy7 = dummy.getVersion("7");
        assertNotNull(dummy7);
        assertEquals("1.7", dummy17.getVersion());
        assertSame(dummy17, dummy7);
    }
}
