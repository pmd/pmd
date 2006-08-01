/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import net.sourceforge.pmd.PMD;

import java.util.Iterator;


public class CSVRenderer implements Renderer {

	private char separator;
	
	public static final char defaultSeparator = ',';
	
	public CSVRenderer() {
		this(defaultSeparator);
	}
	
	public CSVRenderer(char separatorChar) {
		separator = separatorChar;
	}
	
    public String render(Iterator matches) {
        StringBuffer rpt = new StringBuffer(1000);
        rpt.append("lines").append(separator);
        rpt.append("tokens").append(separator);
        rpt.append("occurrences");
        rpt.append(PMD.EOL);
        
        Match match;
        TokenEntry mark;
        
        while (matches.hasNext()) {
            match = (Match) matches.next();
            rpt.append(match.getLineCount()).append(separator);
            rpt.append(match.getTokenCount()).append(separator);
            rpt.append(match.getMarkCount()).append(separator);
            for (Iterator marks = match.iterator(); marks.hasNext();) {
                mark = (TokenEntry) marks.next();
                rpt.append(mark.getBeginLine()).append(separator);
                rpt.append(mark.getTokenSrcID());
                if (marks.hasNext()) {
                    rpt.append(separator);
                }
            }
            rpt.append(PMD.EOL);
        }
        return rpt.toString();
    }
}