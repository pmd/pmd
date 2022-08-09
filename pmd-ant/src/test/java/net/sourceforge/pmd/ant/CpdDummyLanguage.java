/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.ant;

import net.sourceforge.pmd.cpd.AbstractLanguage;
import net.sourceforge.pmd.cpd.AnyTokenizer;

/**
 * Sample language for testing LanguageFactory.
 *
 */
public class CpdDummyLanguage extends AbstractLanguage {

    public CpdDummyLanguage() {
        super("CPD Dummy Language used in tests", "Cpddummy", new AnyTokenizer(), "dummy");
    }
}
