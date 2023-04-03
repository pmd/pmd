/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TokenEntryTest {

    @Test
    void testSimple() {
        TokenEntry.clearImages();
        TokenEntry mark = new TokenEntry("public", "/var/Foo.java", 1);
        assertEquals(1, mark.getBeginLine());
        assertEquals("/var/Foo.java", mark.getTokenSrcID());
        assertEquals(0, mark.getIndex());
        assertEquals(-1, mark.getBeginColumn());
        assertEquals(-1, mark.getEndColumn());
    }

    @Test
    void testColumns() {
        TokenEntry.clearImages();
        TokenEntry mark = new TokenEntry("public", "/var/Foo.java", 1, 2, 3);
        assertEquals(1, mark.getBeginLine());
        assertEquals("/var/Foo.java", mark.getTokenSrcID());
        assertEquals(0, mark.getIndex());
        assertEquals(2, mark.getBeginColumn());
        assertEquals(3, mark.getEndColumn());
    }
}
