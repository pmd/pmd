package test.net.sourceforge.pmd.cpd;

import junit.framework.TestCase;
import net.sourceforge.pmd.cpd.Match;
import net.sourceforge.pmd.cpd.Mark;

import java.util.Iterator;

public class MatchTest extends TestCase  {

    public void testSimple() {
        Mark mark1 = new Mark(1, "/var/Foo.java", 1, 1);
        Mark mark2 = new Mark(2, "/var/Foo.java", 2, 1);
        Match match = new Match(1, mark1, mark2);
        assertEquals(1, match.getTokenCount());
        Iterator i = match.iterator();
        assertEquals(mark1, i.next());
        assertEquals(mark2, i.next());
        assertFalse(i.hasNext());
    }
    public void testCompareTo() {
        Match m1 = new Match(1, new Mark(1, "/var/Foo.java", 1, 1), new Mark(2, "/var/Foo.java", 2, 1));
        Match m2 = new Match(2, new Mark(4, "/var/Foo.java", 4, 1), new Mark(5, "/var/Foo.java", 5, 1));
        assertTrue(m2.compareTo(m1) < 0);
    }
}
