/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Iterator;

import net.sourceforge.pmd.lang.document.Chars;
import net.sourceforge.pmd.lang.document.FileLocation;
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

    static void printlnReport(PrintStream out, CPDReport report) throws IOException {
        SimpleRenderer renderer = new SimpleRenderer();
        PrintWriter pw = new PrintWriter(out);
        renderer.render(report, pw);
    }

    @Override
    public void render(CPDReport report, Writer writer0) throws IOException {
        PrintWriter writer = new PrintWriter(writer0);
        Iterator<Match> matches = report.getMatches().iterator();
        if (matches.hasNext()) {
            renderOn(report, writer, matches.next());
        }

        while (matches.hasNext()) {
            Match match = matches.next();
            writer.println(separator);
            renderOn(report, writer, match);
        }
        writer.flush();
    }

    private void renderOn(CPDReport report, PrintWriter writer, Match match) throws IOException {

        String tokenCount = match.getMinTokenCount() == match.getMaxTokenCount() ? String.valueOf(match.getMinTokenCount())
                                                                                 : match.getMinTokenCount() + ".." + match.getMaxTokenCount();

        writer.append("Found a ").append(String.valueOf(match.getLineCount())).append(" line (").append(tokenCount)
              .append(" tokens) duplication in the following files: ").println();

        for (Mark mark : match) {
            FileLocation loc = mark.getLocation();
            writer.append("Starting at line ")
                  .append(String.valueOf(loc.getStartLine()))
                  .append(" of ").append(report.getDisplayName(loc.getFileId()))
                  .println();
        }

        writer.println(); // add a line to separate the source from the desc above

        FileLocation firstLoc = match.getFirstMark().getLocation();
        writeColumnMarker(true, firstLoc.getStartColumn(), writer);
        Chars source = report.getSourceCodeSlice(match.getFirstMark());

        if (trimLeadingWhitespace) {
            for (Chars line : StringUtil.linesWithTrimIndent(source)) {
                line.writeFully(writer);
                writer.println();
            }
        } else {
            source.writeFully(writer);
            if (!source.endsWith("\n")) {
                writer.println();
            }
        }
        writeColumnMarker(false, firstLoc.getEndColumn(), writer);
    }

    private static void writeColumnMarker(boolean start, int col, PrintWriter writer) {
        if (!start) {
            // end col is exclusive
            col--;
        }
        char marker = start ? 'v' : '^';
        String hinweis = start ? " starting from here (col " + col + ")"
                               : " ending here (col " + col + ")";

        StringBuilder sb = new StringBuilder(col + hinweis.length());
        for (int i = 0; i < col - 1; i++) {
            sb.append('-');
        }
        sb.append(marker).append(hinweis);
        writer.append(sb).println();
    }
}
