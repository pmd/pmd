/*
 * User: tom
 * Date: Jul 30, 2002
 * Time: 10:21:15 AM
 */
package net.sourceforge.pmd.cpd;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

public class Occurrences extends TileOccurrences implements Serializable {

    public Occurrences(CPDListener listener) {
        this(new TokenSets(), listener);
    }

    // don't use this, just for Serialization
    protected Occurrences() {
    }

    public Occurrences(TokenSets tss) {
        this(tss, new CPDNullListener());
    }

    public Occurrences(TokenSets tss, CPDListener listener) {
        int doneSoFar = 0;
        int totalCount = tss.tokenCount();
        for (Iterator j = tss.iterator(); j.hasNext();) {
            TokenList ts = (TokenList) j.next();
            if (!listener.addingTokens(totalCount, doneSoFar, ts.getID()))
                break;
            doneSoFar += ts.size();
            for (Iterator i = ts.iterator(); i.hasNext();) {
                TokenEntry tok = (TokenEntry) i.next();
                addTile(new Tile(tok), tok);
            }
        }
    }

    public void deleteSoloTiles() {
        for (Iterator i = orderedTiles.iterator(); i.hasNext();) {
            Tile tile = (Tile) i.next();
            if (((List) tileToOccurrenceMap.get(tile)).size() == 1) {
                tileToOccurrenceMap.remove(tile);
                i.remove();
            }
        }
    }

    public boolean containsAnyTokensIn(Tile tile) {
        for (Iterator i = tile.getTokens().iterator(); i.hasNext();) {
            if (contains((TokenEntry) i.next())) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(TokenEntry tok) {
        for (Iterator i = tileToOccurrenceMap.keySet().iterator(); i.hasNext();) {
            Tile tile = (Tile) i.next();
            if (tile.contains(tok)) {
                return true;
            }
        }
        return false;
    }

    public boolean isEmpty() {
        return orderedTiles.isEmpty();
    }
}
