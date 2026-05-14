/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone.junit5testnoprivatemodifier;

import org.junit.jupiter.api.Test;

public abstract class ParentWithTest {
    @Test
    void testRegular() {
        // nothing
    }
}
