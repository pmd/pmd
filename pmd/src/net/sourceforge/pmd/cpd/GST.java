/*
 * User: tom
 * Date: Jul 30, 2002
 * Time: 10:55:08 AM
 */
package net.sourceforge.pmd.cpd;

import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class GST {

    private int minimumTileSize;
    private TokenSets tokenSets;

    public GST(TokenSets tokenSets, int minimumTileSize) {
        this.minimumTileSize = minimumTileSize;
        this.tokenSets = tokenSets;
    }

    public Results crunch() {
        Results results = new Results();

        Occurrences occurrences =new Occurrences(tokenSets);

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

            Occurrences newOccurrences = new Occurrences(new TokenSets());
            for (Iterator i = occurrences.getTiles(); i.hasNext();) {
                Tile tile = (Tile)i.next();
                if (!newOccurrences.containsAnyTokensIn(tile)) {
                    expandTile(occurrences, newOccurrences, tile );
                }
            }
            occurrences = newOccurrences;
        }

        return results;
    }

    private void expandTile(Occurrences oldOcc, Occurrences newOcc, Tile tile) {
        for (Iterator i = oldOcc.getOccurrences(tile); i.hasNext();) {
            Occurrence occ = (Occurrence)i.next();
            TokenList tokenSet = tokenSets.getTokenSet(occ);
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
