/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.cpd;

import net.sourceforge.pmd.PMD;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

public class SourceCode {

    private String fileName;
    private SoftReference code;

    public SourceCode(String fileName) {
        this.fileName = fileName;
    }

    public List getCode() {
        List c = null;
        if (code != null) {
            c = (List) code.get();
        }
        if (c != null) {
            return c;
        }
        try {
            readSource(new FileReader(this.fileName));
        } catch (IOException e) {
            throw new RuntimeException("Couldn't read " + fileName);
        }
        return (List) code.get();
    }

    public void setCode(List l) {
        this.code = new SoftReference(l);
    }

    public StringBuffer getCodeBuffer() {
        StringBuffer sb = new StringBuffer();
        List lines = getCode();
        for (int i = 0; i < lines.size(); i++) {
            sb.append((String) lines.get(i));
            sb.append(PMD.EOL);
        }
        return sb;
    }

    public void readSource(Reader input) throws IOException {
        List lines = new ArrayList();
        LineNumberReader r = new LineNumberReader(input);
        String currentLine;
        while ((currentLine = r.readLine()) != null) {
            lines.add(currentLine);
        }
        input.close();
        this.code = new SoftReference(lines);
    }

    public String getSlice(int startLine, int endLine) {
        StringBuffer sb = new StringBuffer();
        List lines = getCode();
        for (int i = startLine - 1; i < endLine && i < lines.size(); i++) {
            if (sb.length() != 0) {
                sb.append(PMD.EOL);
            }
            sb.append((String) lines.get(i));
        }
        return sb.toString();
    }

    public String getFileName() {
        return fileName;
    }

    public boolean equals(Object other) {
        SourceCode o = (SourceCode) other;
        return o.fileName.equals(fileName);
    }

    public int hashCode() {
        return fileName.hashCode();
    }
}
