/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

/**
 * Sample language for testing LanguageFactory.
 *
 */
public class CpddummyLanguage extends AbstractLanguage {

    public CpddummyLanguage() {
        super("CPD Dummy Language used in tests", "Cpddummy", new AnyTokenizer(), "dummy");
    }
}
