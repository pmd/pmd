/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.groovy.cpd;

import org.codehaus.groovy.antlr.SourceInfo;
import org.codehaus.groovy.antlr.parser.GroovyLexer;

import net.sourceforge.pmd.cpd.TokenFactory;
import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.lang.ast.TokenMgrError;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.document.CpdCompat;
import net.sourceforge.pmd.lang.document.FileId;

import groovyjarjarantlr.Token;
import groovyjarjarantlr.TokenStream;
import groovyjarjarantlr.TokenStreamException;

/**
 * The Groovy Tokenizer
 */
public class GroovyTokenizer implements Tokenizer {

    @Override
    public void tokenize(TextDocument document, TokenFactory tokens) {
        GroovyLexer lexer = new GroovyLexer(document.newReader());
        TokenStream tokenStream = lexer.plumb();

        try {
            Token token = tokenStream.nextToken();

            while (token.getType() != Token.EOF_TYPE) {
                String tokenText = token.getText();


                int lastCol;
                int lastLine;
                if (token instanceof SourceInfo) {
                    lastCol = ((SourceInfo) token).getColumnLast();
                    lastLine = ((SourceInfo) token).getLineLast();
                } else {
                    // fallback
                    lastCol = token.getColumn() + tokenText.length();
                    lastLine = token.getLine(); // todo inaccurate
                }

                tokens.recordToken(tokenText, token.getLine(), token.getColumn(), lastLine, lastCol);
                token = tokenStream.nextToken();
            }
        } catch (TokenStreamException err) {
            // Wrap exceptions of the Groovy tokenizer in a TokenMgrError, so
            // they are correctly handled
            // when CPD is executed with the '--skipLexicalErrors' command line
            // option
            throw new TokenMgrError(lexer.getLine(), lexer.getColumn(), document.getFileId(), err.getMessage(), err);
        }
    }
}
