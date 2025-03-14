/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.dart.cpd;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;

import net.sourceforge.pmd.cpd.impl.AntlrCpdLexer;
import net.sourceforge.pmd.cpd.impl.AntlrTokenFilter;
import net.sourceforge.pmd.cpd.impl.BaseTokenFilter;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrToken;
import net.sourceforge.pmd.lang.dart.ast.DartLexer;

/**
 * The Dart Tokenizer
 *
 * <p>Note: This class has been called DartTokenizer in PMD 6</p>.
 */
public class DartCpdLexer extends AntlrCpdLexer {

    @Override
    protected Lexer getLexerForSource(CharStream charStream) {
        return new DartLexer(charStream);
    }

    @Override
    protected TokenManager<AntlrToken> filterTokenStream(TokenManager<AntlrToken> tokenManager) {
        return new DartTokenFilter(tokenManager);
    }

    /**
     * The {@link DartTokenFilter} extends the {@link AntlrTokenFilter} to discard
     * Dart-specific tokens.
     * <p>
     * By default, it discards package and import statements, and
     * enables comment-based CPD suppression.
     * </p>
     */
    private static class DartTokenFilter extends BaseTokenFilter<AntlrToken> {
        private boolean discardingLibraryAndImport = false;
        private boolean discardingNL = false;
        private boolean discardingSemicolon = false;

        /* default */ DartTokenFilter(final TokenManager<AntlrToken> tokenManager) {
            super(tokenManager);
        }

        @Override
        protected void analyzeToken(final AntlrToken currentToken) {
            skipLibraryAndImport(currentToken);
            skipNewLines(currentToken);
            skipSemicolons(currentToken);
        }

        private void skipLibraryAndImport(final AntlrToken currentToken) {
            final int type = currentToken.getKind();
            if (type == DartLexer.LIBRARY || type == DartLexer.IMPORT) {
                discardingLibraryAndImport = true;
            } else if (discardingLibraryAndImport && (type == DartLexer.SEMICOLON || type == DartLexer.NEWLINE)) {
                discardingLibraryAndImport = false;
            }
        }

        private void skipNewLines(final AntlrToken currentToken) {
            discardingNL = currentToken.getKind() == DartLexer.NEWLINE;
        }

        private void skipSemicolons(final AntlrToken currentToken) {
            discardingSemicolon = currentToken.getKind() == DartLexer.SEMICOLON;
        }

        @Override
        protected boolean isLanguageSpecificDiscarding() {
            return discardingLibraryAndImport || discardingNL || discardingSemicolon;
        }
    }
}
