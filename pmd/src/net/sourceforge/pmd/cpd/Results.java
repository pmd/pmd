/*
 * User: tom
 * Date: Aug 1, 2002
 * Time: 2:25:06 PM
 */
package net.sourceforge.pmd.cpd;

import java.util.*;

public class Results {

    private List tiles = new ArrayList();
    private Map occurrences = new HashMap();

    public void addTile(Tile tile, Token tok) {
        if (!has(tile)) {
            List list = new ArrayList();
            list.add(tok);
            tiles.add(tile);
            occurrences.put(tile, list);
        } else {
            List list = (List)occurrences.get(tile);
            list.add(tok);
        }
    }

    public void consolidate() {
        for (int i=tiles.size()-1; i>=0; i--) {
            Tile tile = (Tile)tiles.get(i);
            removeDupesOf(tile);
        }
    }

    public int size() {
        return tiles.size();
    }

    public Iterator getTiles() {
        return tiles.iterator();
    }

    public Iterator getOccurrences(Tile tile) {
        return ((List)occurrences.get(tile)).iterator();
    }

    private boolean has(Tile candidate) {
        for (Iterator i = tiles.iterator(); i.hasNext();) {
            Tile tile = (Tile)i.next();
            if (tile.equals(candidate)) {
                return true;
            }
        }
        return false;
    }

    private void removeDupesOf(Tile tile) {
        Set occs = new HashSet();
        occs.addAll((List)occurrences.get(tile));
        for (Iterator i = occurrences.keySet().iterator(); i.hasNext();) {
            Tile tile2 = (Tile)i.next();

            if (tile2.equals(tile)) {
                continue;
            }

            Set possibleDupe = new HashSet();
            possibleDupe.addAll((List)occurrences.get(tile2));

            possibleDupe.removeAll(occs);
            if (possibleDupe.isEmpty()) {
                occurrences.remove(tile);
                tiles.remove(tile);
                break;
            }
        }

    }
}
