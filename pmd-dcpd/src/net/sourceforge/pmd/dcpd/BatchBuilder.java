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

        int batchesSoFar=0;
        for (Iterator i = occ.getTiles(); i.hasNext();) {
            Tile tile = (Tile)i.next();
            TileWrapper tileWrapper = new TileWrapper(tile, occ.getOccurrencesList(tile), null, null);
            List wrappers = new ArrayList();
            wrappers.add(tileWrapper);
            Batch batch = new Batch(job.id, wrappers, Batch.NOT_DONE, new Integer(batchesSoFar));
            batches.add(batch);
            batchesSoFar++;
            if (batchesSoFar % 100 == 0) {
                System.out.println("Planted " + batchesSoFar + " batches so far");
            }
        }

        return batches;
    }
}
