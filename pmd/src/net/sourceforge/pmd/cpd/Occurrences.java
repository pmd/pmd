/*
 * User: tom
 * Date: Jul 30, 2002
 * Time: 10:21:15 AM
 */
package net.sourceforge.pmd.cpd;

import java.util.*;

public class Occurrences {

    private List tiles = new ArrayList();
    private Map occurrences = new HashMap();

    public Occurrences(TokenSets tss) {
        for (Iterator j = tss.iterator();j.hasNext();) {
            TokenList ts = (TokenList)j.next();
            for (Iterator i = ts.iterator(); i.hasNext();) {
                Token tok = (Token)i.next();
                Tile tile = new Tile(tok);
                Occurrence occ = new Occurrence(ts.getID(), tok);
                addTile(tile, occ);
            }
        }
    }

    public void addTile(Tile tile, Occurrence occ) {
        if (!has(tile)) {
            List list = new ArrayList();
            list.add(occ);
            tiles.add(tile);
            occurrences.put(tile, list);
        } else {
            List list = (List)occurrences.get(tile);
            list.add(occ);
        }
    }

    public int size() {
        return tiles.size();
    }

    public void deleteSoloTiles() {
        for (Iterator i = tiles.iterator(); i.hasNext();) {
            Tile tile = (Tile)i.next();
            List occurrenceList = (List)occurrences.get(tile);
            if (occurrenceList.size() == 1) {
                occurrences.remove(tile);
                i.remove();
            }
        }
    }

    private boolean has(Tile candidate) {
        return tiles.contains(candidate);
    }

    public boolean containsAnyTokensIn(Tile tile) {
        for (Iterator i = tile.getTokens().iterator(); i.hasNext();) {
            if (contains((Token)i.next())) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(Token tok) {
        for (Iterator i = occurrences.keySet().iterator(); i.hasNext();) {
            Tile tile = (Tile)i.next();
            if (tile.contains(tok)) {
                return true;
            }
        }
        return false;
    }

    public boolean isEmpty() {
        return tiles.isEmpty();
    }

    public Iterator getTiles() {
        return tiles.iterator();
    }

    public Iterator getOccurrences(Tile tile) {
        return ((List)occurrences.get(tile)).iterator();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (Iterator i = tiles.iterator(); i.hasNext();) {
            Tile tile = (Tile)i.next();
            sb.append(tile + ":");
            List list = (List)occurrences.get(tile);
            for (Iterator j = list.iterator(); j.hasNext();) {
                Occurrence occ = (Occurrence)j.next();
                sb.append(occ+",");
            }
            if (sb.toString().endsWith(",")) {
                sb = new StringBuffer(sb.substring(0, sb.length()-1));
            }
            sb.append(System.getProperty("line.separator"));
        }
        return sb.toString();
    }

}
