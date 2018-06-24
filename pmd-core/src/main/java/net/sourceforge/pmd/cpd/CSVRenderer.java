/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;

import org.apache.commons.lang3.StringEscapeUtils;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.cpd.renderer.CPDRenderer;

public class CSVRenderer implements Renderer, CPDRenderer {

    private final char separator;
    private final boolean lineCountPerFile;

    public static final char DEFAULT_SEPARATOR = ',';
    public static final boolean DEFAULT_LINECOUNTPERFILE = false;

    public CSVRenderer() {
        this(DEFAULT_SEPARATOR, DEFAULT_LINECOUNTPERFILE);
    }

    public CSVRenderer(boolean lineCountPerFile) {
        this(DEFAULT_SEPARATOR, lineCountPerFile);
    }

    public CSVRenderer(char separatorChar) {
        this(separatorChar, DEFAULT_LINECOUNTPERFILE);
    }

    public CSVRenderer(char separatorChar, boolean lineCountPerFile) {
        this.separator = separatorChar;
        this.lineCountPerFile = lineCountPerFile;
    }

    @Override
    public String render(Iterator<Match> matches) {
        StringWriter writer = new StringWriter(1000);
        try {
            render(matches, writer);
        } catch (IOException ignored) {
            // Not really possible with a StringWriter
        }
        return writer.toString();
    }

    @Override
    public void render(Iterator<Match> matches, Writer writer) throws IOException {
        if (!lineCountPerFile) {
            writer.append("lines").append(separator);
        }
        writer.append("tokens").append(separator).append("occurrences").append(PMD.EOL);

        while (matches.hasNext()) {
            Match match = matches.next();

            if (!lineCountPerFile) {
                writer.append(String.valueOf(match.getLineCount())).append(separator);
            }
            writer.append(String.valueOf(match.getTokenCount())).append(separator)
                .append(String.valueOf(match.getMarkCount())).append(separator);
            for (Iterator<Mark> marks = match.iterator(); marks.hasNext();) {
                Mark mark = marks.next();

                writer.append(String.valueOf(mark.getBeginLine())).append(separator);
                if (lineCountPerFile) {
                    writer.append(String.valueOf(mark.getLineCount())).append(separator);
                }
                writer.append(StringEscapeUtils.escapeCsv(mark.getFilename()));
                if (marks.hasNext()) {
                    writer.append(separator);
                }
            }
            writer.append(PMD.EOL);
        }
        writer.flush();
    }
}
