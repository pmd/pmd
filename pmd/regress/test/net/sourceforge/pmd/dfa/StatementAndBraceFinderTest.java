package test.net.sourceforge.pmd.dfa;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTStatementExpression;
import net.sourceforge.pmd.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.ast.ASTExpression;
import net.sourceforge.pmd.dfa.DataFlowNode;
import net.sourceforge.pmd.dfa.IDataFlowNode;
import net.sourceforge.pmd.dfa.NodeType;
import test.net.sourceforge.pmd.testframework.ParserTst;

import java.util.List;

public class StatementAndBraceFinderTest extends ParserTst {

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

    public void testIfStmtHasCorrectTypes() throws Throwable {
        List nodes = getOrderedNodes(ASTExpression.class, TEST3);
        ASTExpression exp = (ASTExpression)nodes.get(0);
        IDataFlowNode dfn = (IDataFlowNode)exp.getDataFlowNode().getFlow().get(2);
        assertTrue(dfn.isType(NodeType.IF_EXPR));
        assertTrue(dfn.isType(NodeType.IF_LAST_STATEMENT_WITHOUT_ELSE));
    }

    public void testWhileStmtHasCorrectTypes() throws Throwable {
        List nodes = getOrderedNodes(ASTExpression.class, TEST4);
        ASTExpression exp = (ASTExpression)nodes.get(0);
        IDataFlowNode dfn = (IDataFlowNode)exp.getDataFlowNode().getFlow().get(2);
        assertTrue(dfn.isType(NodeType.WHILE_EXPR));
        assertTrue(dfn.isType(NodeType.WHILE_LAST_STATEMENT));
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
        "  if (x) {}" + PMD.EOL +
        " }" + PMD.EOL +
        "}";

    private static final String TEST4 =
        "class Foo {" + PMD.EOL +
        " void bar() {" + PMD.EOL +
        "  while (x) {}" + PMD.EOL +
        " }" + PMD.EOL +
        "}";
}
