/*
 * User: tom
 * Date: Aug 27, 2002
 * Time: 1:53:58 PM
 */
package net.sourceforge.pmd.dcpd;

import net.sourceforge.pmd.cpd.*;
import net.jini.space.JavaSpace;
import net.jini.core.entry.Entry;
import net.jini.core.lease.Lease;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public class DGST {

    private int minimumTileSize;
    private TokenSets tokenSets;
    private JavaSpace space;
    private Job job;

    public DGST(JavaSpace space, Job job, TokenSets tokenSets, int minimumTileSize) {
        this.minimumTileSize = minimumTileSize;
        this.space = space;
        this.tokenSets = tokenSets;
        this.job = job;
    }

    public Results crunch(CPDListener listener) {
        Results results = new Results();
        Occurrences occ = new Occurrences(tokenSets, listener);

        // write all the Tiles in the current Occurrences to the space
        try {
            int tilesSoFar=0;
            for (Iterator i = occ.getTiles(); i.hasNext();) {
                Tile tile = (Tile)i.next();
                TileWrapper tw = new TileWrapper(tile, marshal(occ.getOccurrences(tile)), job.id);
                space.write(tw, null, Lease.FOREVER);
                tilesSoFar++;
                if (tilesSoFar % 10 == 0) {
                    System.out.println("tilesSoFar = " + tilesSoFar);
                }
            }

            System.out.println("Writing the Job to the space");
            space.write(job, null, Lease.FOREVER);
        } catch (Exception e) {
            e.printStackTrace();
        }




/*
        while (!occ.isEmpty()) {
			listener.update("Tiles left to be crunched " + occ.size());



            // add any tiles over the minimum size to the results
			listener.update("Adding large tiles to results");
            for (Iterator i = occ.getTiles(); i.hasNext();) {
                Tile tile = (Tile)i.next();
                if (tile.getTokenCount() >= minimumTileSize) {
                    for (Iterator j = occ.getOccurrences(tile); j.hasNext();) {
                        results.addTile(tile, (TokenEntry)j.next());
                    }
                }
            }

            Occurrences newOcc = new Occurrences(listener);
            int tilesSoFar = 0;
            int totalTiles = occ.size();
            for (Iterator i = occ.getTiles(); i.hasNext();) {
                tilesSoFar++;
                Tile tile = (Tile)i.next();
                if (!newOcc.containsAnyTokensIn(tile)) {
                    expandTile(occ, newOcc, tile, listener, tilesSoFar, totalTiles);
                }
            }
            occ = newOcc;
        }
*/
        return results;
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
