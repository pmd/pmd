/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import net.sourceforge.pmd.lang.swift.antlr4.SwiftLexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;

/**
 * SwiftTokenizer
 */

public class SwiftTokenizer extends AntlrTokenizer {

    @Override
    protected Lexer getLexerForSource(final CharStream charStream) {
        return new SwiftLexer(charStream);
    }
}
