/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.cpd;

import static org.junit.Assert.assertTrue;
import net.sourceforge.pmd.cpd.*;

import org.junit.Test;
public class LanguageFactoryTest {

    @Test
    public void testSimple() {
        LanguageFactory f = new LanguageFactory();
        assertTrue(f.createLanguage(LanguageFactory.JAVA_KEY) instanceof JavaLanguage);
        assertTrue(f.createLanguage(LanguageFactory.CPP_KEY) instanceof CPPLanguage);
        assertTrue(f.createLanguage(LanguageFactory.C_KEY) instanceof CPPLanguage);
        assertTrue(f.createLanguage(LanguageFactory.PHP_KEY) instanceof PHPLanguage);
        assertTrue(f.createLanguage("ruby") instanceof RubyLanguage);
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(LanguageFactoryTest.class);
    }
}
