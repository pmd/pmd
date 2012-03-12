/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import java.io.StringReader;
import java.util.Properties;

import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.java.ast.JavaParserConstants;
import net.sourceforge.pmd.lang.java.ast.Token;

public class JavaTokenizer implements Tokenizer {

    public static final String IGNORE_LITERALS = "ignore_literals";
    public static final String IGNORE_IDENTIFIERS = "ignore_identifiers";

    private boolean ignoreLiterals;
    private boolean ignoreIdentifiers;

    public void setProperties(Properties properties) {
        ignoreLiterals = Boolean.parseBoolean(properties.getProperty(IGNORE_LITERALS, "false"));
        ignoreIdentifiers = Boolean.parseBoolean(properties.getProperty(IGNORE_IDENTIFIERS, "false"));
    }

    public void tokenize(SourceCode sourceCode, Tokens tokenEntries) {
	StringBuilder buffer = sourceCode.getCodeBuffer();

	/*
	I'm doing a sort of State pattern thing here where
	this goes into "discarding" mode when it hits an import or package
	keyword and goes back into "accumulate mode" when it hits a semicolon.
	This could probably be turned into some objects.
	*/
	// Note that Java version is irrelevant for tokenizing
	LanguageVersionHandler languageVersionHandler = LanguageVersion.JAVA_14.getLanguageVersionHandler();
	String fileName = sourceCode.getFileName();
	TokenManager tokenMgr = languageVersionHandler.getParser(languageVersionHandler.getDefaultParserOptions()).getTokenManager(
		fileName, new StringReader(buffer.toString()));
	Token currentToken = (Token) tokenMgr.getNextToken();
	boolean inDiscardingState = false;
	while (currentToken.image.length() > 0) {
	    if (currentToken.kind == JavaParserConstants.IMPORT || currentToken.kind == JavaParserConstants.PACKAGE) {
		inDiscardingState = true;
		currentToken = (Token) tokenMgr.getNextToken();
		continue;
	    }

	    if (inDiscardingState && currentToken.kind == JavaParserConstants.SEMICOLON) {
		inDiscardingState = false;
	    }

	    if (inDiscardingState) {
		currentToken = (Token) tokenMgr.getNextToken();
		continue;
	    }

	    if (currentToken.kind != JavaParserConstants.SEMICOLON) {
		String image = currentToken.image;
		if (ignoreLiterals
			&& (currentToken.kind == JavaParserConstants.STRING_LITERAL
				|| currentToken.kind == JavaParserConstants.CHARACTER_LITERAL
				|| currentToken.kind == JavaParserConstants.DECIMAL_LITERAL || currentToken.kind == JavaParserConstants.FLOATING_POINT_LITERAL)) {
		    image = String.valueOf(currentToken.kind);
		}
		if (ignoreIdentifiers && currentToken.kind == JavaParserConstants.IDENTIFIER) {
		    image = String.valueOf(currentToken.kind);
		}
		tokenEntries.add(new TokenEntry(image, fileName, currentToken.beginLine));
	    }

	    currentToken = (Token) tokenMgr.getNextToken();
	}
	tokenEntries.add(TokenEntry.getEOF());
    }

    public void setIgnoreLiterals(boolean ignore) {
	this.ignoreLiterals = ignore;
    }

    public void setIgnoreIdentifiers(boolean ignore) {
	this.ignoreIdentifiers = ignore;
    }
}
