/*
 * User: tom
 * Date: Aug 7, 2002
 * Time: 2:59:37 PM
 */
package net.sourceforge.pmd.cpd;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class TileOccurrences implements Serializable {
    // as soon as we switch to JDK1.4, change
    // this to use a LinkedHashMap
    protected List orderedTiles = new ArrayList();
    protected Map tileToOccurrenceMap = new HashMap();

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

    public List getOccurrencesList(Tile tile) {
        return (List)tileToOccurrenceMap.get(tile);
    }

    public int getOccurrenceCountFor(Tile tile) {
        return ((List)tileToOccurrenceMap.get(tile)).size();
    }


}
