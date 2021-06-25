/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import org.antlr.v4.runtime.CharStream;

import net.sourceforge.pmd.cpd.token.AntlrToken;
import net.sourceforge.pmd.cpd.token.AntlrTokenFilter;
import net.sourceforge.pmd.lang.antlr.AntlrTokenManager;
import net.sourceforge.pmd.lang.kotlin.antlr4.Kotlin;

/**
 * The Kotlin Tokenizer
 */
public class KotlinTokenizer extends AntlrTokenizer {

    @Override
    protected AntlrTokenManager getLexerForSource(SourceCode sourceCode) {
        CharStream charStream = AntlrTokenizer.getCharStreamFromSourceCode(sourceCode);
        return new AntlrTokenManager(new Kotlin(charStream), sourceCode.getFileName());
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
            if (type == Kotlin.PACKAGE || type == Kotlin.IMPORT) {
                discardingPackageAndImport = true;
            } else if (discardingPackageAndImport && (type == Kotlin.SEMICOLON || type == Kotlin.NL)) {
                discardingPackageAndImport = false;
            }
        }

        private void skipNewLines(final AntlrToken currentToken) {
            discardingNL = currentToken.getKind() == Kotlin.NL;
        }

        @Override
        protected boolean isLanguageSpecificDiscarding() {
            return discardingPackageAndImport || discardingNL;
        }
    }
}
