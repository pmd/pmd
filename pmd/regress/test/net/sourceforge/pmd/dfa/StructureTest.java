package test.net.sourceforge.pmd.dfa;

import junit.framework.TestCase;
import net.sourceforge.pmd.dfa.Structure;
import net.sourceforge.pmd.dfa.StackObject;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.ASTReturnStatement;

public class StructureTest extends TestCase {

    public void testAddResultsinDFANodeContainingAddedNode() {
        Structure s = new Structure();
        SimpleNode n = new ASTMethodDeclaration(1);
        assertEquals(n, s.addNewNode(n).getSimpleNode());
    }
}
