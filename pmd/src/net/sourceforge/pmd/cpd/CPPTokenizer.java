package net.sourceforge.pmd.cpd;

import net.sourceforge.pmd.cpd.cppast.CPPParser;
import net.sourceforge.pmd.cpd.cppast.CPPParserTokenManager;
import net.sourceforge.pmd.cpd.cppast.Token;
import net.sourceforge.pmd.cpd.cppast.TokenMgrError;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class CPPTokenizer implements Tokenizer {
    protected String EOL = System.getProperty("line.separator", "\n");

    private static boolean initted;

    public void tokenize(SourceCode sourceCode, Tokens tokenEntries, Reader input) throws IOException {
        try {
            // first get a snapshot of the code
            List lines = new ArrayList();
            StringBuffer sb = new StringBuffer();
            LineNumberReader r = new LineNumberReader(input);
            String currentLine;
            while ((currentLine = r.readLine()) != null) {
                lines.add(currentLine);
                sb.append(currentLine);
                sb.append(EOL);
            }
            sourceCode.setCode(lines);

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
                tokenEntries.add(new TokenEntry(currToken.image, count, sourceCode.getFileName(), currToken.beginLine));
                currToken = CPPParserTokenManager.getNextToken();
            }
            tokenEntries.add(TokenEntry.EOF);
            System.out.println("Added " + sourceCode.getFileName());
        } catch (TokenMgrError err) {
            System.out.println("Skipping " + sourceCode.getFileName() + " due to parse error");
            List emptyCode = new ArrayList();
            emptyCode.add("");
            sourceCode.setCode(emptyCode);
            tokenEntries.add(TokenEntry.EOF);
        }
    }
}
