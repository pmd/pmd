/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import org.apache.commons.lang3.StringEscapeUtils;

import net.sourceforge.pmd.lang.document.FileLocation;

/**
 * Renders a report to CSV. The CSV format renders each match (duplication)
 * as a single line with the following columns:
 * <ul>
 * <li>lines (optional): The number of lines the first mark of a match spans.
 * Only output if the {@code lineCountPerFile} is disabled (see ctor params).</li>
 * <li>tokens: The number of duplicated tokens in a match (size of the match).</li>
 * <li>occurrences: The number of duplicates in a match (number of times the tokens were found in distinct places).</li>
 * </ul>
 *
 * <p>Trailing each line are pairs (or triples, if {@code lineCountPerFile} is enabled)
 * of fields describing each file where the duplication was found in the format
 * {@code (start line, line count (optional), file path)}. These repeat at least twice.
 *
 * <h2>Examples</h2>
 * <p>
 * Example without {@code lineCountPerFile}:
 * <pre>{@code
 * lines,tokens,occurrences
 * 10,75,2,48,/var/file1,73,/var/file2
 * }</pre>
 * This describes one match with the following characteristics:
 * <ul>
 * <li>The first duplicate instance is 10 lines long;
 * <li>75 duplicated tokens;
 * <li>2 duplicate instances;
 * <li>The first duplicate instance is in file {@code /var/file1} and starts at line 48;</li>
 * <li>The second duplicate instance is in file {@code /var/file2} and starts at line 73.</li>
 * </ul>
 * <p>
 * Example with {@code lineCountPerFile}:
 * <pre>{@code
 * tokens,occurrences
 * 75,2,48,10,/var/file1,73,12,/var/file2
 * }</pre>
 * This describes one match with the following characteristics:
 * <ul>
 * <li>75 duplicated tokens
 * <li>2 duplicate instances
 * <li>The first duplicate instance is in file {@code /var/file1}, starts at line 48, and is 10 lines long;</li>
 * <li>The second duplicate instance is in file {@code /var/file2}, starts at line 73, and is 12 lines long.</li>
 * </ul>
 */
public class CSVRenderer implements CPDReportRenderer {

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
    public void render(CPDReport report, Writer writer) throws IOException {
        if (!lineCountPerFile) {
            writer.append("lines").append(separator);
        }
        writer.append("tokens").append(separator).append("occurrences").append(System.lineSeparator());

        for (Match match : report.getMatches()) {
            if (!lineCountPerFile) {
                writer.append(String.valueOf(match.getLineCount())).append(separator);
            }
            writer.append(String.valueOf(match.getTokenCount())).append(separator)
                  .append(String.valueOf(match.getMarkCount())).append(separator);
            for (Iterator<Mark> marks = match.iterator(); marks.hasNext();) {
                Mark mark = marks.next();
                FileLocation loc = mark.getLocation();

                writer.append(String.valueOf(loc.getStartLine())).append(separator);
                if (lineCountPerFile) {
                    writer.append(String.valueOf(loc.getLineCount())).append(separator);
                }
                writer.append(StringEscapeUtils.escapeCsv(report.getDisplayName(loc.getFileId())));
                if (marks.hasNext()) {
                    writer.append(separator);
                }
            }
            writer.append(System.lineSeparator());
        }
        writer.flush();
    }
}
