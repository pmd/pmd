/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import net.sourceforge.pmd.PMD;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.File;
import java.io.StringReader;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

public class SourceCode {

    public interface CodeLoader {
        SoftReference getCode();
        String getFileName();
    }

    public static class FileCodeLoader implements CodeLoader {
        private File file;
        public FileCodeLoader(File file) {
            this.file = file;
        }
        public SoftReference getCode() {
            try {
                List lines = new ArrayList();
                LineNumberReader lnr = new LineNumberReader(new FileReader(file));
                String currentLine;
                while ((currentLine = lnr.readLine()) != null) {
                    lines.add(currentLine);
                }
                lnr.close();
                return new SoftReference(lines);
            } catch (Exception e) {
                throw new RuntimeException("Problem while reading " + file.getAbsolutePath() + ":" + e.getMessage());
            }
        }
        public String getFileName() {
            return this.file.getAbsolutePath();
        }
    }
    public static class StringCodeLoader implements CodeLoader {
        public static final String DEFAULT_NAME = "CODE_LOADED_FROM_STRING";
        private String code;
        private String name;
        public StringCodeLoader(String code) {
            this(code, DEFAULT_NAME);
        }
        public StringCodeLoader(String code, String name) {
            this.code = code;
            this.name = name;
        }
        public SoftReference getCode() {
            try {
                List lines = new ArrayList();
                LineNumberReader lnr = new LineNumberReader(new StringReader(code));
                String currentLine;
                while ((currentLine = lnr.readLine()) != null) {
                    lines.add(currentLine);
                }
                lnr.close();
                return new SoftReference(lines);
            } catch (Exception e) {
                throw new RuntimeException("Problem while reading code from String: " + e.getMessage());
            }
        }
        public String getFileName() {
            return name;
        }
    }


    private SoftReference code;
    private CodeLoader cl;

    public SourceCode(CodeLoader cl) {
        this.cl = cl;
    }

    public List getCode() {
        List c = null;
        if (code != null) {
            c = (List) code.get();
        }
        if (c != null) {
            return c;
        }
        this.code = cl.getCode();
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
        return cl.getFileName();
    }

    public boolean equals(Object other) {
        SourceCode o = (SourceCode) other;
        return o.cl.getFileName().equals(cl.getFileName());
    }

    public int hashCode() {
        return cl.getFileName().hashCode();
    }
}
