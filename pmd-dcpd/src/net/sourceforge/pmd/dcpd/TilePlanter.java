/*
 * User: tom
 * Date: Sep 6, 2002
 * Time: 4:11:42 PM
 */
package net.sourceforge.pmd.dcpd;

import net.sourceforge.pmd.cpd.Occurrences;
import net.sourceforge.pmd.cpd.Tile;
import net.jini.core.transaction.TransactionException;
import net.jini.core.lease.Lease;
import net.jini.space.JavaSpace;

import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public class TilePlanter {

    private Job job;
    private JavaSpace space;

    public TilePlanter(JavaSpace space, Job job) {
        this.job = job;
        this.space = space;
    }

    public void scatter(Occurrences occ) throws TransactionException, RemoteException {
        int tilesSoFar=0;
        for (Iterator i = occ.getTiles(); i.hasNext();) {
            Tile tile = (Tile)i.next();
            TileWrapper tw = new TileWrapper(tile, occ.getOccurrencesList(tile), null, null);
            List wrappers = new ArrayList();
            wrappers.add(tw);
            Batch batch = new Batch(job.id, wrappers, Batch.NOT_DONE, new Integer(tilesSoFar));
            space.write(batch, null, Lease.FOREVER);
            //System.out.println("Scattering " + tw.tile.getImage() +  "->" + tw.occurrences.size());
            tilesSoFar++;
            if (tilesSoFar % 100 == 0) {
                System.out.println("Planted " + tilesSoFar + " batches so far");
            }
        }
    }
}
