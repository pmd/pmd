/*
 * User: tom
 * Date: Jul 31, 2002
 * Time: 10:36:22 AM
 */
package test.net.sourceforge.pmd.cpd;

import junit.framework.TestCase;
import net.sourceforge.pmd.cpd.TokenSets;
import net.sourceforge.pmd.cpd.TokenList;
import net.sourceforge.pmd.cpd.TokenEntry;

public class TokenSetsTest extends TestCase {

    public void testBasic() {
        TokenEntry tok = new TokenEntry("H", 0, "foo", 5);
        TokenSets tss = new TokenSets();
        TokenList ts = new TokenList("foo");
        ts.add(tok);
        tss.add(ts);
        assertEquals(ts, tss.getTokenList(tok));
        assertTrue(tss.iterator().hasNext());
    }
}
