/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.StringReader;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BOMInputStream;

import net.sourceforge.pmd.PMD;

public class SourceCode {

    public abstract static class CodeLoader {
        private SoftReference<List<String>> code;

        public List<String> getCode() {
            List<String> c = null;
            if (code != null) {
                c = code.get();
            }
            if (c != null) {
                return c;
            }
            this.code = new SoftReference<>(load());
            return code.get();
        }

        public abstract String getFileName();

        protected abstract Reader getReader() throws Exception;

        protected List<String> load() {
            LineNumberReader lnr = null;
            try {
                lnr = new LineNumberReader(getReader());
                List<String> lines = new ArrayList<>();
                String currentLine;
                while ((currentLine = lnr.readLine()) != null) {
                    lines.add(currentLine);
                }
                return lines;
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Problem while reading " + getFileName() + ":" + e.getMessage());
            } finally {
                IOUtils.closeQuietly(lnr);
            }
        }
    }

    public static class FileCodeLoader extends CodeLoader {
        private File file;
        private String encoding;

        public FileCodeLoader(File file, String encoding) {
            this.file = file;
            this.encoding = encoding;
        }

        @Override
        public Reader getReader() throws Exception {
            BOMInputStream inputStream = 
                new BOMInputStream(new FileInputStream(file),
                        ByteOrderMark.UTF_8, ByteOrderMark.UTF_16BE, ByteOrderMark.UTF_16LE);

            if (inputStream.hasBOM()) {
               encoding = inputStream.getBOMCharsetName();
            }
            return new InputStreamReader(inputStream, encoding);
        }

        public String getEncoding() {
            return encoding;
        }

        @Override
        public String getFileName() {
            return file.getAbsolutePath();
        }
    }

    public static class StringCodeLoader extends CodeLoader {
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

        @Override
        public Reader getReader() {
            return new StringReader(code);
        }

        @Override
        public String getFileName() {
            return name;
        }
    }

    public static class ReaderCodeLoader extends CodeLoader {
        public static final String DEFAULT_NAME = "CODE_LOADED_FROM_READER";

        private Reader code;

        private String name;

        public ReaderCodeLoader(Reader code) {
            this(code, DEFAULT_NAME);
        }

        public ReaderCodeLoader(Reader code, String name) {
            this.code = code;
            this.name = name;
        }

        @Override
        public Reader getReader() {
            return code;
        }

        @Override
        public String getFileName() {
            return name;
        }
    }

    private CodeLoader cl;

    public SourceCode(CodeLoader cl) {
        this.cl = cl;
    }

    public List<String> getCode() {
        return cl.getCode();
    }

    public StringBuilder getCodeBuffer() {
        StringBuilder sb = new StringBuilder();
        List<String> lines = cl.getCode();
        for (String line : lines) {
            sb.append(line).append(PMD.EOL);
        }
        return sb;
    }

    public String getSlice(int startLine, int endLine) {
        StringBuilder sb = new StringBuilder();
        List<String> lines = cl.getCode();
        for (int i = startLine == 0 ? startLine : startLine - 1; i < endLine && i < lines.size(); i++) {
            if (sb.length() != 0) {
                sb.append(PMD.EOL);
            }
            sb.append(lines.get(i));
        }
        return sb.toString();
    }

    public String getFileName() {
        return cl.getFileName();
    }
}
