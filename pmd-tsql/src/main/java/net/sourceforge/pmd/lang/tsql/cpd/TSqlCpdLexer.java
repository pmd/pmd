/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.tsql.cpd;

import java.util.Locale;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;

import net.sourceforge.pmd.cpd.impl.AntlrCpdLexer;
import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrLexerBehavior;
import net.sourceforge.pmd.lang.tsql.ast.TSqlLexer;

/**
 * <p>Note: This class has been called TSqlTokenizer in PMD 6</p>.
 */
public class TSqlCpdLexer extends AntlrCpdLexer {

    @Override
    protected Lexer getLexerForSource(CharStream charStream) {
        return new TSqlLexer(new CaseChangingCharStream(charStream, true));
    }

    @Override
    protected AntlrLexerBehavior getLexerBehavior() {
        return new AntlrLexerBehavior() {
            @Override
            protected String getTokenImage(Token token) {
                if (token.getType() == TSqlLexer.STRING) {
                    // This path is for case-sensitive tokens
                    return super.getTokenImage(token);
                }
                // normalize case sensitive tokens
                return token.getText().toUpperCase(Locale.ROOT);
            }
        };
    }
}
