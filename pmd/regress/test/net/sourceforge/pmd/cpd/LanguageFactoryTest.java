/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.cpd;

import junit.framework.TestCase;
import net.sourceforge.pmd.cpd.CPPLanguage;
import net.sourceforge.pmd.cpd.JavaLanguage;
import net.sourceforge.pmd.cpd.LanguageFactory;
import net.sourceforge.pmd.cpd.PHPLanguage;

public class LanguageFactoryTest extends TestCase {

    public void testSimple() {
        LanguageFactory f = new LanguageFactory();
        assertTrue(f.createLanguage(LanguageFactory.JAVA_KEY) instanceof JavaLanguage);
        assertTrue(f.createLanguage(LanguageFactory.CPP_KEY) instanceof CPPLanguage);
        assertTrue(f.createLanguage(LanguageFactory.C_KEY) instanceof CPPLanguage);
        assertTrue(f.createLanguage(LanguageFactory.PHP_KEY) instanceof PHPLanguage);
        try {
            f.createLanguage("fiddlesticks");
            throw new RuntimeException("Should have thrown an exception!");
        } catch (RuntimeException e) {
            // cool
        }

    }
}
