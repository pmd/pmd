/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.cpd.renderer.CPDReportRenderer;
import net.sourceforge.pmd.lang.document.Chars;
import net.sourceforge.pmd.util.StringUtil;

public class SimpleRenderer implements CPDReportRenderer {

    private String separator;
    private boolean trimLeadingWhitespace;

    public static final String DEFAULT_SEPARATOR = "=====================================================================";

    public SimpleRenderer() {
        this(false);
    }

    public SimpleRenderer(boolean trimLeadingWhitespace) {
        this(DEFAULT_SEPARATOR);
        this.trimLeadingWhitespace = trimLeadingWhitespace;
    }

    public SimpleRenderer(String theSeparator) {
        separator = theSeparator;
    }

    @Override
    public void render(CPDReport report, Writer writer) throws IOException {
        Iterator<Match> matches = report.getMatches().iterator();
        if (matches.hasNext()) {
            renderOn(writer, matches.next());
        }

        while (matches.hasNext()) {
            Match match = matches.next();
            writer.append(separator).append(PMD.EOL);
            renderOn(writer, match);
        }
        writer.flush();
    }

    private void renderOn(Writer writer, Match match) throws IOException {

        writer.append("Found a ").append(String.valueOf(match.getLineCount())).append(" line (").append(String.valueOf(match.getTokenCount()))
                .append(" tokens) duplication in the following files: ").append(PMD.EOL);

        for (Mark mark : match) {
            writer.append("Starting at line ").append(String.valueOf(mark.getBeginLine())).append(" of ").append(mark.getFilename())
                  .append(PMD.EOL);
        }

        writer.append(PMD.EOL); // add a line to separate the source from the desc above

        String source = match.getSourceCodeSlice();

        if (trimLeadingWhitespace) {
            for (Chars line : StringUtil.linesWithTrimIndent(source)) {
                line.writeFully(writer);
                writer.append(PMD.EOL);
            }
            return;
        }

        writer.append(source).append(PMD.EOL);
    }

}
