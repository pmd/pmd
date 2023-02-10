/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.IOException;
import java.io.Reader;
import java.lang.ref.SoftReference;
import java.util.List;

import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.document.TextFile;

public class SourceCode {

    private SoftReference<TextDocument> softRef;
    private final TextFile textFile;

    public SourceCode(TextFile textFile) {
        this.textFile = textFile;
    }

    public TextDocument load() throws IOException {
        if (softRef != null && softRef.get() != null) {
            return softRef.get();
        }
        TextDocument doc = TextDocument.create(textFile);
        softRef = new SoftReference<>(doc);
        return doc;
    }


    public List<String> getCode() {
        return cl.getCode();
    }

    /** Newlines are normalized to \n. */
    public StringBuilder getCodeBuffer() {
        StringBuilder sb = new StringBuilder();
        List<String> lines = cl.getCode();
        for (String line : lines) {
            sb.append(line).append('\n');
        }
        return sb;
    }

    /**
     * Loads a range of lines. Newlines are normalized to \n
     *
     * @param startLine Start line (inclusive, 1-based)
     * @param endLine   End line (inclusive, 1-based)
     */
    public String getSlice(int startLine, int endLine) {
        List<String> lines = cl.getCodeSlice(startLine, endLine);

        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            if (sb.length() != 0) {
                sb.append('\n');
            }
            sb.append(line);
        }
        return sb.toString();
    }

    public String getFileName() {
        return cl.getFileName();
    }

    public Reader getReader() throws Exception {
        return cl.getReader();
    }
}
