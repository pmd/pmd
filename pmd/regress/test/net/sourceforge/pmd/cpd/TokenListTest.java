/*
 * User: tom
 * Date: Jul 31, 2002
 * Time: 10:24:58 AM
 */
package test.net.sourceforge.pmd.cpd;

import junit.framework.TestCase;
import net.sourceforge.pmd.cpd.Tile;
import net.sourceforge.pmd.cpd.TokenEntry;
import net.sourceforge.pmd.cpd.TokenList;

import java.util.ArrayList;
import java.util.List;

public class TokenListTest extends TestCase {

    /**
     * The end of line string for this machine.
     */
    protected String EOL = System.getProperty("line.separator", "\n");

    public void testBasic() {
        TokenList ts = new TokenList("foo");
        assertEquals("foo", ts.getID());
    }

    public void testAdd() {
        TokenEntry tok = new TokenEntry("L", 9, "foo", 5);
        TokenList ts = new TokenList("foo");
        ts.add(tok);
        assertEquals(tok, ts.get(0));
        assertTrue(ts.iterator().hasNext());
    }

    public void testCode() {
        TokenList tl = new TokenList("1");
        List list = new ArrayList();
        list.add("public class Foo {");
        list.add(" public void bar() {}");
        list.add(" public void baz() {}");
        list.add("}");
        tl.setCode(list);
        assertEquals(" public void bar() {}", tl.getSlice(1,1));
        assertEquals(" public void bar() {}" +EOL + " public void baz() {}", tl.getSlice(1,2));
    }

    public void testHasTokenAfter() {
        assertTrue(GSTTest.createHelloTokenSet("foo").hasTokenAfter(new Tile(new TokenEntry("H", 0, "foo", 5)), new TokenEntry("H", 0, "foo", 5)));
    }
}
