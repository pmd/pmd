/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java;

import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.java.ast.internal.LanguageLevelChecker;
import net.sourceforge.pmd.lang.java.ast.internal.ReportingStrategy;

public class JavaLanguageHandler extends AbstractJavaHandler {

    private final LanguageLevelChecker<?> levelChecker;

    public JavaLanguageHandler(int jdkVersion) {
        this(jdkVersion, false);
    }

    public JavaLanguageHandler(int jdkVersion, boolean preview) {
        levelChecker = new LanguageLevelChecker<>(jdkVersion, preview, ReportingStrategy.reporterThatThrows());
    }


    @Override
    public Parser getParser(ParserOptions parserOptions) {
        return new JavaLanguageParser(levelChecker, parserOptions);
    }
}
