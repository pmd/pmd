/*
 * User: tom
 * Date: Aug 6, 2002
 * Time: 12:22:03 PM
 */
package net.sourceforge.pmd.cpd;

import net.sourceforge.pmd.ast.JavaCharStream;
import net.sourceforge.pmd.ast.JavaParserTokenManager;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class JavaTokensTokenizer implements Tokenizer {

    private boolean discarding;
    protected String EOL = System.getProperty("line.separator", "\n");

    public void tokenize(TokenList tokens, Reader input) throws IOException {
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
        tokens.setCode(lines);

        // now tokenize it
	    JavaCharStream javaStream = new JavaCharStream(new StringReader(sb.toString()));
        JavaParserTokenManager tokenMgr = new JavaParserTokenManager( javaStream );
        net.sourceforge.pmd.ast.Token currToken = tokenMgr.getNextToken();
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
                tokens.add(new TokenEntry(currToken.image, tokens.size(), tokens.getID(), currToken.beginLine));
            }
            currToken = tokenMgr.getNextToken();
        }
    }
}
