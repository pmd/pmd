/*
 * User: tom
 * Date: Jul 31, 2002
 * Time: 10:24:58 AM
 */
package test.net.sourceforge.pmd.cpd;

import junit.framework.TestCase;
import net.sourceforge.pmd.cpd.TokenList;
import net.sourceforge.pmd.cpd.Token;
import net.sourceforge.pmd.cpd.Tile;
import net.sourceforge.pmd.cpd.Occurrence;

public class TokenListTest extends TestCase{
    public TokenListTest(String name) {
        super(name);
    }

    public void testBasic() {
        TokenList ts = new TokenList("foo");
        assertEquals("foo", ts.getID());
    }

    public void testAdd() {
        Token tok = new Token('l', 9, "foo");
        TokenList ts = new TokenList("foo");
        ts.add(tok);
        assertEquals(tok, ts.get(0));
        assertTrue(ts.iterator().hasNext());
    }

    public void testHasTokenAfter() {
        assertTrue(GSTTest.createHelloTokenSet("foo").hasTokenAfter(new Tile(new Token('h', 0, "foo")), new Occurrence("foo", new Token('h', 0, "foo"))));
    }


}
