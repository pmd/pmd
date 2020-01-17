/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.StringReader;

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
                TokenEntry tokenEntry = new TokenEntry(tokenText, sourceCode.getFileName(), token.getLine(),
                        token.getColumn(), tokenText.length());

                tokenEntries.add(tokenEntry);
                token = tokenStream.nextToken();
            }
        } catch (TokenStreamException err) {
            // Wrap exceptions of the Groovy tokenizer in a TokenMgrError, so
            // they are correctly handled
            // when CPD is executed with the '--skipLexicalErrors' command line
            // option
            throw new TokenMgrError("Lexical error in file " + sourceCode.getFileName() + " at line " + lexer.getLine()
                    + ", column " + lexer.getColumn() + ".  Encountered: " + err.getMessage(),
                    TokenMgrError.LEXICAL_ERROR);
        } finally {
            tokenEntries.add(TokenEntry.getEOF());
        }
    }
}
