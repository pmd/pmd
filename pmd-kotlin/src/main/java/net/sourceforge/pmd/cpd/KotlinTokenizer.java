/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;

import net.sourceforge.pmd.cpd.token.AntlrToken;
import net.sourceforge.pmd.lang.antlr.AntlrTokenManager;
import net.sourceforge.pmd.lang.kotlin.antlr4.Kotlin;

/**
 * The Kotlin Tokenizer
 */
public class KotlinTokenizer extends AntlrTokenizer {

    private boolean discardingPackageAndImport = false;

    @Override
    protected AntlrTokenManager getLexerForSource(SourceCode sourceCode) {
        CharStream charStream = AntlrTokenizer.getCharStreamFromSourceCode(sourceCode);
        final Lexer lexer = new Kotlin(charStream);
        final AntlrTokenManager tokenManager = new AntlrTokenManager(lexer, sourceCode.getFileName()) {
            @Override
            public Object getNextToken() {
                AntlrToken nextToken;
                boolean done = false;
                do {
                    nextToken = (AntlrToken) super.getNextToken();
                    analyzeTokenStart(nextToken);
                    if (!nextToken.isHidden() && nextToken.getType() != Kotlin.NL && !isDiscarding()) {
                        done = true;
                    }
                    analyzeTokenEnd(nextToken);
                } while (!done && nextToken.getType() != Token.EOF);
                return nextToken;
            }
        };
        return tokenManager;
    }

    private boolean isDiscarding() {
        return discardingPackageAndImport;
    }

    private void analyzeTokenStart(final AntlrToken currentToken) {
        final int type = currentToken.getType();
        if (type == Kotlin.PACKAGE || type == Kotlin.IMPORT) {
            discardingPackageAndImport = true;
        }
    }

    private void analyzeTokenEnd(final AntlrToken currentToken) {
        final int type = currentToken.getType();
        if (discardingPackageAndImport && (type == Kotlin.SEMICOLON || type == Kotlin.NL)) {
            discardingPackageAndImport = false;
        }
    }
}
