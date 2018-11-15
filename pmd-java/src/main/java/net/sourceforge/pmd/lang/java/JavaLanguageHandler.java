/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java;

import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;

public class JavaLanguageHandler extends AbstractJavaHandler {
    private final int jdkVersion;

    public JavaLanguageHandler(int jdkVersion) {
        this.jdkVersion = jdkVersion;
    }


    public final int getJdkVersion() {
        return jdkVersion;
    }


    @Override
    public Parser getParser(ParserOptions parserOptions) {
        return new JavaLanguageParser(jdkVersion, parserOptions);
    }
}
