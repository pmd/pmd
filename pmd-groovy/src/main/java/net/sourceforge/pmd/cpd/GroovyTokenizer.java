/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.StringReader;

import org.codehaus.groovy.antlr.SourceInfo;
import org.codehaus.groovy.antlr.parser.GroovyLexer;

import net.sourceforge.pmd.lang.ast.TokenMgrError;

import groovyjarjarantlr.Token;
import groovyjarjarantlr.TokenStream;
import groovyjarjarantlr.TokenStreamException;

/**
 * The Grooovy Tokenizer
 */
public class GroovyTokenizer implements Tokenizer {

    @Override
    public void tokenize(SourceCode sourceCode, Tokens tokenEntries) {
        StringBuilder buffer = sourceCode.getCodeBuffer();

        GroovyLexer lexer = new GroovyLexer(new StringReader(buffer.toString()));
        TokenStream tokenStream = lexer.plumb();

        try {
            Token token = tokenStream.nextToken();

            while (token.getType() != Token.EOF_TYPE) {
                String tokenText = token.getText();


                int lastCol;
                if (token instanceof SourceInfo) {
                    lastCol = ((SourceInfo) token).getColumnLast() - 1;
                    if (lastCol == 0) {
                        // newline
                        lastCol = token.getColumn() + 1;
                    }
                } else {
                    // fallback
                    lastCol = token.getColumn() + tokenText.length();
                }
                TokenEntry tokenEntry = new TokenEntry(tokenText, sourceCode.getFileName(), token.getLine(), token.getColumn(), lastCol);

                tokenEntries.add(tokenEntry);
                token = tokenStream.nextToken();
            }
        } catch (TokenStreamException err) {
            // Wrap exceptions of the Groovy tokenizer in a TokenMgrError, so
            // they are correctly handled
            // when CPD is executed with the '--skipLexicalErrors' command line
            // option
            throw new TokenMgrError(lexer.getLine(), lexer.getColumn(), sourceCode.getFileName(), err.getMessage(), err);
        } finally {
            tokenEntries.add(TokenEntry.getEOF());
        }
    }
}
