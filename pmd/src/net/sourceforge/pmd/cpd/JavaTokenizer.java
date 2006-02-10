/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import net.sourceforge.pmd.TargetJDK1_4;
import net.sourceforge.pmd.ast.JavaParserConstants;
import net.sourceforge.pmd.ast.JavaParserTokenManager;
import net.sourceforge.pmd.ast.Token;

import java.io.StringReader;
import java.util.Properties;

public class JavaTokenizer implements Tokenizer {

    public static final String IGNORE_LITERALS = "ignore_literals";
    public static final String IGNORE_IDENTIFIERS = "ignore_identifiers";

    private boolean ignoreLiterals;
    private boolean ignoreIdentifiers;

    public void setProperties(Properties properties) {
        ignoreLiterals = Boolean.valueOf(properties.getProperty(IGNORE_LITERALS, "false")).booleanValue();
        ignoreIdentifiers = Boolean.valueOf(properties.getProperty(IGNORE_IDENTIFIERS, "false")).booleanValue();
    }

    public void tokenize(SourceCode tokens, Tokens tokenEntries) {
        StringBuffer buffer = tokens.getCodeBuffer();

        /*
        I'm doing a sort of State pattern thing here where
        this goes into "discarding" mode when it hits an import or package
        keyword and goes back into "accumulate mode" when it hits a semicolon.
        This could probably be turned into some objects.
        */

        // TODO - allow for JDK 1.5 selection
        JavaParserTokenManager tokenMgr = new TargetJDK1_4().createJavaParserTokenManager(new StringReader(buffer.toString()));
        Token currentToken = tokenMgr.getNextToken();
        boolean inDiscardingState = false;
        while (currentToken.image.length() > 0) {
            if (currentToken.kind == JavaParserConstants.IMPORT || currentToken.kind == JavaParserConstants.PACKAGE) {
                inDiscardingState = true;
                currentToken = tokenMgr.getNextToken();
                continue;
            }

            if (inDiscardingState && currentToken.kind == JavaParserConstants.SEMICOLON) {
                inDiscardingState = false;
            }

            if (inDiscardingState) {
                currentToken = tokenMgr.getNextToken();
                continue;
            }

            if (currentToken.kind != JavaParserConstants.SEMICOLON) {
                String image = currentToken.image;
                if (ignoreLiterals && (currentToken.kind == JavaParserConstants.STRING_LITERAL || currentToken.kind == JavaParserConstants.CHARACTER_LITERAL
                        || currentToken.kind == JavaParserConstants.DECIMAL_LITERAL || currentToken.kind == JavaParserConstants.FLOATING_POINT_LITERAL)) {
                    image = String.valueOf(currentToken.kind);
                }
                if (ignoreIdentifiers && currentToken.kind == JavaParserConstants.IDENTIFIER) {
                    image = String.valueOf(currentToken.kind);
                }
                tokenEntries.add(new TokenEntry(image, tokens.getFileName(), currentToken.beginLine));
            }

            currentToken = tokenMgr.getNextToken();
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
