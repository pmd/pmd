/*
 * User: tom
 * Date: Aug 1, 2002
 * Time: 2:25:06 PM
 */
package net.sourceforge.pmd.cpd;

import java.util.*;

public interface Results  {
    public void addTile(Tile tile, TokenEntry tok);
    public int getTileLineCount(Tile tile, TokenSets tokenSets);
    public Iterator getOccurrences(Tile tile);
    public List getOccurrencesList(Tile tile);
    public int size();
    public Iterator getTiles();
    public int getOccurrenceCountFor(Tile tile);
}
