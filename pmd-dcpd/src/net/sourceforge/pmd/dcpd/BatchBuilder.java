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

        int currentBatchSize = 0;
        int maxBatchSize = 10;

        List wrappers = new ArrayList();
        for (Iterator i = occ.getTiles(); i.hasNext();) {
            Tile tile = (Tile)i.next();
            TileWrapper tileWrapper = new TileWrapper(tile, occ.getOccurrencesList(tile), null, null);
            wrappers.add(tileWrapper);
            currentBatchSize++;

            if (wrappers.size() > maxBatchSize) {
                Batch batch = new Batch(job, wrappers, Batch.NOT_DONE, new Integer(batches.size()));
                batches.add(batch);

                currentBatchSize = 0;
                wrappers = new ArrayList();
            }
        }

        if (currentBatchSize > 0) {
            Batch batch = new Batch(job, wrappers, Batch.NOT_DONE, new Integer(batches.size()));
            batches.add(batch);
        }

        return batches;
    }
}
