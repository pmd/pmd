/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.scm;

import net.sourceforge.pmd.lang.java.JavaLanguageModule;

public class JavaMinimizerModule extends BaseMinimizerLanguageModule {
    public JavaMinimizerModule() {
        super(new JavaLanguageModule());
    }
}
