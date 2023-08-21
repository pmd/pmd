/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Iterator;

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
    public void render(CPDReport report, Writer writer0) throws IOException {
        PrintWriter writer = new PrintWriter(writer0);
        Iterator<Match> matches = report.getMatches().iterator();
        if (matches.hasNext()) {
            renderOn(writer, matches.next());
        }

        while (matches.hasNext()) {
            Match match = matches.next();
            writer.println(separator);
            renderOn(writer, match);
        }
        writer.flush();
    }

    private void renderOn(PrintWriter writer, Match match) throws IOException {

        writer.append("Found a ").append(String.valueOf(match.getLineCount())).append(" line (").append(String.valueOf(match.getTokenCount()))
                .append(" tokens) duplication in the following files: ").println();

        for (Mark mark : match) {
            writer.append("Starting at line ").append(String.valueOf(mark.getBeginLine())).append(" of ").append(mark.getFilename())
                  .println();
        }

        writer.println(); // add a line to separate the source from the desc above

        String source = match.getSourceCodeSlice();

        if (trimLeadingWhitespace) {
            for (Chars line : StringUtil.linesWithTrimIndent(source)) {
                line.writeFully(writer);
                writer.println();
            }
            return;
        }

        writer.println(source);
    }

}
