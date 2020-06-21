/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift;

import net.sourceforge.pmd.lang.AbstractPmdLanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.swift.ast.PmdSwiftParser;

public class SwiftHandler extends AbstractPmdLanguageVersionHandler {

    @Override
    public Parser getParser(final ParserOptions parserOptions) {
        return new PmdSwiftParser(parserOptions);
    }
}
