/*
 * User: tom
 * Date: Jul 31, 2002
 * Time: 10:24:58 AM
 */
package test.net.sourceforge.pmd.cpd;

import junit.framework.TestCase;
import net.sourceforge.pmd.cpd.TokenSet;
import net.sourceforge.pmd.cpd.Token;
import net.sourceforge.pmd.cpd.Tile;
import net.sourceforge.pmd.cpd.Occurrence;

public class TokenSetTest extends TestCase{
    public TokenSetTest(String name) {
        super(name);
    }

    public void testBasic() {
        TokenSet ts = new TokenSet("foo");
        assertEquals("foo", ts.getID());
    }

    public void testAdd() {
        Token tok = new Token('l', 9, "foo");
        TokenSet ts = new TokenSet("foo");
        ts.add(tok);
        assertEquals(tok, ts.get(0));
        assertTrue(ts.iterator().hasNext());
    }

    public void testHasTokenAfter() {
        assertTrue(GSTTest.createHelloTokenSet("foo").hasTokenAfter(new Tile(new Token('h', 0, "foo")), new Occurrence("foo", new Token('h', 0, "foo"))));
    }


}
