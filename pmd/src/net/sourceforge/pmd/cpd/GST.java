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

        Occurrences occ =new Occurrences(tokenSets);

        while (!occ.isEmpty()) {
            occ.deleteSoloTiles();

            // add any tiles over the minimum size to the results
            for (Iterator i = occ.getTiles(); i.hasNext();) {
                Tile tile = (Tile)i.next();
                if (tile.getTokenCount() >= minimumTileSize) {
                    for (Iterator j = occ.getOccurrences(tile); j.hasNext();) {
                        results.addTile(tile, (Token)j.next());
                        results.consolidate();
                    }
                }
            }

            Occurrences newOcc = new Occurrences(new TokenSets());
            for (Iterator i = occ.getTiles(); i.hasNext();) {
                Tile tile = (Tile)i.next();
                if (!newOcc.containsAnyTokensIn(tile)) {
                    expandTile(occ, newOcc, tile );
                }
            }
            occ = newOcc;
        }

        return results;
    }

    private void expandTile(Occurrences oldOcc, Occurrences newOcc, Tile tile) {
        for (Iterator i = oldOcc.getOccurrences(tile); i.hasNext();) {
            Token tok = (Token)i.next();
            TokenList tokenSet = tokenSets.getTokenSet(tok);
            if (tokenSet.hasTokenAfter(tile, tok)) {
                Token token = (Token)tokenSet.get(tok.getIndex() + tile.getTokenCount());
                // make sure the next token hasn't already been used in an occurrence
                if (!newOcc.contains(token)) {
                    Tile newTile = tile.copy();
                    newTile.add(token);
                    newOcc.addTile(newTile, tok);
                }
            }
        }
    }
}
