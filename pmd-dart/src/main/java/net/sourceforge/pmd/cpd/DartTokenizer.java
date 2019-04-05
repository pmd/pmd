/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import org.antlr.v4.runtime.CharStream;

import net.sourceforge.pmd.cpd.token.AntlrToken;
import net.sourceforge.pmd.cpd.token.AntlrTokenFilter;
import net.sourceforge.pmd.lang.antlr.AntlrTokenManager;
import net.sourceforge.pmd.lang.dart.antlr4.Dart2Lexer;

/**
 * The Dart Tokenizer
 */
public class DartTokenizer extends AntlrTokenizer {

    @Override
    protected AntlrTokenManager getLexerForSource(SourceCode sourceCode) {
        CharStream charStream = AntlrTokenizer.getCharStreamFromSourceCode(sourceCode);
        return new AntlrTokenManager(new Dart2Lexer(charStream), sourceCode.getFileName());
    }

    @Override
    protected AntlrTokenFilter getTokenFilter(final AntlrTokenManager tokenManager) {
        return new DartTokenFilter(tokenManager);
    }

    /**
     * The {@link DartTokenFilter} extends the {@link AntlrTokenFilter} to discard
     * Dart-specific tokens.
     * <p>
     * By default, it discards package and import statements, and
     * enables annotation-based CPD suppression.
     * </p>
     */
    private static class DartTokenFilter extends AntlrTokenFilter {
        private boolean discardingPackageAndImport = false;
        private boolean discardingNL = false;

        /* default */ DartTokenFilter(final AntlrTokenManager tokenManager) {
            super(tokenManager);
        }

        @Override
        protected void analyzeToken(final AntlrToken currentToken) {
            skipPackageAndImport(currentToken);
            skipNewLines(currentToken);
        }

        private void skipPackageAndImport(final AntlrToken currentToken) {
            final int type = currentToken.getType();
            /*if (type == Dart.PACKAGE || type == Dart.IMPORT) {
                discardingPackageAndImport = true;
            } else if (discardingPackageAndImport && (type == Dart.SEMICOLON || type == Dart.NL)) {
                discardingPackageAndImport = false;
            }*/
        }

        private void skipNewLines(final AntlrToken currentToken) {
            discardingNL = false;//currentToken.getType() == Dart.NL;
        }

        @Override
        protected boolean isLanguageSpecificDiscarding() {
            return discardingPackageAndImport || discardingNL;
        }
    }
}
