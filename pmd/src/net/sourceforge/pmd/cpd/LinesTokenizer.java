package net.sourceforge.pmd.cpd;

import java.io.IOException;
import java.io.Reader;

public class LinesTokenizer implements Tokenizer {

    public void tokenize(TokenList tokens, Reader input) throws IOException {
/*
        String eol = System.getProperty("line.separator", "\n");
        List lines = new ArrayList();
        StringBuffer sb = new StringBuffer();
        LineNumberReader r = new LineNumberReader(input);
        String currentLine;
        while ((currentLine = r.readLine()) != null) {
            lines.add(currentLine);
            sb.append(currentLine);
            sb.append(eol);
            tokens.add(new TokenEntry(currentLine, tokens.size(), tokens.getFileName(), lines.size()));
        }
        tokens.setCode(lines);
*/
    }
}
