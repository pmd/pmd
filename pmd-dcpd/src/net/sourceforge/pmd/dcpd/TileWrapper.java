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

    // routing information
    public static final Integer DONE = new Integer( 1 );
    public static final Integer NOT_DONE = new Integer( 0 );
    public static final Integer DISCARD_DUE_TO_EOF = new Integer(0);
    public Integer isDone;
    public Integer discardFlag;

    // payload information
    public Tile tile;
    public Integer jobID;
    public List occurrences;
    public Integer originalTilePosition;
    public Integer expansionIndex;
    public Integer expansionsTotal;


    public TileWrapper() {}

    public TileWrapper(Tile tile, List occurrences, Integer jobID, Integer isDone, Integer discardFlag, Integer originalTilePosition, Integer expansionIndex, Integer expansionsTotal) {
        this.tile = tile;
        this.jobID = jobID;
        this.occurrences = occurrences;
        this.isDone = isDone;
        this.discardFlag = discardFlag;
        this.originalTilePosition = originalTilePosition;
        this.expansionIndex = expansionIndex;
        this.expansionsTotal = expansionsTotal;
    }

    public String toString() {
        return tile.getImage() + ":" + isDone + ":" + getExpansionIndexPicture();
    }

    public String getExpansionIndexPicture() {
        return "(" + originalTilePosition + ":" + expansionIndex + "/" + expansionsTotal + ")";
    }
}
