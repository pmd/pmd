/*
 * User: tom
 * Date: Aug 5, 2002
 * Time: 2:02:17 PM
 */
package test.net.sourceforge.pmd.cpd;

import junit.framework.TestCase;
import net.sourceforge.pmd.cpd.Results;
import net.sourceforge.pmd.cpd.Tile;
import net.sourceforge.pmd.cpd.TokenEntry;
import net.sourceforge.pmd.cpd.ResultsImpl;

import java.util.Iterator;

public class ResultsTest extends TestCase {

    public void testBasic() {
        Results r = new ResultsImpl();
        Tile tile = new Tile(CPDTest.getHelloTokens());
        TokenEntry startToken = new TokenEntry("h", 0, "1", 5);
        r.addTile(tile, startToken);
        Iterator i = r.getOccurrences(tile);
        assertTrue(i.hasNext());
        assertEquals(startToken, (TokenEntry)i.next());
    }
}
