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

    public static final Integer DONE = new Integer( 1 );
    public static final Integer NOT_DONE = new Integer( 0 );

    public Tile tile;
    public Integer jobID;
    public List occurrences;
    public Integer isDone;
    public Integer sequenceNumber;
    public Integer expansionNumber;
    public Integer expansionTotal;

    public TileWrapper() {}

    public TileWrapper(Tile tile, List occurrences, Integer jobID, Integer isDone, Integer sequenceNumber, Integer expansionNumber, Integer expansionTotal) {
        this.tile = tile;
        this.jobID = jobID;
        this.occurrences = occurrences;
        this.isDone = isDone;
        this.sequenceNumber = sequenceNumber;
        this.expansionNumber = expansionNumber;
        this.expansionTotal = expansionTotal;
    }
}
