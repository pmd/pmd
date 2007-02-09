package test.net.sourceforge.pmd.dfa;

import static org.junit.Assert.assertEquals;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.dfa.Structure;

import org.junit.Test;

public class StructureTest {

    @Test
    public void testAddResultsinDFANodeContainingAddedNode() {
        Structure s = new Structure();
        SimpleNode n = new ASTMethodDeclaration(1);
        assertEquals(n, s.createNewNode(n).getSimpleNode());
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(StructureTest.class);
    }
}
