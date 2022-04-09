/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;

import net.sourceforge.pmd.cpd.internal.AntlrTokenizer;
import net.sourceforge.pmd.cpd.token.AntlrTokenFilter;
import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrToken;
import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrTokenManager;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinLexer;

/**
 * The Kotlin Tokenizer
 */
public class KotlinTokenizer extends AntlrTokenizer {

    @Override
    protected Lexer getLexerForSource(CharStream charStream) {
        return new KotlinLexer(charStream);
    }

    @Override
    protected AntlrTokenFilter getTokenFilter(final AntlrTokenManager tokenManager) {
        return new KotlinTokenFilter(tokenManager);
    }

    /**
     * The {@link KotlinTokenFilter} extends the {@link AntlrTokenFilter} to discard
     * Kotlin-specific tokens.
     * <p>
     * By default, it discards package and import statements, and
     * enables annotation-based CPD suppression.
     * </p>
     */
    private static class KotlinTokenFilter extends AntlrTokenFilter {
        private boolean discardingPackageAndImport = false;
        private boolean discardingNL = false;

        /* default */ KotlinTokenFilter(final AntlrTokenManager tokenManager) {
            super(tokenManager);
        }

        @Override
        protected void analyzeToken(final AntlrToken currentToken) {
            skipPackageAndImport(currentToken);
            skipNewLines(currentToken);
        }

        private void skipPackageAndImport(final AntlrToken currentToken) {
            final int type = currentToken.getKind();
            if (type == KotlinLexer.PACKAGE || type == KotlinLexer.IMPORT) {
                discardingPackageAndImport = true;
            } else if (discardingPackageAndImport && (type == KotlinLexer.SEMICOLON || type == KotlinLexer.NL)) {
                discardingPackageAndImport = false;
            }
        }

        private void skipNewLines(final AntlrToken currentToken) {
            discardingNL = currentToken.getKind() == KotlinLexer.NL;
        }

        @Override
        protected boolean isLanguageSpecificDiscarding() {
            return discardingPackageAndImport || discardingNL;
        }
    }
}
