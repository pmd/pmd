/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.IOException;
import java.io.Writer;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.cpd.renderer.CPDReportRenderer;

public class VSRenderer implements CPDReportRenderer {

    @Override
    public void render(CPDReport report, Writer writer) throws IOException {
        for (Match match: report.getMatches()) {
            for (Mark mark : match) {
                writer.append(mark.getFilename())
                      .append('(').append(String.valueOf(mark.getBeginLine())).append("):")
                      .append(" Between lines ").append(String.valueOf(mark.getBeginLine()))
                      .append(" and ").append(String.valueOf(mark.getBeginLine() + match.getLineCount()))
                      .append(PMD.EOL);
            }
        }
        writer.flush();
    }
}
