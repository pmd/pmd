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
            TilePlanter scatterer = new TilePlanter(space, job);
            scatterer.scatter(occ);
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
                System.out.println("**Season complete" + System.getProperty("line.separator") + "->Tile count: " + occ.size() + System.getProperty("line.separator") + "->Tile size: " + ((Tile)(occ.getTiles().next())).getTokenCount() + System.getProperty("line.separator") + "->First tile image: " + ((Tile)(occ.getTiles().next())).getImage());
            }
            TilePlanter scatterer = new TilePlanter(space, job);
            scatterer.scatter(occ);
        }
    }

    private void addToResults(Occurrences occ) {
        for (Iterator i = occ.getTiles(); i.hasNext();) {
            Tile tile = (Tile)i.next();
            if (tile.getTokenCount() >= minimumTileSize) {
                for (Iterator j = occ.getOccurrences(tile); j.hasNext();) {
                    TokenEntry te = (TokenEntry)j.next();
                    results.addTile(tile, te);
                }
            }
        }
    }

/*
    private boolean isDuplicate(Tile candidate) {
        for (Iterator j = results.getTiles(); j.hasNext();) {
            Tile tile = (Tile)j.next();
            for (int i=0;i<tile.getTokens().size(); i++) {
                TokenEntry tok = (TokenEntry)tile.getTokens().get(i);
                for (int k=0; k<candidate.getTokens().size(); k++) {
                    TokenEntry candidateToken = (TokenEntry)candidate.getTokens().get(k);
                    if (tok.getTokenSrcID().equals(candidateToken.getTokenSrcID()) &&
                        tok.getBeginLine() == candidateToken.getBeginLine() &&
                        tok.getImage().equals(candidateToken.getImage())) {
                            System.out.println("DISCARD");
                            return true;
                        }
                }
            }
        }
        return false;
    }
*/
}
