package test.net.sourceforge.pmd.dfa;

import junit.framework.TestCase;
import net.sourceforge.pmd.dfa.StatementAndBraceFinder;
import net.sourceforge.pmd.dfa.Structure;
import net.sourceforge.pmd.ast.ASTStatementExpression;
import net.sourceforge.pmd.ast.ASTVariableDeclarator;

/**
 * Created by IntelliJ IDEA.
 * User: tom
 * Date: Sep 27, 2004
 * Time: 6:05:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class StatementAndBraceFinderTest extends TestCase {

    public void testNodesAreAdded() {
        StatementAndBraceFinder s = new StatementAndBraceFinder();
        ASTStatementExpression se = new ASTStatementExpression(1);
        Structure str = new Structure();
        s.visit(se, str);
        assertEquals(se, str.getFirst().getSimpleNode());

        ASTVariableDeclarator vd = new ASTVariableDeclarator(2);
        s.visit(vd, str);
        assertEquals(vd, str.getLast().getSimpleNode());
    }
}
