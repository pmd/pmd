/*
 * User: tom
 * Date: Jul 31, 2002
 * Time: 12:20:23 PM
 */
package test.net.sourceforge.pmd.cpd;

import junit.framework.TestCase;
import net.sourceforge.pmd.cpd.Occurrence;
import net.sourceforge.pmd.cpd.Token;

public class OccurrenceTest extends TestCase {
    public OccurrenceTest(String name) {
        super(name);
    }

    public void testBasic() {
        Occurrence occ = new Occurrence(new Token('h',0,"foo"));
        assertEquals("foo", occ.getTokenSetID());
        assertEquals(0, occ.getIndex());
    }
}
