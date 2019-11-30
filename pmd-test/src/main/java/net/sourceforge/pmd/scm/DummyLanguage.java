/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.scm;

import net.sourceforge.pmd.lang.Parser;

public class DummyLanguage extends BaseMinimizerLanguageModule {
    public DummyLanguage() {
        super("dummy");
    }

    @Override
    public Parser getParser() {
        return null;
    }
}
