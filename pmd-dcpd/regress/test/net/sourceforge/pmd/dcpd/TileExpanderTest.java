/*
 * User: tom
 * Date: Sep 5, 2002
 * Time: 11:04:26 AM
 */
package test.net.sourceforge.pmd.dcpd;

import junit.framework.TestCase;
import net.jini.space.JavaSpace;
import net.jini.core.lease.Lease;
import net.sourceforge.pmd.dcpd.*;
import net.sourceforge.pmd.cpd.*;

import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Stack;

public class TileExpanderTest extends TestCase {

    public TileExpanderTest(String name) {
        super(name);
    }

    public void test1() throws Throwable {
/*
        TokenSets tokenSets = TileExpanderTest.createTokenSets();
        MockJavaSpace space = new MockJavaSpace();
        Job job = new Job("foo", new Integer(1));
        Occurrences occ = new Occurrences(tokenSets);

        int tilesSoFar=0;
        List tilesToWrite = new ArrayList();
        for (Iterator i = occ.getTiles(); i.hasNext();) {
            Tile tile = (Tile)i.next();
            TileWrapper tw = new TileWrapper(tile,
                    marshal(occ.getOccurrences(tile)),
                    job.id,
                    Batch.NOT_DONE,
                    null,
                    new Integer(tilesSoFar),
                    null, null);
            tilesToWrite.add(tw);
            tilesSoFar++;
        }

        space.setTileWrappers(tilesToWrite);

        TileExpander expander = new TileExpander(space, new TokenSetsWrapper(tokenSets, job.id));
        expander.expandAvailableTiles();

        List writtenEntries = space.getWrittenEntries();

        assertEquals(7, writtenEntries.size());
*/
    }

   public static List marshal(Iterator i) {
        List list = new ArrayList();
        while (i.hasNext()) {
            list.add(i.next());
        }
        return list;
    }

    public static TokenSets createTokenSets() throws Throwable {
        TokenSets tokenSets = new TokenSets();
        TokenList tokenList1 = new TokenList("list1");
        JavaTokensTokenizer tokenizer = new JavaTokensTokenizer();
        tokenizer.tokenize(tokenList1, new StringReader("public class Foo {}"));
        tokenSets.add(tokenList1);
        TokenList tokenList2 = new TokenList("list2");
        tokenizer.tokenize(tokenList2, new StringReader("public class Bar {}"));
        tokenSets.add(tokenList2);
        return tokenSets;
    }

}
