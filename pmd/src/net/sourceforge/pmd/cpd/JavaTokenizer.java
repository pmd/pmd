package net.sourceforge.pmd.cpd;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.ast.JavaCharStream;
import net.sourceforge.pmd.ast.JavaParserTokenManager;
import net.sourceforge.pmd.ast.Token;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class JavaTokenizer implements Tokenizer {

    public void tokenize(SourceCode tokens, Tokens tokenEntries, Reader input) throws IOException {
        // first get a snapshot of the code
        List lines = new ArrayList();
        StringBuffer sb = new StringBuffer();
        LineNumberReader r = new LineNumberReader(input);
        String currentLine;
        while ((currentLine = r.readLine()) != null) {
            lines.add(currentLine);
            sb.append(currentLine);
            sb.append(PMD.EOL);
        }
        tokens.setCode(lines);

        // now tokenize it
        /*
        I'm doing a sort of State pattern thing here where
        this goes into "discarding" mode when it hits an import or package
        keyword and goes back into "accumulate mode when it hits a semicolon.
        This could probably be turned into some objects.
        */
        JavaCharStream javaStream = new JavaCharStream(new StringReader(sb.toString()));
        JavaParserTokenManager tokenMgr = new JavaParserTokenManager(javaStream);
        Token currToken = tokenMgr.getNextToken();
        boolean discarding = false;
        int count = 0;
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
                count++;
                tokenEntries.add(new TokenEntry(currToken.image, count, tokens.getFileName(), currToken.beginLine));
            }

            currToken = tokenMgr.getNextToken();
        }
        tokenEntries.add(TokenEntry.EOF);
    }
}
