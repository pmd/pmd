/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import org.antlr.v4.runtime.CharStream;

import net.sourceforge.pmd.cpd.internal.AntlrTokenizer;
import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrTokenManager;
import net.sourceforge.pmd.lang.swift.ast.SwiftTreeLexer;

/**
 * SwiftTokenizer
 */
public class SwiftTokenizer extends AntlrTokenizer {

    @Override
    protected AntlrTokenManager getLexerForSource(final SourceCode sourceCode) {
        CharStream charStream = AntlrTokenizer.getCharStreamFromSourceCode(sourceCode);
        return new AntlrTokenManager(new SwiftTreeLexer(charStream), sourceCode.getFileName());
    }
}
