/*
 * User: tom
 * Date: Jul 30, 2002
 * Time: 10:21:15 AM
 */
package net.sourceforge.pmd.cpd;

import java.util.*;

public class Occurrences {

    // as soon as we switch to JDK1.4, change
    // this to use a LinkedHashMap
    private List orderedTiles = new ArrayList();
    private Map tileToOccurrenceMap = new HashMap();

    public Occurrences(TokenSets tss) {
        this(tss, new CPDNullListener());
    }

    public Occurrences(TokenSets tss, CPDListener listener) {
        int doneSoFar = 0;
        int totalCount = tss.tokenCount();
        for (Iterator j = tss.iterator();j.hasNext();) {
            TokenList ts = (TokenList)j.next();
			listener.addingTokens(totalCount, doneSoFar, ts.getID());
            doneSoFar += ts.size();
            for (Iterator i = ts.iterator(); i.hasNext();) {
                TokenEntry tok = (TokenEntry)i.next();
                addTile(new Tile(tok), tok);
            }
        }
    }

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

    public void deleteSoloTiles() {
        for (Iterator i = orderedTiles.iterator(); i.hasNext();) {
            Tile tile = (Tile)i.next();
            if (((List)tileToOccurrenceMap.get(tile)).size() == 1) {
                tileToOccurrenceMap.remove(tile);
                i.remove();
            }
        }
    }

    public boolean containsAnyTokensIn(Tile tile) {
        for (Iterator i = tile.getTokens().iterator(); i.hasNext();) {
            if (contains((TokenEntry)i.next())) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(TokenEntry tok) {
        for (Iterator i = tileToOccurrenceMap.keySet().iterator(); i.hasNext();) {
            Tile tile = (Tile)i.next();
            if (tile.contains(tok)) {
                return true;
            }
        }
        return false;
    }

    public boolean isEmpty() {
        return orderedTiles.isEmpty();
    }

    public Iterator getTiles() {
        return orderedTiles.iterator();
    }

    public Iterator getOccurrences(Tile tile) {
        return ((List)tileToOccurrenceMap.get(tile)).iterator();
    }
		
    public int getOccurrenceCountFor(Tile tile) {
        return ((List)tileToOccurrenceMap.get(tile)).size();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (Iterator i = getTiles(); i.hasNext();) {
            Tile tile = (Tile)i.next();
            sb.append(tile + ":");
            for (Iterator j = getOccurrences(tile); j.hasNext();) {
                TokenEntry tok = (TokenEntry)j.next();
                sb.append(tok+",");
            }
            if (sb.toString().endsWith(",")) {
                sb = new StringBuffer(sb.substring(0, sb.length()-1));
            }
            sb.append(System.getProperty("line.separator"));
        }
        return sb.toString();
    }

}
