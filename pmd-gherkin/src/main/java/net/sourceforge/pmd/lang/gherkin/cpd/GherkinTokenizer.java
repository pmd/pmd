/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.gherkin.cpd;

import org.antlr.v4.runtime.CharStream;

import net.sourceforge.pmd.cpd.SourceCode;
import net.sourceforge.pmd.cpd.internal.AntlrTokenizer;
import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrTokenManager;
import net.sourceforge.pmd.lang.gherkin.ast.GherkinLexer;

/**
 * The Gherkin Tokenizer.
 */
public class GherkinTokenizer extends AntlrTokenizer {

    @Override
    protected AntlrTokenManager getLexerForSource(SourceCode sourceCode) {
        CharStream charStream = AntlrTokenizer.getCharStreamFromSourceCode(sourceCode);
        return new AntlrTokenManager(new GherkinLexer(charStream), sourceCode.getFileName());
    }
}
