/*
 * User: tom
 * Date: Aug 7, 2002
 * Time: 9:44:40 AM
 */
package net.sourceforge.pmd.cpd;

import java.util.Iterator;

public class TextRenderer implements CPDRenderer {
    /**
     * The end of line string for this machine.
     */
    protected String EOL = System.getProperty("line.separator", "\n");

    public String render(CPD cpd) {
        Results results = cpd.getResults();
        StringBuffer sb = new StringBuffer();
        for (Iterator i = results.getTiles(); i.hasNext();) {
            Tile tile = (Tile)i.next();
            sb.append("=============================================================");
            sb.append(EOL);
            sb.append("A " + cpd.getLineCountFor(tile) + " line (" + tile.getTokenCount() + " tokens) duplication:");
            sb.append(EOL);
            for (Iterator j = cpd.getResults().getOccurrences(tile); j.hasNext();) {
                TokenEntry tok = (TokenEntry)j.next();
                sb.append("Starting at line " + tok.getBeginLine() + " in " + tok.getTokenSrcID());
                sb.append(EOL);
            }
            sb.append(cpd.getImage(tile));
            sb.append(EOL);
        }
        return sb.toString();
    }
}
