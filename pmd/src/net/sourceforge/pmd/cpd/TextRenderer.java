/*
 * User: tom
 * Date: Aug 7, 2002
 * Time: 9:44:40 AM
 */
package net.sourceforge.pmd.cpd;

import java.util.Iterator;

public class TextRenderer implements CPDRenderer {
    public String render(CPD cpd) {
        Results results = cpd.getResults();
        StringBuffer sb = new StringBuffer();
        for (Iterator i = results.getTiles(); i.hasNext();) {
            Tile tile = (Tile)i.next();
            sb.append("=============================================================");
            sb.append(System.getProperty("line.separator"));
            sb.append("A " + cpd.getLineCountFor(tile) + " line (" + tile.getTokenCount() + " tokens) duplication in these files:");
            sb.append(System.getProperty("line.separator"));
            for (Iterator j = cpd.getResults().getOccurrences(tile); j.hasNext();) {
                TokenEntry tok = (TokenEntry)j.next();
                sb.append(tok.getBeginLine() + "\t" + tok.getTokenSrcID());
                sb.append(System.getProperty("line.separator"));
            }
            sb.append(cpd.getImage(tile));
            sb.append(System.getProperty("line.separator"));
        }
        return sb.toString();
    }
}
