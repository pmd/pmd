/*
 * User: tom
 * Date: Aug 1, 2002
 * Time: 2:25:06 PM
 */
package net.sourceforge.pmd.cpd;

import java.util.Iterator;
import java.util.List;

public interface Results {
    void addTile(Tile tile, TokenEntry tok);

    int getTileLineCount(Tile tile, TokenSets tokenSets);

    Iterator getOccurrences(Tile tile);

    List getOccurrencesList(Tile tile);

    int size();

    Iterator getTiles();

    int getOccurrenceCountFor(Tile tile);
}
