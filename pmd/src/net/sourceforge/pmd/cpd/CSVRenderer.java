/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.cpd;

import net.sourceforge.pmd.PMD;

import java.util.Iterator;

public class CSVRenderer implements Renderer {

    public String render(Iterator matches) {
        StringBuffer rpt = new StringBuffer();
        rpt.append("lines,tokens,occurances" + PMD.EOL);
        while (matches.hasNext()) {
            Match match = (Match) matches.next();
            rpt.append(match.getLineCount() + "," + match.getTokenCount() + "," + match.getMarkCount() + ",");
            for (Iterator marks = match.iterator(); marks.hasNext();) {
                TokenEntry mark = (TokenEntry) marks.next();
                rpt.append(mark.getBeginLine() + "," + mark.getTokenSrcID());
                if (marks.hasNext()) {
                    rpt.append(",");
                }
            }
            rpt.append(PMD.EOL);
        }
        return rpt.toString();
    }
}