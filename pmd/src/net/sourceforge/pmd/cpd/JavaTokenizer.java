/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.cpd;

import net.sourceforge.pmd.TargetJDK1_4;
import net.sourceforge.pmd.ast.JavaParserTokenManager;
import net.sourceforge.pmd.ast.Token;

import java.io.StringReader;

public class JavaTokenizer implements Tokenizer {

    public void tokenize(SourceCode tokens, Tokens tokenEntries) {
        StringBuffer sb = tokens.getCodeBuffer();

        /*
        I'm doing a sort of State pattern thing here where
        this goes into "discarding" mode when it hits an import or package
        keyword and goes back into "accumulate mode when it hits a semicolon.
        This could probably be turned into some objects.
        */
        JavaParserTokenManager tokenMgr = new TargetJDK1_4().createJavaParserTokenManager(new StringReader(sb.toString()));
        Token currToken = tokenMgr.getNextToken();
        boolean discarding = false;
        while (currToken.image != "") {
            if (currToken.image.equals("import") || currToken.image.equals("package")) {
                discarding = true;
                currToken = tokenMgr.getNextToken();
                continue;
            }

            if (discarding && currToken.image.equals(";")) {
                discarding = false;
            }

            if (discarding) {
                currToken = tokenMgr.getNextToken();
                continue;
            }

            if (!currToken.image.equals(";")) {
                tokenEntries.add(new TokenEntry(currToken.image, tokens.getFileName(), currToken.beginLine));
            }

            currToken = tokenMgr.getNextToken();
        }
        tokenEntries.add(TokenEntry.getEOF());
    }
}
