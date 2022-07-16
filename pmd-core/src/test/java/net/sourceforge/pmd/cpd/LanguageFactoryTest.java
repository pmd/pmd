/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class LanguageFactoryTest {

    @Test
    void testSimple() {
        assertTrue(LanguageFactory.createLanguage("Cpddummy") instanceof CpddummyLanguage);
        assertTrue(LanguageFactory.createLanguage("not_existing_language") instanceof AnyLanguage);
    }
}
