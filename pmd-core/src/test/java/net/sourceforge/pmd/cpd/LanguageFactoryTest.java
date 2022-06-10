/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LanguageFactoryTest {

    @Test
    void testSimple() {
        Assertions.assertTrue(LanguageFactory.createLanguage("Cpddummy") instanceof CpddummyLanguage);
        Assertions.assertTrue(LanguageFactory.createLanguage("not_existing_language") instanceof AnyLanguage);
    }
}
