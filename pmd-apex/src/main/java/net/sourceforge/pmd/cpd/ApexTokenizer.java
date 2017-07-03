/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Locale;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.Lexer;
import org.antlr.runtime.Token;

import net.sourceforge.pmd.lang.ast.TokenMgrError;

import apex.jorje.parser.impl.ApexLexer;

public class ApexTokenizer implements Tokenizer {

    @Override
    public void tokenize(SourceCode sourceCode, Tokens tokenEntries) {
        StringBuilder code = sourceCode.getCodeBuffer();

        ANTLRStringStream ass = new ANTLRStringStream(code.toString());
        ApexLexer lexer = new ApexLexer(ass) {
            public void emitErrorMessage(String msg) {
                throw new TokenMgrError(msg, TokenMgrError.LEXICAL_ERROR);
            }
        };

        try {
            Token token = lexer.nextToken();

            while (token.getType() != Token.EOF) {
                if (token.getChannel() != Lexer.HIDDEN) {
                    String tokenText = token.getText();
                    // note: old behavior of AbstractTokenizer was, to consider only lowercase
                    tokenText = tokenText.toLowerCase(Locale.ROOT);
                    TokenEntry tokenEntry = new TokenEntry(tokenText, sourceCode.getFileName(), token.getLine());
                    tokenEntries.add(tokenEntry);
                }
                token = lexer.nextToken();
            }
        } finally {
            tokenEntries.add(TokenEntry.getEOF());
        }
    }
}
