/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;

import net.sourceforge.pmd.lang.ast.TokenMgrError;
import net.sourceforge.pmd.lang.kotlin.antlr4.Kotlin;

/**
 * The Kotlin Tokenizer
 */
public class KotlinTokenizer implements Tokenizer {

    private boolean discardingPackageAndImport = false;

    @Override
    public void tokenize(final SourceCode sourceCode, final Tokens tokenEntries) {
        final StringBuilder buffer = sourceCode.getCodeBuffer();

        try {
            final ANTLRInputStream ais = new ANTLRInputStream(buffer.toString());
            final Kotlin lexer = new Kotlin(ais);

            lexer.removeErrorListeners();
            lexer.addErrorListener(new ErrorHandler());
            Token token = lexer.nextToken();

            while (token.getType() != Token.EOF) {
                analyzeTokenStart(token);
                if (token.getChannel() != Lexer.HIDDEN && token.getType() != Kotlin.NL && !isDiscarding()) {
                    final TokenEntry tokenEntry = new TokenEntry(token.getText(), sourceCode.getFileName(), token.getLine());
                    tokenEntries.add(tokenEntry);
                }
                analyzeTokenEnd(token);
                token = lexer.nextToken();
            }
        } catch (final ANTLRSyntaxError err) {
            // Wrap exceptions of the Kotlin tokenizer in a TokenMgrError, so
            // they are correctly handled
            // when CPD is executed with the '--skipLexicalErrors' command line
            // option
            throw new TokenMgrError("Lexical error in file " + sourceCode.getFileName() + " at line " + err.getLine()
                    + ", column " + err.getColumn() + ".  Encountered: " + err.getMessage(),
                    TokenMgrError.LEXICAL_ERROR);
        } finally {
            tokenEntries.add(TokenEntry.getEOF());
        }
    }

    private boolean isDiscarding() {
        return discardingPackageAndImport;
    }

    private void analyzeTokenStart(final Token currentToken) {
        final int type = currentToken.getType();
        if (type == Kotlin.PACKAGE || type == Kotlin.IMPORT) {
            discardingPackageAndImport = true;
        }
    }

    private void analyzeTokenEnd(final Token currentToken) {
        final int type = currentToken.getType();
        if (discardingPackageAndImport && (type == Kotlin.SEMICOLON || type == Kotlin.NL)) {
            discardingPackageAndImport = false;
        }
    }


    private static class ErrorHandler extends BaseErrorListener {
        @Override
        public void syntaxError(final Recognizer<?, ?> recognizer, final Object offendingSymbol, final int line, final int charPositionInLine,
                final String msg, final RecognitionException ex) {
            throw new ANTLRSyntaxError(msg, line, charPositionInLine, ex);
        }
    }

    private static class ANTLRSyntaxError extends RuntimeException {
        private static final long serialVersionUID = 1L;
        private final int line;
        private final int column;

        ANTLRSyntaxError(final String msg, final int line, final int column, final RecognitionException cause) {
            super(msg, cause);
            this.line = line;
            this.column = column;
        }

        public int getLine() {
            return line;
        }

        public int getColumn() {
            return column;
        }
    }
}
