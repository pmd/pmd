/*
 * User: tom
 * Date: Aug 27, 2002
 * Time: 1:53:58 PM
 */
package net.sourceforge.pmd.dcpd;

import net.sourceforge.pmd.cpd.*;
import net.jini.space.JavaSpace;
import net.jini.core.entry.Entry;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.lease.Lease;
import net.jini.core.transaction.TransactionException;
import java.rmi.RemoteException;
import java.util.*;

public class DGST {

    private int minimumTileSize;
    private TokenSets tokenSets;
    private JavaSpace space;
    private Job job;
    private Results results = new Results();

    public DGST(JavaSpace space, Job job, TokenSets tokenSets, int minimumTileSize) {
        this.minimumTileSize = minimumTileSize;
        this.space = space;
        this.tokenSets = tokenSets;
        this.job = job;
    }

    public Results crunch(CPDListener listener) {
        Occurrences occ = new Occurrences(tokenSets, listener);
        try {
            scatter(occ);
            space.write(job, null, Lease.FOREVER);
            expand(occ);
            System.out.println("DONE");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    private void expand(Occurrences occ)  throws RemoteException, UnusableEntryException, TransactionException, InterruptedException {
        while (!occ.isEmpty()) {
            TileGatherer tg = new TileGatherer(space, job);
            occ = tg.gather(occ.size());
            addToResults(occ);
            System.out.println("************* Scatter..gather complete; tile count now " + occ.size());
            scatter(occ);
        }
    }

    private void addToResults(Occurrences occ) {
        for (Iterator i = occ.getTiles(); i.hasNext();) {
            Tile tile = (Tile)i.next();
            if (tile.getTokenCount() > this.minimumTileSize) {
                for (Iterator j = occ.getOccurrences(tile); j.hasNext();) {
                    TokenEntry te = (TokenEntry)j.next();
                    results.addTile(tile, te);
                }
            }
        }
    }

    private void scatter(Occurrences occ) throws TransactionException, RemoteException {
        System.out.println("Scattering");
        int tilesSoFar=0;
        for (Iterator i = occ.getTiles(); i.hasNext();) {
            Tile tile = (Tile)i.next();
            TileWrapper tw = new TileWrapper(tile,
                    marshal(occ.getOccurrences(tile)),
                    job.id,
                    TileWrapper.NOT_DONE,
                    new Integer(tilesSoFar),
                    null, null);
            space.write(tw, null, Lease.FOREVER);
            //System.out.println("Scattering " + tw);
            tilesSoFar++;
            if (tilesSoFar % 25 == 0) {
                System.out.println("Written " + tilesSoFar + " tiles so far");
            }
        }
        System.out.println("Done scattering");
    }

    private List marshal(Iterator i) {
        List list = new ArrayList();
        while (i.hasNext()) {
            list.add(i.next());
        }
        return list;
    }

}
