/*
 * User: tom
 * Date: Jul 30, 2002
 * Time: 9:59:48 AM
 */
package net.sourceforge.pmd.cpd;

import net.sourceforge.pmd.ast.*;

import java.io.Reader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

public class CharacterTokenizer implements Tokenizer {

    /**
     * The end of line string for this machine.
     */
    protected String EOL = System.getProperty("line.separator", "\n");

    /**
     * You'll probably want to write a test for this before using it
     */
    public void tokenize(TokenList tokens, Reader input) throws IOException {
        List lines = new ArrayList();
        StringBuffer sb = new StringBuffer();
        LineNumberReader r = new LineNumberReader(input);
        String currentLine = null;
        int position = 0;
        while ((currentLine = r.readLine()) != null) {
            lines.add(currentLine);
            sb.append(currentLine);
            sb.append(EOL);
            for (int i=0; i<currentLine.length(); i++) {
                tokens.add(new TokenEntry(String.valueOf(currentLine.charAt(i)), position, tokens.getID(), lines.size()));
                position++;
            }
        }
        tokens.setCode(lines);
    }
}
