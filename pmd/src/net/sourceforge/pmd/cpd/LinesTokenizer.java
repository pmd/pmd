/*
 * User: tom
 * Date: Sep 23, 2002
 * Time: 3:14:38 PM
 */
package net.sourceforge.pmd.cpd;

import java.io.Reader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.List;
import java.util.ArrayList;

public class LinesTokenizer implements Tokenizer {

    public void tokenize(TokenList tokens, Reader input) throws IOException {
        String eol = System.getProperty("line.separator", "\n");
        List lines = new ArrayList();
        StringBuffer sb = new StringBuffer();
        LineNumberReader r = new LineNumberReader(input);
        String currentLine = null;
        while ((currentLine = r.readLine()) != null) {
            lines.add(currentLine);
            sb.append(currentLine);
            sb.append(eol);
            tokens.add(new TokenEntry(currentLine, tokens.size(), tokens.getID(), lines.size()));
        }
        tokens.setCode(lines);
    }
}
