package net.sourceforge.pmd.cpd;

import net.sourceforge.pmd.PMD;

import java.util.Iterator;

public class SimpleRenderer implements Renderer {

    public String render(Iterator matches) {
        StringBuffer rpt = new StringBuffer();
        while (matches.hasNext()) {
            Match match = (Match)matches.next();
            rpt.append("=====================================================================" + PMD.EOL);
            rpt.append("Found a " + match.getLineCount() + " line (" + match.getTokenCount() + " tokens) duplication in the following files: " + PMD.EOL);
            for (Iterator occurrences = match.iterator(); occurrences.hasNext();) {
                Mark mark = (Mark)occurrences.next();
                rpt.append("Starting at line " + mark.getBeginLine() + " of " + mark.getTokenSrcID() + PMD.EOL);
            }
            rpt.append(match.getSourceCodeSlice() + PMD.EOL);
        }
        return rpt.toString();
    }
}
