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

    public void crunch(CPDListener listener) {
        Occurrences occ = new Occurrences(tokenSets, listener);
        try {
            scatter(occ);
            space.write(job, null, Lease.FOREVER);
            expand(occ);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void expand(Occurrences occ)  throws RemoteException, UnusableEntryException, TransactionException, InterruptedException {
        while (!occ.isEmpty()) {
            occ = gather(occ.size()-1);
            scatter(occ);
            System.out.println("scatter..gather complete; tile count now " + occ.size());
        }
    }

    private void scatter(Occurrences occ) throws TransactionException, RemoteException {
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
            tilesSoFar++;
            if (tilesSoFar % 10 == 0) {
                System.out.println("Written " + tilesSoFar + " tiles so far");
            }
        }
    }

    private Occurrences gather(int originalOccurrencesCount) throws RemoteException, UnusableEntryException, TransactionException, InterruptedException {
        System.out.println("STARTING TO GATHER");
        Occurrences occ = new Occurrences(new CPDNullListener());
        for (int i=0;i<originalOccurrencesCount; i++) {

            // this gets tile (6:0/3:4:0/2:3)
            TileWrapper tw = (TileWrapper)space.take(new TileWrapper(null,
                    null,
                    job.id,
                    TileWrapper.DONE,
                    new Integer(i), null, null), null, Lease.FOREVER);
            System.out.println("Took " + tw.getExpansionIndexPicture());

            addAllExpansions(i, tw, occ);
        }
        System.out.println("DONE GATHERING");

        return occ;
    }

    private void addAllExpansions(int originalPosition, TileWrapper firstTileWrapper, Occurrences occ) throws RemoteException, UnusableEntryException, TransactionException, InterruptedException {
        addTileWrapperToOccurrences(firstTileWrapper, occ);
        for (int i=1; i<firstTileWrapper.expansionsTotal.intValue(); i++) {
            TileWrapper nextExpansion = (TileWrapper)space.take(new TileWrapper(null,
                    null,
                    firstTileWrapper.jobID,
                    TileWrapper.DONE,
                    new Integer(originalPosition),
                    new Integer(i),
                    firstTileWrapper.expansionsTotal), null, Lease.FOREVER);
            System.out.println("Took " + nextExpansion.getExpansionIndexPicture());
            addTileWrapperToOccurrences(nextExpansion, occ);
        }
    }

    private void addTileWrapperToOccurrences(TileWrapper tw, Occurrences occ) {
        for (int i=0; i<tw.occurrences.size(); i++) {
            if (!occ.containsAnyTokensIn(tw.tile)) {
                occ.addTile(tw.tile, (TokenEntry)tw.occurrences.get(i));
            }
        }
    }


    private List marshal(Iterator i) {
        List list = new ArrayList();
        while (i.hasNext()) {
            list.add(i.next());
        }
        return list;
    }

    private void expandTile(Occurrences oldOcc, Occurrences newOcc, Tile tile, CPDListener listener, int tilesSoFar, int totalTiles) {
        for (Iterator i = oldOcc.getOccurrences(tile); i.hasNext();) {
            TokenEntry tok = (TokenEntry)i.next();
            TokenList tokenSet = tokenSets.getTokenList(tok);
            if (tokenSet.hasTokenAfter(tile, tok)) {
                TokenEntry token = (TokenEntry)tokenSet.get(tok.getIndex() + tile.getTokenCount());
                // make sure the next token hasn't already been used in an occurrence
                if (!newOcc.contains(token)) {
                    Tile newTile = tile.copy();
                    newTile.add(token);
                    newOcc.addTile(newTile, tok);
					listener.addedNewTile(newTile, tilesSoFar, totalTiles);
                }
            }
        }
        newOcc.deleteSoloTiles();
    }

}
