/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import org.antlr.v4.runtime.CharStream;

import net.sourceforge.pmd.lang.antlr.AntlrTokenManager;
import net.sourceforge.pmd.lang.swift.antlr4.SwiftLexer;

/**
 * SwiftTokenizer
 */
public class SwiftTokenizer extends AntlrTokenizer {

    @Override
    protected AntlrTokenManager getLexerForSource(final SourceCode sourceCode) {
        CharStream charStream = AntlrTokenizer.getCharStreamFromSourceCode(sourceCode);
        return new AntlrTokenManager(new SwiftLexer(charStream), sourceCode.getFileName());
    }
}
