package test.net.sourceforge.pmd.cpd;

import junit.framework.TestCase;
import net.sourceforge.pmd.cpd.CPDListener;
import net.sourceforge.pmd.cpd.CPDNullListener;
import net.sourceforge.pmd.cpd.Mark;
import net.sourceforge.pmd.cpd.MarkComparator;
import net.sourceforge.pmd.cpd.TokenEntry;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MarkComparatorTest extends TestCase {

    private class MyListener implements CPDListener {
        public void addedFile(int fileCount, File file) {
        }
        public void comparisonCountUpdate(long comparisons) {
            gotCallback = true;
        }
    }

    private boolean gotCallback;

    public void testEqualMarksAreEqual() {
        List code = getCode();
        MarkComparator comp = new MarkComparator(new CPDNullListener(), code);
        Mark mark1 = new Mark(code.size(), "/var/Foo.java", 0, 1);
        Mark mark6 = new Mark(code.size(), "/var/Foo.java", 5, 1);
        assertEquals(0, comp.compare(mark1, mark6));
    }

    public void testSameMarkIsEqual() {
        List code = getCode();
        MarkComparator comp = new MarkComparator(new CPDNullListener(), code);
        Mark mark1 = new Mark(code.size(), "/var/Foo.java", 0, 1);
        assertEquals(0, comp.compare(mark1, mark1));
    }
    public void testUnuequalMarksAreUnequal() {
        List code = getCode();
        MarkComparator comp = new MarkComparator(new CPDNullListener(), code);
        Mark mark1 = new Mark(0, "/var/Foo.java", 0, 1);
        Mark mark5 = new Mark(4, "/var/Foo.java", 4, 1);
        assertFalse(0 == comp.compare(mark1, mark5));
    }

    public void testcomparisonCountCallback() {
        gotCallback = false;
        List code = getCode();
        MarkComparator comp = new MarkComparator(new MyListener(), code, 3);
        Mark mark1 = new Mark(code.size(), "/var/Foo.java", 0, 1);
        Mark mark2 = new Mark(code.size(), "/var/Foo.java", 1, 1);
        comp.compare(mark1, mark2);
        comp.compare(mark1, mark2);
        comp.compare(mark1, mark2);
        assertTrue(gotCallback);
    }

    public void test1() {}

    private List getCode() {
        List code = new ArrayList();
        TokenEntry te1 = new TokenEntry("System", 0, "/var/Foo.java", 1);
        TokenEntry te2 = new TokenEntry(".", 1, "/var/Foo.java", 1);
        TokenEntry te3 = new TokenEntry("out", 2, "/var/Foo.java", 1);
        TokenEntry te4 = new TokenEntry(".", 3, "/var/Foo.java", 1);
        TokenEntry te5 = new TokenEntry("println", 4, "/var/Foo.java", 1);
        TokenEntry te6 = new TokenEntry("System", 5, "/var/Foo.java", 1);
        code.add(te1);
        code.add(te2);
        code.add(te3);
        code.add(te4);
        code.add(te5);
        code.add(te6);
        return code;
    }
}
