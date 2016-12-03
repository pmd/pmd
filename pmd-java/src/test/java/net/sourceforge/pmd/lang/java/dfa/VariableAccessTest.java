/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.dfa;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sourceforge.pmd.lang.dfa.VariableAccess;

public class VariableAccessTest {

    @Test
    public void testGetVariableName() {
        VariableAccess va = new VariableAccess(VariableAccess.DEFINITION, "foo.bar");
        assertEquals("foo", va.getVariableName());
        va = new VariableAccess(VariableAccess.DEFINITION, ".foobar");
        assertEquals("", va.getVariableName());
        va = new VariableAccess(VariableAccess.DEFINITION, "foobar.");
        assertEquals("foobar", va.getVariableName());
        va = new VariableAccess(VariableAccess.DEFINITION, "foobar");
        assertEquals("foobar", va.getVariableName());
    }
}
