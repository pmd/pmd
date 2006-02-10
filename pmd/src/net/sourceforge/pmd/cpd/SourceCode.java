/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import net.sourceforge.pmd.PMD;

import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.StringReader;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

public class SourceCode {

    public static abstract class CodeLoader {
        private SoftReference code;

        public List getCode() {
            List c = null;
            if (code != null) {
                c = (List) code.get();
            }
            if (c != null) {
                return c;
            }
            this.code = new SoftReference(load());
            return (List) code.get();
        }

        public abstract String getFileName();

        protected abstract Reader getReader() throws Exception;

        protected List load() {
            LineNumberReader lnr = null;
            try {
                lnr = new LineNumberReader(getReader());
                List lines = new ArrayList();
                String currentLine;
                while ((currentLine = lnr.readLine()) != null) {
                    lines.add(currentLine);
                }
                return lines;
            } catch (Exception e) {
                throw new RuntimeException("Problem while reading " + getFileName() + ":" + e.getMessage());
            } finally {
                try {
                    if (lnr != null)
                        lnr.close();
                } catch (Exception e) {
                    throw new RuntimeException("Problem while reading " + getFileName() + ":" + e.getMessage());
                }
            }
        }
    }

    public static class FileCodeLoader extends CodeLoader {
        private File file;

        public FileCodeLoader(File file) {
            this.file = file;
        }

        public Reader getReader() throws Exception {
            return new FileReader(file);
        }

        public String getFileName() {
            return this.file.getAbsolutePath();
        }
    }

    public static class StringCodeLoader extends CodeLoader {
        public static final String DEFAULT_NAME = "CODE_LOADED_FROM_STRING";

        private String source_code;

        private String name;

        public StringCodeLoader(String code) {
            this(code, DEFAULT_NAME);
        }

        public StringCodeLoader(String code, String name) {
            this.source_code = code;
            this.name = name;
        }

        public Reader getReader() {
            return new StringReader(source_code);
        }

        public String getFileName() {
            return name;
        }
    }

    private CodeLoader cl;

    public SourceCode(CodeLoader cl) {
        this.cl = cl;
    }

    public List getCode() {
        return cl.getCode();
    }

    public StringBuffer getCodeBuffer() {
        StringBuffer sb = new StringBuffer();
        List lines = cl.getCode();
        for (int i = 0; i < lines.size(); i++) {
            sb.append((String) lines.get(i));
            sb.append(PMD.EOL);
        }
        return sb;
    }

    public String getSlice(int startLine, int endLine) {
        StringBuffer sb = new StringBuffer();
        List lines = cl.getCode();
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
}