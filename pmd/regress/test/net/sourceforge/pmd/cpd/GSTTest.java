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

    public void test1() {
        TokenList ts1 = GSTTest.createHelloTokenSet("foo");
        TokenList ts2 = GSTTest.createHelloTokenSet("bar");
        TokenSets tss = new TokenSets();
        tss.add(ts1);
        tss.add(ts2);
        GST gst = new GST(tss, 5);
        Results results = gst.crunch();
        assertEquals(1, results.size());
        Tile tile = (Tile)results.getTiles().next();
        assertEquals("hello", tile.getImage());
        Iterator occs = results.getOccurrences(tile);
        assertTrue(occs.hasNext());
        while (occs.hasNext()) {
            TokenEntry tok = (TokenEntry)occs.next();
            if (tok.getTokenSrcID().equals("foo")) {
                assertEquals(0, tok.getIndex());
            } else {
                assertEquals("bar", tok.getTokenSrcID());
            }
        }

    }

    public static TokenList createHelloTokenSet(String id) {
        TokenList ts = new TokenList(id);
        ts.add(new TokenEntry("h", 0, id, 5));
        ts.add(new TokenEntry("e", 1, id, 5));
        ts.add(new TokenEntry("l", 2, id, 5));
        ts.add(new TokenEntry("l", 3, id, 5));
        ts.add(new TokenEntry("o", 4, id, 5));
        return ts;
    }

}
