/*
 * User: tom
 * Date: Aug 1, 2002
 * Time: 2:25:06 PM
 */
package net.sourceforge.pmd.cpd;

import java.util.*;

public class Results {

    // as soon as we switch to JDK1.4, change
    // this to use a LinkedHashMap
    private List orderedTiles = new ArrayList();
    private Map tileToOccurrenceMap = new HashMap();

    public void addTile(Tile tile, TokenEntry tok) {
        if (!orderedTiles.contains(tile)) {
            List list = new ArrayList();
            list.add(tok);
            orderedTiles.add(tile);
            tileToOccurrenceMap.put(tile, list);
        } else {
            List list = (List)tileToOccurrenceMap.get(tile);
            list.add(tok);
        }

        for (int i=orderedTiles.size()-1; i>=0; i--) {
            Tile candidate = (Tile)orderedTiles.get(i);
            removeDupesOf(candidate);
        }
    }

    public int getTileLineCount(Tile tile, TokenSets tokenSets) {
        Iterator i = getOccurrences(tile);
        TokenEntry firstToken = (TokenEntry)i.next();
        TokenList tl = tokenSets.getTokenList(firstToken);
        int lastTokenIndex = firstToken.getIndex() + tile.getTokenCount();
        TokenEntry lastToken = (TokenEntry)tl.get(lastTokenIndex);
        return (lastToken.getBeginLine()+1) - firstToken.getBeginLine();
    }

    public int size() {
        return orderedTiles.size();
    }

    public Iterator getTiles() {
        return orderedTiles.iterator();
    }

    public Iterator getOccurrences(Tile tile) {
        return ((List)tileToOccurrenceMap.get(tile)).iterator();
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
