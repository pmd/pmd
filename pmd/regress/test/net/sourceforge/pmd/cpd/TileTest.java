/*
 * User: tom
 * Date: Jul 31, 2002
 * Time: 10:08:40 AM
 */
package test.net.sourceforge.pmd.cpd;

import junit.framework.TestCase;
import net.sourceforge.pmd.cpd.Tile;
import net.sourceforge.pmd.cpd.Token;

import java.util.ArrayList;
import java.util.List;

public class TileTest extends TestCase {
    public TileTest(String name) {
        super(name);
    }

    public void testConstructors() {
        Token tok = new Token('a', 2,"foo");
        Tile tile = new Tile(tok);
        assertEquals(1, tile.getTokenCount());

        List tokens = new ArrayList();
        tokens.add(tok);
        tile = new Tile(tokens);
        assertEquals(1, tile.getTokenCount());
    }

    public void testCopy() {
        Token tok = new Token('a', 2,"foo");
        Tile tile = new Tile(tok);
        Tile tileCopy = tile.copy();
        assertEquals(1, tile.getTokenCount());
        assertEquals(tok, tileCopy.getTokens().get(0));
    }

    public void testEquality() {
        Tile tile = new Tile(new Token('a', 2,"foo"));
        Tile tile2 = new Tile(new Token('a', 2,"foo"));
        assertEquals(tile, tile2);
        assertEquals(tile.hashCode(), tile2.hashCode());
    }

    public void testContains() {
        Token tok = new Token('a', 2,"foo");
        Tile tile = new Tile(tok);
        assertTrue(tile.contains(tok));
        assertTrue(tile.contains(new Token('a', 2,"foo")));
    }

    public void testAdd() {
        Token tok = new Token('a', 2,"foo");
        Tile tile = new Tile(tok);
        tile.add(new Token('l', 8, "bar"));
        assertEquals(2, tile.getTokenCount());
    }
}
