/*
 * User: tom
 * Date: Sep 5, 2002
 * Time: 11:26:28 AM
 */
package net.sourceforge.pmd.dcpd;

import net.jini.core.entry.UnusableEntryException;
import net.jini.core.entry.Entry;
import net.jini.core.transaction.TransactionException;
import net.jini.core.lease.Lease;
import net.jini.space.JavaSpace;
import net.sourceforge.pmd.cpd.*;

import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class TileExpander {

    private JavaSpace space;
    private Map jobs;

    public TileExpander(JavaSpace space, Map jobs) {
        this.space = space;
        this.jobs = jobs;
    }

    public void expandAvailableTiles() throws RemoteException, UnusableEntryException, TransactionException, InterruptedException{
        Entry twQuery = space.snapshot(new Batch(null, null, Batch.NOT_DONE, null));

        Batch batch = null;
        int total = 0;
        while ((batch = (Batch)space.take(twQuery, null, 250)) != null) {
            TokenSetsWrapper tsw = (TokenSetsWrapper)jobs.get(batch.job);
            total++;
            List wrappers = new ArrayList();
            for (int i=0; i<batch.tileWrappers.size(); i++) {
                TileWrapper tileWrapperToExpand = (TileWrapper)batch.tileWrappers.get(i);
                //System.out.println("Expanding " + tileWrapperToExpand.tile.getImage());
                Occurrences results = expand(tsw, tileWrapperToExpand);
                int expansionIndex = 0;
                for (Iterator iter = results.getTiles();iter.hasNext();) {
                    Tile tile = (Tile)iter.next();
                    // here's where we drop solo tiles
                    if (results.getOccurrenceCountFor(tile) == 1) {
                        continue;
                    }
                    TileWrapper tileWrapperToWrite = new TileWrapper(tile, results.getOccurrencesList(tile), new Integer(expansionIndex), new Integer(results.size()));
                    wrappers.add(tileWrapperToWrite);
                    //System.out.println("Wrote " + tileWrapperToWrite + "; occurrences = " + tileWrapperToWrite.occurrences.size());
                    expansionIndex++;
                }
            }
            Batch batchToWrite = new Batch(batch.job, wrappers, Batch.DONE, batch.sequenceID);
            space.write(batchToWrite, null, Lease.FOREVER);
        }
        if (total>0) System.out.println("Expanded " + total + " tiles");
    }

    private Occurrences expand(TokenSetsWrapper tsw, TileWrapper tileWrapper)  throws RemoteException, UnusableEntryException, TransactionException, InterruptedException{
        Occurrences newOcc = new Occurrences(new CPDNullListener());
        for (Iterator i = tileWrapper.occurrences.iterator(); i.hasNext();) {
            TokenEntry tok = (TokenEntry)i.next();
            TokenList tokenSet = tsw.tokenSets.getTokenList(tok);
            if (tokenSet.hasTokenAfter(tileWrapper.tile, tok)) {
                TokenEntry token = (TokenEntry)tokenSet.get(tok.getIndex() + tileWrapper.tile.getTokenCount());
                if (!newOcc.contains(token)) {
                    Tile newTile = tileWrapper.tile.copy();
                    newTile.add(token);
                    newOcc.addTile(newTile, tok);
                }
            }
        }
        return newOcc;
    }
}
