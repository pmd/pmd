/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TokenMgrErrorTest {
    @Test
    void invalidLocation() {
        TokenMgrError error = new TokenMgrError(2, 0, null, "test", null);
        // this shouldn't throw a IllegalArgumentException
        assertEquals("line 2, column 1", error.location().startPosToString());
    }

    @Test
    void invalidLocationJavaCC() {
        TokenMgrError error = new TokenMgrError(false, "DEFAULT", 2, 0, "}", '\n');
        // this shouldn't throw a IllegalArgumentException
        assertEquals("line 2, column 1", error.location().startPosToString());
    }

    @Test
    void validLocation() {
        TokenMgrError error = new TokenMgrError(1, 1, null, "test", null);
        assertEquals("line 1, column 1", error.location().startPosToString());
    }

    @Test
    void validLocationJavaCC() {
        TokenMgrError error = new TokenMgrError(false, "DEFAULT", 1, 1, "}", '\n');
        assertEquals("line 1, column 1", error.location().startPosToString());
    }
}
