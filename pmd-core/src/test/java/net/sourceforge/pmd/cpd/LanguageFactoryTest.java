/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class LanguageFactoryTest {

    @Test
    public void testSimple() {
        assertTrue(LanguageFactory.createLanguage("Cpddummy") instanceof CpddummyLanguage);
        assertTrue(LanguageFactory.createLanguage("not_existing_language") instanceof AnyLanguage);
    }
}
