package test.net.sourceforge.pmd.dfa;

import junit.framework.TestCase;
import net.sourceforge.pmd.dfa.variableaccess.VariableAccess;

public class VariableAccessTest extends TestCase {

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
