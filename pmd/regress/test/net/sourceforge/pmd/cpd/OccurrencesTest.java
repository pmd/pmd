/*
 * User: tom
 * Date: Jul 31, 2002
 * Time: 12:25:01 PM
 */
package test.net.sourceforge.pmd.cpd;

import junit.framework.TestCase;
import net.sourceforge.pmd.cpd.*;

import java.util.Iterator;

public class OccurrencesTest  extends TestCase {
    public OccurrencesTest(String name) {
        super(name);
    }

    public void testBasic() {
        Occurrences occs = new Occurrences();
        assertTrue(!occs.contains(new Token('h', 0, "foo")));
        assertTrue(!occs.getTiles().hasNext());
        assertTrue(occs.isEmpty());
        assertEquals(0, occs.size());
    }

    public void testInitialFrequencyCount() {
        Occurrences occs = new Occurrences();
        TokenSet ts = new TokenSet("foo");
        ts.add(new Token('h', 0, "foo"));
        ts.add(new Token('e', 1, "foo"));
        ts.add(new Token('l', 2, "foo"));
        ts.add(new Token('l', 3, "foo"));
        ts.add(new Token('o', 4, "foo"));
        occs.addInitial(ts);
        assertEquals(4, occs.size());
        assertTrue(occs.contains(new Token('h', 0, "foo")));
        Iterator i = occs.getOccurrences(new Tile(new Token('h', 0, "foo")));
        assertTrue(i.hasNext());
        Occurrence occ = (Occurrence)i.next();
        assertEquals("foo", occ.getTokenSetID());
        assertEquals(0,occ.getIndex());
    }
}
