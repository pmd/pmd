/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.cpd;

import java.io.IOException;
import java.util.Locale;

import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.Lexer;
import org.antlr.runtime.Token;

import net.sourceforge.pmd.cpd.TokenFactory;
import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.lang.apex.ApexJorjeLogging;
import net.sourceforge.pmd.lang.document.TextDocument;

import apex.jorje.parser.impl.ApexLexer;

public class ApexTokenizer implements Tokenizer {

    public ApexTokenizer() {
        ApexJorjeLogging.disableLogging();
    }

    @Override
    public void tokenize(TextDocument document, TokenFactory tokenEntries) throws IOException {

        ANTLRStringStream ass = new ANTLRReaderStream(document.newReader());
        ApexLexer lexer = new ApexLexer(ass) {
            @Override
            public void emitErrorMessage(String msg) {
                throw tokenEntries.makeLexException(getLine(), getCharPositionInLine(), msg, null);
            }
        };

        Token token = lexer.nextToken();

        while (token.getType() != Token.EOF) {
            if (token.getChannel() != Lexer.HIDDEN) {
                String tokenText = token.getText();
                // be case-insensitive
                tokenText = tokenText.toLowerCase(Locale.ROOT);
                tokenEntries.recordToken(
                    tokenText,
                    token.getLine(),
                    token.getCharPositionInLine() + 1,
                    token.getLine(),
                    token.getCharPositionInLine() + tokenText.length() + 1
                );
            }
            token = lexer.nextToken();
        }
    }
}
