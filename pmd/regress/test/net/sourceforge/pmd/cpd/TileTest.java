/*
 * User: tom
 * Date: Jul 31, 2002
 * Time: 10:08:40 AM
 */
package test.net.sourceforge.pmd.cpd;

import junit.framework.TestCase;
import net.sourceforge.pmd.cpd.Tile;
import net.sourceforge.pmd.cpd.TokenEntry;

import java.util.ArrayList;
import java.util.List;

public class TileTest extends TestCase {

    public void testConstructors() {
        TokenEntry tok = new TokenEntry("a", 2, "foo", 5);
        Tile tile = new Tile(tok);
        assertEquals(1, tile.getTokenCount());

        List tokens = new ArrayList();
        tokens.add(tok);
        tile = new Tile(tokens);
        assertEquals(1, tile.getTokenCount());
    }

    public void testCopy() {
        TokenEntry tok = new TokenEntry("a", 2, "foo", 5);
        Tile tile = new Tile(tok);
        Tile tileCopy = tile.copy();
        assertEquals(1, tile.getTokenCount());
        assertEquals(tok, tileCopy.getTokens().get(0));
    }

    public void testEquality() {
        Tile tile = new Tile(new TokenEntry("a", 2, "foo", 5));
        Tile tile2 = new Tile(new TokenEntry("a", 2, "foo", 5));
        assertEquals(tile, tile2);
        assertEquals(tile.hashCode(), tile2.hashCode());
    }

    public void testContains() {
        TokenEntry tok = new TokenEntry("a", 2, "foo", 5);
        Tile tile = new Tile(tok);
        assertTrue(tile.contains(tok));
        assertTrue(tile.contains(new TokenEntry("a", 2, "foo", 5)));
    }

    public void testAdd() {
        TokenEntry tok = new TokenEntry("a", 2, "foo", 5);
        Tile tile = new Tile(tok);
        tile.add(new TokenEntry("l", 8, "bar", 5));
        assertEquals(2, tile.getTokenCount());
    }
}
