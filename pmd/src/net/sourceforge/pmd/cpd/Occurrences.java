/*
 * User: tom
 * Date: Jul 30, 2002
 * Time: 10:21:15 AM
 */
package net.sourceforge.pmd.cpd;

import java.util.*;

public class Occurrences {

    private List orderedTiles = new ArrayList();
    private Map tileToOccurrenceMap = new HashMap();

		public Occurrences(TokenSets tss) {
			this(tss, new CPD.NullListener());
		}
    public Occurrences(TokenSets tss, CPD.Listener listener) {
        for (Iterator j = tss.iterator();j.hasNext();) {
            TokenList ts = (TokenList)j.next();
						listener.update("Adding token set " + ts.getID() + " to the initial frequency table");
            for (Iterator i = ts.iterator(); i.hasNext();) {
                Token tok = (Token)i.next();
                addTile(new Tile(tok), tok);
            }
        }
    }

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
            if (contains((Token)i.next())) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(Token tok) {
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
