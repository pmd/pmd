/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Test;

public class MatchTest {

    @Test
    public void testSimple() {
        TokenEntry mark1 = new TokenEntry("public", "/var/Foo.java", 1);
        TokenEntry mark2 = new TokenEntry("class", "/var/Foo.java", 1);
        Match match = new Match(1, mark1, mark2);
        match.setSourceCodeSlice("public class Foo {}");
        assertEquals("public class Foo {}", match.getSourceCodeSlice());
        match.setLineCount(10);
        assertEquals(10, match.getLineCount());
        assertEquals(1, match.getTokenCount());
        Iterator i = match.iterator();
        assertEquals(mark1, i.next());
        assertEquals(mark2, i.next());
        assertFalse(i.hasNext());
    }

    @Test
    public void testCompareTo() {
        Match m1 = new Match(1, new TokenEntry("public", "/var/Foo.java", 1), new TokenEntry("class", "/var/Foo.java", 1));
        Match m2 = new Match(2, new TokenEntry("Foo", "/var/Foo.java", 1), new TokenEntry("{", "/var/Foo.java", 1));
        assertTrue(m2.compareTo(m1) < 0);
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(MatchTest.class);
    }
}
