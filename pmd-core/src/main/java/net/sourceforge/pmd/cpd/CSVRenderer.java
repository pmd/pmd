/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import net.sourceforge.pmd.PMD;

import java.util.Iterator;


public class CSVRenderer implements Renderer {

	private char separator;
	
	public static final char DEFAULT_SEPARATOR = ',';
	
	public CSVRenderer() {
		this(DEFAULT_SEPARATOR);
	}
	
	public CSVRenderer(char separatorChar) {
		separator = separatorChar;
	}
	
    public String render(Iterator<Match> matches) {
        StringBuilder rpt = new StringBuilder(1000);
        rpt.append("lines").append(separator);
        rpt.append("tokens").append(separator);
        rpt.append("occurrences");
        rpt.append(PMD.EOL);
        
        while (matches.hasNext()) {
            Match match = matches.next();
            rpt.append(match.getLineCount()).append(separator);
            rpt.append(match.getTokenCount()).append(separator);
            rpt.append(match.getMarkCount()).append(separator);
            for (Iterator<TokenEntry> marks = match.iterator(); marks.hasNext();) {
                TokenEntry mark = marks.next();
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