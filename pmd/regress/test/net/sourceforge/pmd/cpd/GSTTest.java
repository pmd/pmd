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
            Token tok = (Token)occs.next();
            if (tok.getTokenSrcID().equals("foo")) {
                assertEquals(0, tok.getIndex());
            } else {
                assertEquals("bar", tok.getTokenSrcID());
            }
        }

    }

    public static TokenList createHelloTokenSet(String id) {
        TokenList ts = new TokenList(id);
        ts.add(new Token('h', 0, id));
        ts.add(new Token('e', 1, id));
        ts.add(new Token('l', 2, id));
        ts.add(new Token('l', 3, id));
        ts.add(new Token('o', 4, id));
        return ts;
    }

}
