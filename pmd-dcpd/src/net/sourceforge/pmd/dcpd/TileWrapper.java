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
    public Integer jobID;
    public List occurrences;

    public TileWrapper() {}

    public TileWrapper(Tile tile, List occurrences, Integer jobID) {
        this.tile = tile;
        this.jobID = jobID;
        this.occurrences = occurrences;
    }
}
