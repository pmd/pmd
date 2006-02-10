package test.net.sourceforge.pmd.dfa;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.ast.ASTExpression;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTStatementExpression;
import net.sourceforge.pmd.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.dfa.DataFlowNode;
import net.sourceforge.pmd.dfa.IDataFlowNode;
import net.sourceforge.pmd.dfa.NodeType;
import net.sourceforge.pmd.dfa.StatementAndBraceFinder;
import test.net.sourceforge.pmd.testframework.ParserTst;

public class StatementAndBraceFinderTest extends ParserTst {

    public void testStatementExpressionParentChildLinks() throws Throwable {
        ASTStatementExpression se = (ASTStatementExpression) getOrderedNodes(ASTStatementExpression.class, TEST1).get(0);
        ASTMethodDeclaration seParent = (ASTMethodDeclaration) ((DataFlowNode) se.getDataFlowNode().getParents().get(0)).getSimpleNode();
        assertEquals(se, ((IDataFlowNode) seParent.getDataFlowNode().getChildren().get(0)).getSimpleNode());
        assertEquals(seParent, ((IDataFlowNode) se.getDataFlowNode().getParents().get(0)).getSimpleNode());
    }

    public void testVariableDeclaratorParentChildLinks() throws Throwable {
        ASTVariableDeclarator vd = (ASTVariableDeclarator) getOrderedNodes(ASTVariableDeclarator.class, TEST2).get(0);
        ASTMethodDeclaration vdParent = (ASTMethodDeclaration) ((DataFlowNode) vd.getDataFlowNode().getParents().get(0)).getSimpleNode();
        assertEquals(vd, ((IDataFlowNode) vdParent.getDataFlowNode().getChildren().get(0)).getSimpleNode());
        assertEquals(vdParent, ((IDataFlowNode) vd.getDataFlowNode().getParents().get(0)).getSimpleNode());
    }

    public void testIfStmtHasCorrectTypes() throws Throwable {
        ASTExpression exp = (ASTExpression) getOrderedNodes(ASTExpression.class, TEST3).get(0);
        IDataFlowNode dfn = (IDataFlowNode) exp.getDataFlowNode().getFlow().get(2);
        assertTrue(dfn.isType(NodeType.IF_EXPR));
        assertTrue(dfn.isType(NodeType.IF_LAST_STATEMENT_WITHOUT_ELSE));
    }

    public void testWhileStmtHasCorrectTypes() throws Throwable {
        ASTExpression exp = (ASTExpression) getOrderedNodes(ASTExpression.class, TEST4).get(0);
        IDataFlowNode dfn = (IDataFlowNode) exp.getDataFlowNode().getFlow().get(2);
        assertTrue(dfn.isType(NodeType.WHILE_EXPR));
        assertTrue(dfn.isType(NodeType.WHILE_LAST_STATEMENT));
    }

    public void testForStmtHasCorrectTypes() throws Throwable {
        ASTExpression exp = (ASTExpression) getOrderedNodes(ASTExpression.class, TEST5).get(0);
        IDataFlowNode dfn = (IDataFlowNode) exp.getDataFlowNode().getFlow().get(2);
        assertTrue(dfn.isType(NodeType.FOR_INIT));
        dfn = (IDataFlowNode) exp.getDataFlowNode().getFlow().get(3);
        assertTrue(dfn.isType(NodeType.FOR_EXPR));
        dfn = (IDataFlowNode) exp.getDataFlowNode().getFlow().get(4);
        assertTrue(dfn.isType(NodeType.FOR_UPDATE));
        assertTrue(dfn.isType(NodeType.FOR_BEFORE_FIRST_STATEMENT));
        assertTrue(dfn.isType(NodeType.FOR_END));
    }

    public void testOnlyWorksForMethodsAndConstructors() {
        StatementAndBraceFinder sbf = new StatementAndBraceFinder();
        try {
            sbf.buildDataFlowFor(new ASTCompilationUnit(1));
            fail("Should have failed!");
        } catch (RuntimeException e) {
            // cool
        }
        sbf.buildDataFlowFor(new ASTMethodDeclaration(1));
        sbf.buildDataFlowFor(new ASTConstructorDeclaration(1));
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

    private static final String TEST5 =
            "class Foo {" + PMD.EOL +
            " void bar() {" + PMD.EOL +
            "  for (int i=0; i<10; i++) {}" + PMD.EOL +
            " }" + PMD.EOL +
            "}";
}
