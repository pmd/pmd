/*
 * User: tom
 * Date: Jul 30, 2002
 * Time: 10:55:08 AM
 */
package net.sourceforge.pmd.cpd;

import java.util.Iterator;
import java.util.List;

public class GST {

    private int minimumTileSize;
    private Occurrences occurrences;
    private TokenSets tokenSets;

    private Occurrences results = new Occurrences();

    public GST(TokenSets tokenSets, Occurrences occurrences, int minimumTileSize) {
        this.minimumTileSize = minimumTileSize;
        this.occurrences = occurrences;
        this.tokenSets = tokenSets;
    }

    public void crunch() {
        while (!occurrences.isEmpty()) {
            occurrences.deleteSoloTiles();

            // add any tiles over the minimum size to the results
            for (Iterator i = occurrences.getTiles(); i.hasNext();) {
                Tile tile = (Tile)i.next();
                if (tile.getTokenCount() >= minimumTileSize) {
                    for (Iterator j = occurrences.getOccurrences(tile); j.hasNext();) {
                        Occurrence occ = (Occurrence)j.next();
                        results.addTile(tile, occ);
                        results.consolidate();
                    }
                }
            }

            Occurrences newOccurrences = new Occurrences();
            for (Iterator i = occurrences.getTiles(); i.hasNext();) {
                Tile tile = (Tile)i.next();
                expandTile(newOccurrences, tile );
            }
            occurrences = newOccurrences;
        }
    }

    public Occurrences getResults() {
        return results;
    }

    private void expandTile(Occurrences newOcc, Tile tile) {
        // make sure the tile doesn't have anything that's used already
        for (Iterator i = tile.getTokens().iterator(); i.hasNext();) {
            if (newOcc.contains((Token)i.next())) {
                return;
            }
        }

        for (Iterator i = occurrences.getOccurrences(tile); i.hasNext();) {
            Occurrence occ = (Occurrence)i.next();
            TokenSet tokenSet = tokenSets.getTokenSet(occ);
            if (tokenSet.hasTokenAfter(tile, occ)) {
                Token token = (Token)tokenSet.get(occ.getIndex() + tile.getTokenCount());
                // make sure the next token hasn't already been used in an occurrence
                if (!newOcc.contains(token)) {
                    Tile newTile = tile.copy();
                    newTile.add(token);
                    newOcc.addTile(newTile, occ);
                }
            }
        }
    }
}
