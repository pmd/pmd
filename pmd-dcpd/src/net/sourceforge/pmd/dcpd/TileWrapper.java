/*
 * User: tom
 * Date: Aug 27, 2002
 * Time: 2:33:41 PM
 */
package net.sourceforge.pmd.dcpd;

import net.jini.core.entry.Entry;
import net.sourceforge.pmd.cpd.Tile;

import java.util.List;

public class TileWrapper implements Entry {

    public Tile tile;
    public List occurrences;
    public Integer expansionIndex;
    public Integer expansionsTotal;

    public TileWrapper() {}

    public TileWrapper(Tile tile, List occurrences, Integer expansionIndex, Integer expansionsTotal) {
        this.tile = tile;
        this.occurrences = occurrences;
        this.expansionIndex = expansionIndex;
        this.expansionsTotal = expansionsTotal;
    }

    public String toString() {
        return tile.getImage() + ":" + getExpansionIndexPicture();
    }

    public String getExpansionIndexPicture() {
        return "(" + expansionIndex + "/" + expansionsTotal + ")";
    }
}
