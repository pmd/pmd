/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.tsql.cpd;

import java.util.Locale;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;

import net.sourceforge.pmd.cpd.impl.AntlrCpdLexer;
import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrToken;
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
    protected String getImage(AntlrToken token) {
        if (token.getKind() == TSqlLexer.STRING) {
            // This path is for case-sensitive tokens
            return token.getImage();
        }
        // normalize case-insensitive tokens
        return token.getImage().toUpperCase(Locale.ROOT);
    }
}
