/*
 * User: tom
 * Date: Aug 1, 2002
 * Time: 2:25:06 PM
 */
package net.sourceforge.pmd.cpd;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ResultsImpl extends TileOccurrences implements Serializable, Results {

    public void addTile(Tile tile, TokenEntry tok) {
        super.addTile(tile, tok);
        for (int i=orderedTiles.size()-1; i>=0; i--) {
            Tile candidate = (Tile)orderedTiles.get(i);
            removeDupesOf(candidate);
        }
    }

    public int getTileLineCount(Tile tile, TokenSets tokenSets) {
        TokenEntry firstToken = (TokenEntry)((List)tileToOccurrenceMap.get(tile)).get(0);
        TokenList tl = tokenSets.getTokenList(firstToken);
        TokenEntry lastToken = (TokenEntry)tl.get(firstToken.getIndex()-1 + tile.getTokenCount());
        int result = lastToken.getBeginLine() - firstToken.getBeginLine() + 1;
        if (result == 0) {
            result = 1;
        }
        return result;
    }

    private void removeDupesOf(Tile tile) {
        Set occs = new HashSet();
        occs.addAll((List)tileToOccurrenceMap.get(tile));
        for (Iterator i = tileToOccurrenceMap.keySet().iterator(); i.hasNext();) {
            Tile tile2 = (Tile)i.next();

            if (tile2.equals(tile)) {
                continue;
            }

            Set possibleDupe = new HashSet();
            possibleDupe.addAll((List)tileToOccurrenceMap.get(tile2));
            possibleDupe.removeAll(occs);
            if (possibleDupe.isEmpty()) {
                tileToOccurrenceMap.remove(tile);
                orderedTiles.remove(tile);
                break;
            }
        }

    }
}
