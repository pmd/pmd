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
    private Results results = new DCPDResultsImpl();

    public DGST(JavaSpace space, Job job, TokenSets tokenSets, int minimumTileSize) {
        this.minimumTileSize = minimumTileSize;
        this.space = space;
        this.tokenSets = tokenSets;
        this.job = job;
    }

    public Results crunch(CPDListener listener) {
        Occurrences occ = new Occurrences(tokenSets, listener);
        try {
            BatchBuilder builder = new BatchBuilder(occ, job);
            List batches = builder.buildBatches();
            TilePlanter planter = new TilePlanter(space, job);
            planter.plant(batches);
            space.write(job, null, Lease.FOREVER);
            expand(occ);
            System.out.println("Done");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    private void expand(Occurrences occ)  throws RemoteException, UnusableEntryException, TransactionException, InterruptedException {
        while (!occ.isEmpty()) {
            TileHarvester tg = new TileHarvester(space, job);
            occ = tg.harvest(occ.size());
            addToResults(occ);
            if (!occ.isEmpty()) {
                System.out.println("**Season complete" + System.getProperty("line.separator") + "->Tile count: " + occ.size() + System.getProperty("line.separator") + "->Tile size: " + ((Tile)(occ.getTiles().next())).getTokenCount());
            }
            BatchBuilder builder = new BatchBuilder(occ, job);
            List batches = builder.buildBatches();
            TilePlanter planter = new TilePlanter(space, job);
            planter.plant(batches);
        }
    }

    private void addToResults(Occurrences occ) {
        for (Iterator i = occ.getTiles(); i.hasNext();) {
            Tile tile = (Tile)i.next();
            if (tile.getTokenCount() >= minimumTileSize) {
                for (Iterator j = occ.getOccurrences(tile); j.hasNext();) {
                    results.addTile(tile, (TokenEntry)j.next());
                }
            }
        }
    }

}
