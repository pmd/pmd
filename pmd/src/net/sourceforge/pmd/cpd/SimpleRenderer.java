package net.sourceforge.pmd.cpd;

import java.util.Iterator;

public class SimpleRenderer implements Renderer {

    public String render(Iterator matches) {
        StringBuffer rpt = new StringBuffer();
        while (matches.hasNext()) {
            Match match = (Match)matches.next();
            rpt.append("=====================================================================" + CPD.EOL);
            rpt.append("Found a " + match.getLineCount() + " line (" + match.getTokenCount() + " tokens) duplication in the following files: " + CPD.EOL);
            for (Iterator occurrences = match.iterator(); occurrences.hasNext();) {
                Mark mark = (Mark)occurrences.next();
                rpt.append("Starting at line " + mark.getBeginLine() + " of " + mark.getTokenSrcID() + CPD.EOL);
            }
            rpt.append(match.getSourceCodeSlice() + CPD.EOL);
        }
        return rpt.toString();
    }
}
