/*
 * User: tom
 * Date: Sep 5, 2002
 * Time: 12:06:26 PM
 */
package test.net.sourceforge.pmd.dcpd;

import junit.framework.TestCase;
import net.sourceforge.pmd.cpd.TokenSets;
import net.sourceforge.pmd.cpd.Occurrences;
import net.sourceforge.pmd.cpd.Tile;
import net.sourceforge.pmd.dcpd.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

public class TileHarvesterTest extends TestCase {

    public TileHarvesterTest(String name) {
        super(name);
    }

    public void test1() throws Throwable {
        TokenSets tokenSets = TileExpanderTest.createTokenSets();
        MockJavaSpace space = new MockJavaSpace();
        Job job = new Job("foo", new Integer(1));
        Occurrences occ = new Occurrences(tokenSets);

        // do the expansion from 1 to 2 tokens and write those expansions
        // back to the mock space so TileHarvester can read them
        int tilesSoFar=0;
        List tilesToWrite = new ArrayList();
        for (Iterator i = occ.getTiles(); i.hasNext();) {
            Tile tile = (Tile)i.next();
            TileWrapper tw = new TileWrapper(tile,
                    TileExpanderTest.marshal(occ.getOccurrences(tile)),
                    job.id,
                    TileWrapper.NOT_DONE,
                    null,
                    new Integer(tilesSoFar),
                    null, null);
            tilesToWrite.add(tw);
            tilesSoFar++;
        }
        space.setTileWrappers(tilesToWrite);
        TileExpander expander = new TileExpander(space, new TokenSetsWrapper(tokenSets, job.id));
        expander.expandAvailableTiles();
        space.setTileWrappers(space.getWrittenEntries());

        // now the test
        TileHarvester tileGatherer = new TileHarvester(space, job);
        Occurrences newOcc = tileGatherer.harvest(occ.size());
        assertEquals(2, newOcc.size());
    }
}
