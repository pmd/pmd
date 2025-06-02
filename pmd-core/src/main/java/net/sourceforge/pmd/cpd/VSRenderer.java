/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.IOException;
import java.io.Writer;

import net.sourceforge.pmd.lang.document.FileLocation;

public class VSRenderer implements CPDReportRenderer {

    @Override
    public void render(CPDReport report, Writer writer) throws IOException {
        for (Match match: report.getMatches()) {
            for (Mark mark : match) {
                FileLocation loc = mark.getLocation();
                writer.append(report.getDisplayName(loc.getFileId()))
                      .append('(').append(String.valueOf(loc.getStartLine())).append("):")
                      .append(" Between lines ").append(String.valueOf(loc.getStartLine()))
                      .append(" and ").append(String.valueOf(loc.getEndLine()))
                      .append(System.lineSeparator());
            }
        }
        writer.flush();
    }
}
