package test.net.sourceforge.pmd.ant;

import net.sourceforge.pmd.ant.PathChecker;
import junit.framework.TestCase;

public class PathCheckerTest extends TestCase {

    public void testPathCheckerRelativeWin() {
        PathChecker pc = new PathChecker("Windows XP");
        assertTrue(!pc.isAbsolute("foo\\bar.html"));
    }

    public void testPathCheckerAbsoluteWin() {
        PathChecker pc = new PathChecker("Windows XP");
        assertTrue(pc.isAbsolute("c:\\foo\\bar.html"));
    }

    public void testPathCheckerRelativeNix() {
        PathChecker pc = new PathChecker("Linux");
        assertTrue(!pc.isAbsolute("foo/bar.html"));
    }

    public void testPathCheckerAbsoluteNix() {
        PathChecker pc = new PathChecker("Linux");
        assertTrue(pc.isAbsolute("/var/www/html/report.html"));
    }

}
