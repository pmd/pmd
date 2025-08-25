/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import net.sourceforge.pmd.lang.document.Chars;
import net.sourceforge.pmd.lang.document.FileLocation;

public class MarkdownRenderer implements CPDReportRenderer {

    //separator must be surrounded by empty lines to be rendered properly
    private static final String SEPARATOR = "\n---\n\n";
    private static final String FOUND_DUPLICATION_TEMPLATE = "Found a %d line (%d tokens) duplication in the following files:\n";
    private static final String STARTING_AT_LINE_TEMPLATE = "Starting at line %d of %s\n";
    private static final String CODE_BLOCK_TEMPLATE = "\n```\n%s```\n";

    @Override
    public void render(CPDReport report, Writer writer) throws IOException {
        Iterator<Match> matches = report.getMatches().iterator();
        if (matches.hasNext()) {
            renderDuplication(report, writer, matches.next());
        }

        while (matches.hasNext()) {
            Match match = matches.next();
            writer.write(SEPARATOR);
            renderDuplication(report, writer, match);
        }

        writer.flush();
    }

    private void renderDuplication(CPDReport report, Writer writer, Match match) throws IOException {
        writer.append(String.format(FOUND_DUPLICATION_TEMPLATE, match.getLineCount(), match.getTokenCount()));

        for (Mark mark : match) {
            FileLocation loc = mark.getLocation();
            writer.append(String.format(STARTING_AT_LINE_TEMPLATE, loc.getStartLine(), report.getDisplayName(loc.getFileId())));
        }

        Chars source = report.getSourceCodeSlice(match.getFirstMark());
        writer.append(String.format(CODE_BLOCK_TEMPLATE, source));
    }
}
