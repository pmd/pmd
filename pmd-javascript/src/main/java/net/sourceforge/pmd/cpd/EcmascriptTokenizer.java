/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import net.sourceforge.pmd.cpd.token.JavaCCTokenFilter;
import net.sourceforge.pmd.cpd.token.TokenFilter;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.ast.TokenMgrError;
import net.sourceforge.pmd.lang.ecmascript.EcmascriptLanguageModule;
import net.sourceforge.pmd.lang.ecmascript5.ast.Ecmascript5ParserConstants;
import net.sourceforge.pmd.lang.ecmascript5.ast.Token;

/**
 * The Ecmascript Tokenizer
 */
public class EcmascriptTokenizer implements Tokenizer {

    @Override
    public void tokenize(SourceCode sourceCode, Tokens tokenEntries) {
        StringBuilder buffer = sourceCode.getCodeBuffer();
        try (Reader reader = new StringReader(buffer.toString())) {
            LanguageVersionHandler languageVersionHandler = LanguageRegistry.getLanguage(EcmascriptLanguageModule.NAME)
                    .getDefaultVersion().getLanguageVersionHandler();
            TokenFilter tokenFilter = new JavaCCTokenFilter(languageVersionHandler
                    .getParser(languageVersionHandler.getDefaultParserOptions())
                    .getTokenManager(sourceCode.getFileName(), reader));
            Token currentToken = (Token) tokenFilter.getNextToken();
            while (currentToken != null) {
                tokenEntries.add(
                        new TokenEntry(getTokenImage(currentToken), sourceCode.getFileName(), currentToken.beginLine));
                currentToken = (Token) tokenFilter.getNextToken();
            }
            tokenEntries.add(TokenEntry.getEOF());
            System.err.println("Added " + sourceCode.getFileName());
        } catch (TokenMgrError err) {
            err.printStackTrace();
            System.err.println("Skipping " + sourceCode.getFileName() + " due to parse error");
            tokenEntries.add(TokenEntry.getEOF());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getTokenImage(Token token) {
        // Remove line continuation characters from string literals
        if (token.kind == Ecmascript5ParserConstants.STRING_LITERAL
                || token.kind == Ecmascript5ParserConstants.UNTERMINATED_STRING_LITERAL) {
            return token.image.replaceAll("(?<!\\\\)\\\\(\\r\\n|\\r|\\n)", "");
        }
        return token.image;
    }
}
