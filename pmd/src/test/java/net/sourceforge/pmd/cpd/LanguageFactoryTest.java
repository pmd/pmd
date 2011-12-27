/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
public class LanguageFactoryTest {

    @Test
    public void testSimple() {
        LanguageFactory f = new LanguageFactory();
        assertTrue(f.createLanguage("java") instanceof JavaLanguage);
        assertTrue(f.createLanguage("cpp") instanceof CPPLanguage);
        assertTrue(f.createLanguage("c") instanceof CPPLanguage);
        assertTrue(f.createLanguage("php") instanceof PHPLanguage);
        assertTrue(f.createLanguage("ruby") instanceof RubyLanguage);
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(LanguageFactoryTest.class);
    }
}
