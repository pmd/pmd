/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.cpd;

import java.io.IOException;
import java.util.Locale;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;

import net.sourceforge.pmd.cpd.CpdLexer;
import net.sourceforge.pmd.cpd.TokenFactory;
import net.sourceforge.pmd.lang.document.TextDocument;

import com.nawforce.apexparser.ApexLexer;

public class ApexCpdLexer implements CpdLexer {
    @Override
    public void tokenize(TextDocument document, TokenFactory tokenEntries) throws IOException {

        CharStream charStream = CharStreams.fromReader(document.newReader());
        ApexLexer lexer = new ApexLexer(charStream);

        Token token = lexer.nextToken();

        while (token.getType() != Token.EOF) {
            if (token.getChannel() == ApexLexer.DEFAULT_TOKEN_CHANNEL) { // exclude WHITESPACE_CHANNEL and COMMENT_CHANNEL
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
