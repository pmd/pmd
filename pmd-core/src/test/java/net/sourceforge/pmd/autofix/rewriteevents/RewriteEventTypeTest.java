/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.autofix.rewriteevents;

import static org.junit.Assert.fail;

import org.junit.Test;

public class RewriteEventTypeTest {

    @Test
    public void testValidIndexes() {
        final RewriteEventType[] rewriteEventTypes = new RewriteEventType[RewriteEventType.values().length];
        for (final RewriteEventType rewriteEventType : RewriteEventType.values()) {
            final int index = rewriteEventType.getIndex();
            // If IndexOutOfBoundException, index is equal or greater than RewriteEventType.values().length,
            //  which is an invalid condition for this enum specification
            if (rewriteEventTypes[index] != null) {
                fail(); // There was another rewrite event with the same index
            }
            rewriteEventTypes[index] = rewriteEventType;
        }
    }
}
