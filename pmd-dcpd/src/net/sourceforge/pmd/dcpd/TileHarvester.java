/*
 * User: tom
 * Date: Sep 5, 2002
 * Time: 12:03:03 PM
 */
package net.sourceforge.pmd.dcpd;

import net.sourceforge.pmd.cpd.Occurrences;
import net.sourceforge.pmd.cpd.CPDNullListener;
import net.sourceforge.pmd.cpd.TokenEntry;
import net.sourceforge.pmd.cpd.Results;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.transaction.TransactionException;
import net.jini.core.lease.Lease;
import net.jini.space.JavaSpace;

import java.rmi.RemoteException;

public class TileHarvester {

    private JavaSpace space;
    private Job job;

    public TileHarvester(JavaSpace space, Job job) {
        this.space = space;
        this.job = job;
    }

    public Occurrences harvest(int batchesToHarvest) throws RemoteException, UnusableEntryException, TransactionException, InterruptedException {
        Occurrences occ = new Occurrences(new CPDNullListener());
        for (int i=0;i<batchesToHarvest; i++) {
            Batch batch = (Batch)space.take(new Batch(job.id,  null, Batch.DONE, new Integer(i)), null, Lease.FOREVER);
            for (int j=0; j<batch.tileWrappers.size(); j++) {
                addTileWrapperToOccurrences((TileWrapper)batch.tileWrappers.get(j), occ);
            }
            if (i >0 && i % 100 == 0) {
                System.out.println("Harvested " + i + " batches so far");
            }
        }
        return occ;
    }

    private void addTileWrapperToOccurrences(TileWrapper tw, Occurrences occ) {
        if (tw.occurrences.size() > 1 && !occ.containsAnyTokensIn(tw.tile)) {
            for (int i=0; i<tw.occurrences.size(); i++) {
                occ.addTile(tw.tile, (TokenEntry)tw.occurrences.get(i));
            }
        }
    }

}
