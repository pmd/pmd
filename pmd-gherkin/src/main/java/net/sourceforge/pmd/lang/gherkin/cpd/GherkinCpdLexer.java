/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.gherkin.cpd;

import net.sourceforge.pmd.cpd.impl.AntlrCpdLexer;
import net.sourceforge.pmd.lang.gherkin.ast.GherkinLexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;

/**
 * The Gherkin Tokenizer.
 *
 * <p>Note: This class has been called GherkinTokenizer in PMD 6</p>.
 */
public class GherkinCpdLexer extends AntlrCpdLexer {

    @Override
    protected Lexer getLexerForSource(CharStream charStream) {
        return new GherkinLexer(charStream);
    }
}
