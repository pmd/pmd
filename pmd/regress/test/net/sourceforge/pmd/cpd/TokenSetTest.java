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
        TokenSet ts = new TokenSet("foo");
        Token tok = new Token('h', 0, "foo");
        ts.add(tok);
        Token tok1 = new Token('e', 1, "foo");
        ts.add(tok1);
        Token tok3 = new Token('l', 2, "foo");
        ts.add(tok3);
        Token tok4 = new Token('l', 3, "foo");
        ts.add(tok4);
        Token tok5 = new Token('o', 4, "foo");
        ts.add(tok5);
        assertTrue(ts.hasTokenAfter(new Tile(tok), new Occurrence("foo", tok)));
    }


}
