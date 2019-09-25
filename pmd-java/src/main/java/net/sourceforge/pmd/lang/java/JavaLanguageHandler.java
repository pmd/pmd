/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java;

import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.java.ast.internal.LanguageLevelChecker;

public class JavaLanguageHandler extends AbstractJavaHandler {
    private final int jdkVersion;
    private final boolean preview;
    private final LanguageLevelChecker levelChecker;

    public JavaLanguageHandler(int jdkVersion) {
        this(jdkVersion, false);
    }

    public JavaLanguageHandler(int jdkVersion, boolean preview) {
        this.jdkVersion = jdkVersion;
        this.preview = preview;
        levelChecker = LanguageLevelChecker.checkerThatThrows(jdkVersion, preview);
    }


    @Override
    public Parser getParser(ParserOptions parserOptions) {
        return new JavaLanguageParser(levelChecker, parserOptions);
    }
}
