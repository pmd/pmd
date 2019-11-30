/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.scm;

import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.java.JavaLanguageParser;

public class JavaMinimizerModule extends BaseMinimizerLanguageModule {
    public JavaMinimizerModule() {
        super("java");
    }

    @Override
    public Parser getParser() {
        return new JavaLanguageParser(13, new ParserOptions());
    }
}
