/*
 * User: tom
 * Date: Jul 31, 2002
 * Time: 2:06:36 PM
 */
package test.net.sourceforge.pmd.cpd;

import junit.framework.TestCase;
import net.sourceforge.pmd.cpd.*;

import java.util.Iterator;

public class GSTTest extends TestCase {
    public GSTTest(String name) {
        super(name);
    }

    public void test1() {
        TokenSet ts1 = GSTTest.createHelloTokenSet("foo");
        TokenSet ts2 = GSTTest.createHelloTokenSet("bar");
        TokenSets tss = new TokenSets();
        tss.add(ts1);
        tss.add(ts2);
        Occurrences occ = new Occurrences();
        occ.addInitial(tss);
        GST gst = new GST(tss, occ, 5);
        gst.crunch();
        occ = gst.getResults();
        assertEquals(1, occ.size());
        Iterator tiles = occ.getTiles();
        assertTrue(tiles.hasNext());
        Tile tile = (Tile)tiles.next();
        assertEquals("hello", tile.getImage());
        Iterator occs = occ.getOccurrences(tile);
        assertTrue(occs.hasNext());
        while (occs.hasNext()) {
            Occurrence oc = (Occurrence)occs.next();
            if (oc.getTokenSetID().equals("foo")) {
                assertEquals(0, oc.getIndex());
            } else {
                assertEquals("bar", oc.getTokenSetID());
            }
        }

    }

    public static TokenSet createHelloTokenSet(String id) {
        TokenSet ts = new TokenSet(id);
        ts.add(new Token('h', 0, id));
        ts.add(new Token('e', 1, id));
        ts.add(new Token('l', 2, id));
        ts.add(new Token('l', 3, id));
        ts.add(new Token('o', 4, id));
        return ts;
    }

}
