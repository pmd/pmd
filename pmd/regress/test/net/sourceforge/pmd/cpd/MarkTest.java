package test.net.sourceforge.pmd.cpd;

import net.sourceforge.pmd.cpd.Mark;
import net.sourceforge.pmd.cpd.Locator;
import net.sourceforge.pmd.cpd.CPDNullListener;
import junit.framework.TestCase;

import java.util.ArrayList;

public class MarkTest extends TestCase {

    public void testSimple() {
        Locator loc = new Locator("/var/foo.java", 10);
        Mark mark = new Mark(new ArrayList(), loc);
        assertEquals(mark.getLocator(), loc);
    }
}
