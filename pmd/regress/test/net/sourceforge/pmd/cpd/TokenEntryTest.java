/*
 * User: tom
 * Date: Jul 31, 2002
 * Time: 10:00:36 AM
 */
package test.net.sourceforge.pmd.cpd;

import junit.framework.TestCase;
import net.sourceforge.pmd.cpd.TokenEntry;

public class TokenEntryTest extends TestCase {

    public void testBasic() {
        TokenEntry t = new TokenEntry("a", 2, "foo", 5);
        assertEquals("a", t.getImage());
        assertEquals(2, t.getIndex());
        assertEquals("foo", t.getTokenSrcID());
    }
}
