package net.sourceforge.pmd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

public class ExcludeLines {

    private Set lines = new HashSet();
    private String copy;

    public ExcludeLines(Reader r) throws IOException {
        BufferedReader br = new BufferedReader(r);
        StringBuffer copyBuffer = new StringBuffer();
        String tmp;
        int counter = 1;
        while ((tmp = br.readLine()) != null) {
            copyBuffer.append(tmp + PMD.EOL);
            if (tmp.indexOf("NOPMD") != -1) {
                lines.add(new Integer(counter));
            }
            counter++;
        }
        copy = copyBuffer.toString();
    }

    public Reader getCopyReader() {
        return new StringReader(copy);
    }

    public Set getLinesToExclude() {
        return lines;
    }
}
