package test.net.sourceforge.pmd.cpd;

import junit.framework.TestCase;
import net.sourceforge.pmd.cpd.TokenEntry;

public class TokenEntryTest extends TestCase {

    public void testSimple() {
        TokenEntry.clearImages();
        TokenEntry mark = new TokenEntry("public", "/var/Foo.java", 1);
        assertEquals(1, mark.getBeginLine());
        assertEquals("/var/Foo.java", mark.getTokenSrcID());
        assertEquals(0, mark.getIndex());
    }
}
