/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Locale;
import java.util.Properties;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.Lexer;
import org.antlr.runtime.Token;

import net.sourceforge.pmd.lang.apex.ApexJorjeLogging;
import net.sourceforge.pmd.lang.ast.TokenMgrError;

import apex.jorje.parser.impl.ApexLexer;

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

        ANTLRStringStream ass = new ANTLRStringStream(code.toString());
        ApexLexer lexer = new ApexLexer(ass) {
            @Override
            public void emitErrorMessage(String msg) {
                throw new TokenMgrError(msg, TokenMgrError.LEXICAL_ERROR);
            }
        };

        try {
            Token token = lexer.nextToken();

            while (token.getType() != Token.EOF) {
                if (token.getChannel() != Lexer.HIDDEN) {
                    String tokenText = token.getText();
                    if (!caseSensitive) {
                        tokenText = tokenText.toLowerCase(Locale.ROOT);
                    }
                    TokenEntry tokenEntry = new TokenEntry(tokenText, sourceCode.getFileName(), token.getLine(),
                            token.getCharPositionInLine(), token.getCharPositionInLine() + tokenText.length());
                    tokenEntries.add(tokenEntry);
                }
                token = lexer.nextToken();
            }
        } finally {
            tokenEntries.add(TokenEntry.getEOF());
        }
    }
}
