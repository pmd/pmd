/*
 * User: tom
 * Date: Jul 31, 2002
 * Time: 10:36:22 AM
 */
package test.net.sourceforge.pmd.cpd;

import junit.framework.TestCase;
import net.sourceforge.pmd.cpd.TokenSets;
import net.sourceforge.pmd.cpd.TokenList;
import net.sourceforge.pmd.cpd.Occurrence;
import net.sourceforge.pmd.cpd.Token;

public class TokenSetsTest extends TestCase {
    public TokenSetsTest(String name) {
        super(name);
    }

    public void testBasic() {
        Token tok = new Token('h', 0, "foo");
        TokenSets tss = new TokenSets();
        TokenList ts = new TokenList("foo");
        ts.add(tok);
        tss.add(ts);
        assertEquals(ts, tss.getTokenSet(new Occurrence(tok)));
        assertTrue(tss.iterator().hasNext());
    }
}
