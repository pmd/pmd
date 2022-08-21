/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Locale;
import java.util.Properties;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;

import net.sourceforge.pmd.lang.apex.ApexJorjeLogging;

import com.nawforce.apexparser.ApexLexer;
import com.nawforce.apexparser.CaseInsensitiveInputStream;

public class ApexTokenizer implements Tokenizer {

    public ApexTokenizer() {
        ApexJorjeLogging.disableLogging();
    }

    /**
     * If the properties is <code>false</code> (default), then the case of any token
     * is ignored.
     */
    public static final String CASE_SENSITIVE = "net.sourceforge.pmd.cpd.ApexTokenizer.caseSensitive";

    private boolean caseSensitive;

    public void setProperties(Properties properties) {
        caseSensitive = Boolean.parseBoolean(properties.getProperty(CASE_SENSITIVE, "false"));
    }

    @Override
    public void tokenize(SourceCode sourceCode, Tokens tokenEntries) {
        StringBuilder code = sourceCode.getCodeBuffer();

        CharStream charStream = CharStreams.fromString(code.toString());
        ApexLexer lexer = new ApexLexer(new CaseInsensitiveInputStream(charStream));

        try {
            Token token = lexer.nextToken();

            while (token.getType() != Token.EOF) {
                if (token.getChannel() == ApexLexer.DEFAULT_TOKEN_CHANNEL) { // exclude WHITESPACE_CHANNEL and COMMENT_CHANNEL
                    String tokenText = token.getText();
                    if (!caseSensitive) {
                        tokenText = tokenText.toLowerCase(Locale.ROOT);
                    }
                    TokenEntry tokenEntry = new TokenEntry(tokenText, sourceCode.getFileName(), token.getLine(),
                            token.getCharPositionInLine() + 1,
                            token.getCharPositionInLine() + tokenText.length());
                    tokenEntries.add(tokenEntry);
                }
                token = lexer.nextToken();
            }
        } finally {
            tokenEntries.add(TokenEntry.getEOF());
        }
    }
}
