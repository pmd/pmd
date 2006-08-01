/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import net.sourceforge.pmd.PMD;

import java.util.Iterator;

public class SimpleRenderer implements Renderer {

	private String separator;
	
	public static final String defaultSeparator = "=====================================================================";
	
	public SimpleRenderer() {
		this(defaultSeparator);
	}
	
	public SimpleRenderer(String theSeparator) {
		separator = theSeparator;
	}
	
	private void renderOn(StringBuffer rpt, Match match) {
		
          rpt.append("Found a ").append(match.getLineCount()).append(" line (").append(match.getTokenCount()).append(" tokens) duplication in the following files: ").append(PMD.EOL);
          
          TokenEntry mark;
          for (Iterator occurrences = match.iterator(); occurrences.hasNext();) {
              mark = (TokenEntry) occurrences.next();
              rpt.append("Starting at line ").append(mark.getBeginLine()).append(" of ").append(mark.getTokenSrcID()).append(PMD.EOL);
          }
          
          rpt.append(match.getSourceCodeSlice()).append(PMD.EOL);
	}
	
	
    public String render(Iterator matches) {
    	
        StringBuffer rpt = new StringBuffer(300);
        
        if (matches.hasNext()) {
        	renderOn(rpt, (Match)matches.next());
        }
        
        Match match;
        while (matches.hasNext()) {
            match = (Match) matches.next();
            rpt.append(separator).append(PMD.EOL);
            renderOn(rpt, match);
          
        }
        return rpt.toString();
    }
}
