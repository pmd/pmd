/*
 * User: tom
 * Date: Sep 11, 2002
 * Time: 3:05:50 PM
 */
package net.sourceforge.pmd.dcpd;

import net.sourceforge.pmd.cpd.Occurrences;
import net.sourceforge.pmd.cpd.Tile;
import net.jini.core.lease.Lease;

import java.util.*;

public class BatchBuilder {

    private Occurrences occ;
    private Job job;

    public BatchBuilder(Occurrences occ, Job job) {
        this.occ = occ;
        this.job = job;
    }

    public List buildBatches() {
        List batches = new ArrayList();

        int tilesSoFar=0;
        for (Iterator i = occ.getTiles(); i.hasNext();) {
            Tile tile = (Tile)i.next();
            TileWrapper tw = new TileWrapper(tile, occ.getOccurrencesList(tile), null, null);
            List wrappers = new ArrayList();
            wrappers.add(tw);
            Batch batch = new Batch(job.id, wrappers, Batch.NOT_DONE, new Integer(tilesSoFar));
            batches.add(batch);
            tilesSoFar++;
            if (tilesSoFar % 100 == 0) {
                System.out.println("Planted " + tilesSoFar + " batches so far");
            }
        }

        return batches;
    }
}
