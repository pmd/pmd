package test.net.sourceforge.pmd.dfa;

import junit.framework.TestCase;
import net.sourceforge.pmd.dfa.StatementAndBraceFinder;
import net.sourceforge.pmd.dfa.Structure;
import net.sourceforge.pmd.dfa.NodeType;
import net.sourceforge.pmd.dfa.DataFlowNode;
import net.sourceforge.pmd.dfa.IDataFlowNode;
import net.sourceforge.pmd.ast.ASTStatementExpression;
import net.sourceforge.pmd.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.ASTIfStatement;
import net.sourceforge.pmd.PMD;
import test.net.sourceforge.pmd.testframework.ParserTst;

import java.util.List;

public class StatementAndBraceFinderTest extends ParserTst {

/*
    public void testStatementExpressionParentChildLinks() throws Throwable {
        List nodes = getOrderedNodes(ASTStatementExpression.class, TEST1);
        ASTStatementExpression se = (ASTStatementExpression)nodes.get(0);
        ASTMethodDeclaration seParent = (ASTMethodDeclaration)((DataFlowNode)se.getDataFlowNode().getParents().get(0)).getSimpleNode();
        assertEquals(se, ((IDataFlowNode)seParent.getDataFlowNode().getChildren().get(0)).getSimpleNode());
        assertEquals(seParent, ((IDataFlowNode)se.getDataFlowNode().getParents().get(0)).getSimpleNode());
    }

    public void testVariableDeclaratorParentChildLinks() throws Throwable {
        List nodes = getOrderedNodes(ASTVariableDeclarator.class, TEST2);
        ASTVariableDeclarator vd = (ASTVariableDeclarator)nodes.get(0);
        ASTMethodDeclaration vdParent = (ASTMethodDeclaration)((DataFlowNode)vd.getDataFlowNode().getParents().get(0)).getSimpleNode();
        assertEquals(vd, ((IDataFlowNode)vdParent.getDataFlowNode().getChildren().get(0)).getSimpleNode());
        assertEquals(vdParent, ((IDataFlowNode)vd.getDataFlowNode().getParents().get(0)).getSimpleNode());
    }
*/

    public void testIfStmtGoesOnStack() throws Throwable {
        List nodes = getOrderedNodes(ASTIfStatement.class, TEST3);
        ASTIfStatement ifstmt = (ASTIfStatement)nodes.get(0);
        IDataFlowNode dfn = (IDataFlowNode)ifstmt.getDataFlowNode().getFlow().get(0);
        System.out.println(dfn);
        dfn = (IDataFlowNode)ifstmt.getDataFlowNode().getFlow().get(1);
        System.out.println(dfn);
        dfn = (IDataFlowNode)ifstmt.getDataFlowNode().getFlow().get(2);
        System.out.println(dfn);
        System.out.println(dfn.getChildren().get(0));
        System.out.println(dfn.getChildren().get(1));

        System.out.println(dfn.getChildren().get(0));
    }

    private static final String TEST1 =
        "class Foo {" + PMD.EOL +
        " void bar() {" + PMD.EOL +
        "  x = 2;" + PMD.EOL +
        " }" + PMD.EOL +
        "}";

    private static final String TEST2 =
        "class Foo {" + PMD.EOL +
        " void bar() {" + PMD.EOL +
        "  int x;" + PMD.EOL +
        " }" + PMD.EOL +
        "}";

    private static final String TEST3 =
        "class Foo {" + PMD.EOL +
        " void bar() {" + PMD.EOL +
        "  if (x>2) {" + PMD.EOL +
        "   bar();" + PMD.EOL +
        "  }" + PMD.EOL +
        " }" + PMD.EOL +
        "}";



}
