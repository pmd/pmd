package test.net.sourceforge.pmd.cpd;

import junit.framework.TestCase;
import net.sourceforge.pmd.cpd.Mark;

import java.util.ArrayList;

public class MarkTest extends TestCase {

    public void testSimple() {
        Mark mark = new Mark(new ArrayList(), 0, "/var/Foo.java", 10);
        assertEquals(mark.getIndexIntoFile(), 10);
    }
}
