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
                addTile(new Tile(tok), tok);
            }
        }
    }

    public void addTile(Tile tile, Token tok) {
        if (!tiles.contains(tile)) {
            List list = new ArrayList();
            list.add(tok);
            tiles.add(tile);
            occurrences.put(tile, list);
        } else {
            List list = (List)occurrences.get(tile);
            list.add(tok);
        }
    }

    public int size() {
        return tiles.size();
    }

    public void deleteSoloTiles() {
        for (Iterator i = tiles.iterator(); i.hasNext();) {
            Tile tile = (Tile)i.next();
            if (((List)occurrences.get(tile)).size() == 1) {
                occurrences.remove(tile);
                i.remove();
            }
        }
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

    public Iterator getTokens(Tile tile) {
        return ((List)occurrences.get(tile)).iterator();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (Iterator i = tiles.iterator(); i.hasNext();) {
            Tile tile = (Tile)i.next();
            sb.append(tile + ":");
            List list = (List)occurrences.get(tile);
            for (Iterator j = list.iterator(); j.hasNext();) {
                Token tok = (Token)j.next();
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
