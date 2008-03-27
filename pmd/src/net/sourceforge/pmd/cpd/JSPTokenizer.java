/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import java.io.StringReader;

import net.sourceforge.pmd.Language;
import net.sourceforge.pmd.jsp.ast.Token;
import net.sourceforge.pmd.parsers.TokenManager;

public class JSPTokenizer implements Tokenizer {

    public void tokenize(SourceCode tokens, Tokens tokenEntries) {
	StringBuffer buffer = tokens.getCodeBuffer();
	TokenManager tokenMgr = Language.JSP.getDefaultVersion().getLanguageVersionHandler().getParser()
		.getTokenManager(new StringReader(buffer.toString()));
	Token currentToken = (Token) tokenMgr.getNextToken();
	while (currentToken.image.length() > 0) {
	    tokenEntries.add(new TokenEntry(String.valueOf(currentToken.kind), tokens.getFileName(),
		    currentToken.beginLine));
	    currentToken = (Token) tokenMgr.getNextToken();
	}
	tokenEntries.add(TokenEntry.getEOF());
    }
}
