package test.net.sourceforge.pmd.dfa;

import junit.framework.TestCase;
import net.sourceforge.pmd.dfa.Structure;
import net.sourceforge.pmd.dfa.StackObject;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.ASTReturnStatement;

/**
 * Created by IntelliJ IDEA.
 * User: tom
 * Date: Sep 27, 2004
 * Time: 5:56:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class StructureTest extends TestCase {

    public void testAddNewNode() {
        Structure s = new Structure();
        SimpleNode n = new ASTMethodDeclaration(1);
        s.addNewNode(n);
        assertEquals(n, s.getFirst().getSimpleNode());
        assertEquals(n, s.getLast().getSimpleNode());
    }

/*
    public void testAddReturn() {
        Structure s = new Structure();
        SimpleNode n = new ASTReturnStatement(1);
        s.addNewNode(n);
        assertEquals(n, ((StackObject)s.getCBRStack().get(0)).getDataFlowNode().getSimpleNode());
        //assertEquals(n, s.getLast().getSimpleNode());
    }
*/
}
