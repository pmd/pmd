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

public class TileExpander {

    private JavaSpace space;
    private TokenSetsWrapper tsw;

    public TileExpander(JavaSpace space, TokenSetsWrapper tsw) {
        this.space = space;
        this.tsw = tsw;
    }

    public void expandAvailableTiles() throws RemoteException, UnusableEntryException, TransactionException, InterruptedException{
        Entry twQuery = space.snapshot(new TileWrapper(null, null, tsw.jobID, TileWrapper.NOT_DONE, null, null, null, null));

        TileWrapper tileWrapperToExpand = null;
        int total = 0;
        while ((tileWrapperToExpand = (TileWrapper)space.take(twQuery, null, 10)) != null) {
            total++;
            //System.out.println("Expanding " + tileWrapperToExpand.tile.getImage());
            Occurrences results = expand(tileWrapperToExpand);
            int expansionIndex = 0;
            for (Iterator i = results.getTiles();i.hasNext();) {
                Tile tile = (Tile)i.next();
                TileWrapper tileWrapperToWrite = new TileWrapper(tile,
                                                    marshal(results.getOccurrences(tile)),
                                                    tsw.jobID,
                                                    TileWrapper.DONE,
                                                    null,
                                                    tileWrapperToExpand.originalTilePosition,
                                                    new Integer(expansionIndex),
                                                    new Integer(results.size()));
                space.write(tileWrapperToWrite, null, Lease.FOREVER);
                //System.out.println("Wrote " + tileWrapperToWrite + "; occurrences = " + tileWrapperToWrite.occurrences.size());
                expansionIndex++;
            }
        }
        if (total>0) System.out.println("Expanded " + total + " tiles");
    }


    private List marshal(Iterator i) {
        List list = new ArrayList();
        while (i.hasNext()) {
            list.add(i.next());
        }
        return list;
    }

    private Occurrences expand(TileWrapper tileWrapper)  throws RemoteException, UnusableEntryException, TransactionException, InterruptedException{
        Occurrences newOcc = new Occurrences(new CPDNullListener());
        for (Iterator i = tileWrapper.occurrences.iterator(); i.hasNext();) {
            TokenEntry tok = (TokenEntry)i.next();
            TokenList tokenSet = tsw.tokenSets.getTokenList(tok);
            if (tokenSet.hasTokenAfter(tileWrapper.tile, tok)) {
                TokenEntry token = (TokenEntry)tokenSet.get(tok.getIndex() + tileWrapper.tile.getTokenCount());
                // make sure the next token hasn't already been used in an occurrence
                if (!newOcc.contains(token)) {
                    Tile newTile = tileWrapper.tile.copy();
                    newTile.add(token);
                    newOcc.addTile(newTile, tok);
                }
            } else {
                // we have to put something back in the space to let DGST know that
                // this tile has been processed...
                TileWrapper tileWrapperToWrite = new TileWrapper(tileWrapper.tile,
                                                    new ArrayList(),
                                                    tsw.jobID,
                                                    TileWrapper.DONE,
                                                    TileWrapper.DISCARD_DUE_TO_EOF,
                                                    tileWrapper.originalTilePosition,
                                                    new Integer(0),
                                                    null);
                space.write(tileWrapperToWrite, null, Lease.FOREVER);
            }
        }
        return newOcc;
    }
}
