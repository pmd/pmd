/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TokenEntryTest  {

    @Test
    public void testSimple() {
        TokenEntry.clearImages();
        TokenEntry mark = new TokenEntry("public", "/var/Foo.java", 1);
        assertEquals(1, mark.getBeginLine());
        assertEquals("/var/Foo.java", mark.getTokenSrcID());
        assertEquals(0, mark.getIndex());
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(TokenEntryTest.class);
    }
}
