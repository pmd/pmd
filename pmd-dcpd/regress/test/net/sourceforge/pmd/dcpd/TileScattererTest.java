/*
 * User: tom
 * Date: Sep 6, 2002
 * Time: 4:15:46 PM
 */
package test.net.sourceforge.pmd.dcpd;

import junit.framework.TestCase;
import net.sourceforge.pmd.cpd.TokenSets;
import net.sourceforge.pmd.cpd.Occurrences;
import net.sourceforge.pmd.dcpd.Job;
import net.sourceforge.pmd.dcpd.TileScatterer;

public class TileScattererTest extends TestCase {

    public TileScattererTest(String name) {
        super(name);
    }

    public void test1() throws Throwable {
        TokenSets tokenSets = TileExpanderTest.createTokenSets();
        MockJavaSpace space = new MockJavaSpace();
        Job job = new Job("foo", new Integer(1));
        Occurrences occ = new Occurrences(tokenSets);

        TileScatterer scatterer = new TileScatterer(space, job);
        scatterer.scatter(occ);

        assertEquals(6, space.getWrittenEntries().size());

    }
}
