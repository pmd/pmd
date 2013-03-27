/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import java.io.StringReader;

import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.TokenMgrError;
import net.sourceforge.pmd.lang.cpp.ast.Token;

import org.apache.commons.io.IOUtils;

public class CPPTokenizer implements Tokenizer {

	public void tokenize(SourceCode sourceCode, Tokens tokenEntries) {
		StringBuilder buffer = sourceCode.getCodeBuffer();
		StringReader reader = null;
		try {
			LanguageVersionHandler languageVersionHandler = LanguageVersion.CPP.getLanguageVersionHandler();
			reader = new StringReader(buffer.toString());
			TokenManager tokenManager = languageVersionHandler.getParser(
					languageVersionHandler.getDefaultParserOptions())
					.getTokenManager(sourceCode.getFileName(), reader);
			Token currentToken = (Token) tokenManager.getNextToken();
			while (currentToken.image.length() > 0) {
				tokenEntries.add(new TokenEntry(currentToken.image, sourceCode.getFileName(), currentToken.beginLine));
				currentToken = (Token) tokenManager.getNextToken();
			}
			tokenEntries.add(TokenEntry.getEOF());
			System.err.println("Added " + sourceCode.getFileName());
		} catch (TokenMgrError err) {
			err.printStackTrace();
			System.err.println("Skipping " + sourceCode.getFileName() + " due to parse error");
			tokenEntries.add(TokenEntry.getEOF());
		} finally {
			IOUtils.closeQuietly(reader);
		}
	}
}
