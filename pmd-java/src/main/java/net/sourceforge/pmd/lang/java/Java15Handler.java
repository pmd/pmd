/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java;

import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;

public class Java15Handler extends AbstractJavaHandler {

    public Parser getParser(ParserOptions parserOptions) {
        return new Java15Parser(parserOptions);
    }
}
