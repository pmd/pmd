/*
 * User: tom
 * Date: Aug 1, 2002
 * Time: 2:25:06 PM
 */
package net.sourceforge.pmd.cpd;

import java.util.*;

public class Results {

    private List orderedTiles = new ArrayList();
    private Map tileToOccurrenceMap = new HashMap();

    public void addTile(Tile tile, Token tok) {
        if (!orderedTiles.contains(tile)) {
            List list = new ArrayList();
            list.add(tok);
            orderedTiles.add(tile);
            tileToOccurrenceMap.put(tile, list);
        } else {
            List list = (List)tileToOccurrenceMap.get(tile);
            list.add(tok);
        }
    }

    public void consolidate() {
        for (int i=orderedTiles.size()-1; i>=0; i--) {
            Tile tile = (Tile)orderedTiles.get(i);
            removeDupesOf(tile);
        }
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
