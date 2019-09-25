/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java;

import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;

public class JavaLanguageHandler extends AbstractJavaHandler {
    private final int jdkVersion;
    private final boolean preview;

    public JavaLanguageHandler(int jdkVersion) {
        this(jdkVersion, false);
    }

    public JavaLanguageHandler(int jdkVersion, boolean preview) {
        this.jdkVersion = jdkVersion;
        this.preview = preview;
    }

    @Override
    public Parser getParser(ParserOptions parserOptions) {
        return new JavaLanguageParser(jdkVersion, preview, parserOptions);
    }
}
