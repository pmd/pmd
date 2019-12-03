/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.scm;

import net.sourceforge.pmd.test.lang.DummyLanguageModule;

public class DummyLanguage extends BaseMinimizerLanguageModule {
    public DummyLanguage() {
        super(new DummyLanguageModule());
    }
}
