/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import net.sourceforge.pmd.jsp.ast.JspCharStream;
import net.sourceforge.pmd.jsp.ast.JspParserTokenManager;
import net.sourceforge.pmd.jsp.ast.Token;

import java.io.StringReader;

public class JSPTokenizer implements Tokenizer {

    public void tokenize(SourceCode tokens, Tokens tokenEntries) {
        StringBuffer buffer = tokens.getCodeBuffer();
        JspParserTokenManager tokenMgr = new JspParserTokenManager(new JspCharStream(new StringReader(buffer.toString())));
        Token currentToken = tokenMgr.getNextToken();
        while (currentToken.image.length() > 0) {
            tokenEntries.add(new TokenEntry(String.valueOf(currentToken.kind), tokens.getFileName(), currentToken.beginLine));
            currentToken = tokenMgr.getNextToken();
        }
        tokenEntries.add(TokenEntry.getEOF());
    }
}

