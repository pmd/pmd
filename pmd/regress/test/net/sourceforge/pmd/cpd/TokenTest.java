/*
 * User: tom
 * Date: Jul 31, 2002
 * Time: 10:00:36 AM
 */
package test.net.sourceforge.pmd.cpd;

import junit.framework.TestCase;
import net.sourceforge.pmd.cpd.Token;

public class TokenTest extends TestCase {
    public TokenTest(String name) {
        super(name);
    }

    public void testBasic() {
        Token t = new Token('a', 2, "foo");
        assertEquals("a", t.getImage());
        assertEquals(2, t.getIndex());
        assertEquals("foo", t.getTokenSrcID());
    }
}
