package net.sourceforge.pmd.cpd;

import net.sourceforge.pmd.PMD;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class PHPTokenizer implements Tokenizer {

    public void tokenize(SourceCode tokens, Tokens tokenEntries, Reader input) throws IOException {
        List lines = new ArrayList();
        StringBuffer sb = new StringBuffer();
        LineNumberReader r = new LineNumberReader(input);
        String currentLine;
        int lineCount = 0;
        int count = 0;
        while ((currentLine = r.readLine()) != null) {
            lines.add(currentLine);
            sb.append(currentLine);
            sb.append(PMD.EOL);
            for (int i=0;i<currentLine.length(); i++) {
                char tok =currentLine.charAt(i);
                if (    !Character.isWhitespace(tok) &&
                        tok != '{' &&
                        tok != '}' &&
                        tok != ';') {
                    count++;
                    tokenEntries.add(new TokenEntry(String.valueOf(tok), count, tokens.getFileName(), lineCount));
                }
            }
            lineCount++;
        }
        tokens.setCode(lines);
        tokenEntries.add(TokenEntry.EOF);
    }
}
