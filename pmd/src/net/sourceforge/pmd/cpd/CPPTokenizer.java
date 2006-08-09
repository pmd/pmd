/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import net.sourceforge.pmd.cpd.cppast.CPPParserTokenManager;
import net.sourceforge.pmd.cpd.cppast.Token;
import net.sourceforge.pmd.cpd.cppast.TokenMgrError;
import net.sourceforge.pmd.cpd.cppast.SimpleCharStream;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class CPPTokenizer implements Tokenizer {
    protected String EOL = System.getProperty("line.separator", "\n");

    private static SimpleCharStream charStream;
    public void tokenize(SourceCode sourceCode, Tokens tokenEntries) {
        StringBuffer sb = sourceCode.getCodeBuffer();
        try {
/*
            if (c == null) {
                c = new SimpleCharStream(new StringReader(sb.toString()));
            } else {
                c.ReInit(new StringReader(sb.toString()));
            }
*/
            if (charStream == null) {
                charStream = new SimpleCharStream(new StringReader(sb.toString()));
            } else {
                charStream.ReInit(new StringReader(sb.toString()));
            }
            CPPParserTokenManager.ReInit(charStream);
            CPPParserTokenManager.setFileName(sourceCode.getFileName());
            Token currToken = CPPParserTokenManager.getNextToken();
            while (currToken.image.length() > 0) {
                tokenEntries.add(new TokenEntry(currToken.image, sourceCode.getFileName(), currToken.beginLine));
                currToken = CPPParserTokenManager.getNextToken();
            }
            tokenEntries.add(TokenEntry.getEOF());
            System.out.println("Added " + sourceCode.getFileName());
        } catch (TokenMgrError err) {
            err.printStackTrace();
            System.out.println("Skipping " + sourceCode.getFileName() + " due to parse error");
            List emptyCode = new ArrayList();
            emptyCode.add("");
            tokenEntries.add(TokenEntry.getEOF());
        }
    }
}
