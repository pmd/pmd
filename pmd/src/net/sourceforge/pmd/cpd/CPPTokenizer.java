/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.cpd;

import net.sourceforge.pmd.cpd.cppast.CPPParser;
import net.sourceforge.pmd.cpd.cppast.CPPParserTokenManager;
import net.sourceforge.pmd.cpd.cppast.Token;
import net.sourceforge.pmd.cpd.cppast.TokenMgrError;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class CPPTokenizer implements Tokenizer {
    protected String EOL = System.getProperty("line.separator", "\n");

    private static boolean initted;

    public void tokenize(SourceCode sourceCode, Tokens tokenEntries) {
        StringBuffer sb = sourceCode.getCodeBuffer();
        try {
            // now tokenize it
            if (!initted) {
                new CPPParser(new StringReader(sb.toString()));
                initted = true;
            }
            CPPParser.ReInit(new StringReader(sb.toString()));
            Token currToken = CPPParserTokenManager.getNextToken();
            int count = 0;
            while (currToken.image != "") {
                count++;
                tokenEntries.add(new TokenEntry(currToken.image, sourceCode.getFileName(), currToken.beginLine));
                currToken = CPPParserTokenManager.getNextToken();
            }
            tokenEntries.add(TokenEntry.getEOF());
            System.out.println("Added " + sourceCode.getFileName());
        } catch (TokenMgrError err) {
            System.out.println("Skipping " + sourceCode.getFileName() + " due to parse error");
            List emptyCode = new ArrayList();
            emptyCode.add("");
            tokenEntries.add(TokenEntry.getEOF());
        }
    }
}
