package net.sourceforge.pmd.cpd;

import java.io.IOException;
import java.io.Reader;

public class CharacterTokenizer /*implements Tokenizer*/ {

    /**
     * The end of line string for this machine.
     */
    protected String EOL = System.getProperty("line.separator", "\n");

    /**
     * You'll probably want to write a test for this before using it
     */
    public void tokenize(SourceCode tokens, Reader input) throws IOException {
/*
        List lines = new ArrayList();
        StringBuffer sb = new StringBuffer();
        LineNumberReader r = new LineNumberReader(input);
        String currentLine;
        int position = 0;
        while ((currentLine = r.readLine()) != null) {
            lines.add(currentLine);
            sb.append(currentLine);
            sb.append(EOL);
            for (int i = 0; i < currentLine.length(); i++) {
                tokens.add(new TokenEntry(String.valueOf(currentLine.charAt(i)), position, tokens.getFileName(), lines.size()));
                position++;
            }
        }
        tokens.setCode(lines);
*/
    }
}
