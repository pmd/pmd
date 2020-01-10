/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.dfa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.ast.AbstractNode;
import net.sourceforge.pmd.lang.dfa.DataFlowNode;
import net.sourceforge.pmd.lang.dfa.NodeType;
import net.sourceforge.pmd.lang.dfa.StartOrEndDataFlowNode;
import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;
import net.sourceforge.pmd.lang.plsql.PlsqlParsingHelper;
import net.sourceforge.pmd.lang.plsql.ast.ASTExpression;
import net.sourceforge.pmd.lang.plsql.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.plsql.ast.ASTProgramUnit;
import net.sourceforge.pmd.lang.plsql.ast.ASTVariableOrConstantDeclarator;
import net.sourceforge.pmd.lang.plsql.ast.PLSQLNode;

public class StatementAndBraceFinderTest extends AbstractPLSQLParserTst {

    private final PlsqlParsingHelper plsql = PlsqlParsingHelper.WITH_PROCESSING.withResourceContext(StatementAndBraceFinderTest.class);

    private ASTExpression getExpr(String test1) {
        return plsql.parse(test1).getFirstDescendantOfType(ASTExpression.class);
    }

    /**
     * Java ASTStatementExpression equivalent is inferred as an Expression()
     * which has an UnlabelledStatement as a parent.
     */
    @Test
    public void testExpressionParentChildLinks() {
        ASTExpression ex = getExpr(TEST1);
        DataFlowNode dfn = ex.getDataFlowNode();
        assertEquals(3, dfn.getLine());
        assertTrue(dfn.getNode() instanceof ASTExpression);
        List<DataFlowNode> dfns = dfn.getParents();
        assertEquals(1, dfns.size());
        DataFlowNode parentDfn = dfns.get(0);
        assertEquals(2, parentDfn.getLine());
        assertTrue(parentDfn.getNode() instanceof ASTProgramUnit);
        ASTProgramUnit exParent = (ASTProgramUnit) parentDfn.getNode();
        // Validate the two-way link between Program Unit and Statement
        assertEquals(ex, exParent.getDataFlowNode().getChildren().get(0).getNode());
        assertEquals(exParent, ex.getDataFlowNode().getParents().get(0).getNode());
    }


    @Test
    public void testVariableOrConstantDeclaratorParentChildLinks() {
        ASTVariableOrConstantDeclarator vd = plsql.getNodes(ASTVariableOrConstantDeclarator.class, TEST2).get(0);
        // ASTMethodDeclaration vdParent = (ASTMethodDeclaration)
        // ((DataFlowNode) vd.getDataFlowNode().getParents().get(0)).getNode();
        ASTProgramUnit vdParent = (ASTProgramUnit) vd.getDataFlowNode().getParents().get(0).getNode();
        // Validate the two-way link between Program Unit and Variable
        assertEquals(vd, vdParent.getDataFlowNode().getChildren().get(0).getNode());
        assertEquals(vdParent, vd.getDataFlowNode().getParents().get(0).getNode());
    }

    @Test
    public void testIfStmtHasCorrectTypes() {
        ASTExpression exp = getExpr(TEST3);
        assertEquals(5, exp.getDataFlowNode().getFlow().size());
        DataFlowNode dfn = exp.getDataFlowNode().getFlow().get(2);
        assertTrue(dfn.isType(NodeType.IF_EXPR));
        assertEquals(3, dfn.getLine());
        dfn = exp.getDataFlowNode().getFlow().get(3);
        assertTrue(dfn.isType(NodeType.IF_LAST_STATEMENT_WITHOUT_ELSE));
        assertEquals(3, dfn.getLine());
    }

    @Test
    public void testWhileStmtHasCorrectTypes() {
        ASTExpression exp = getExpr(TEST4);
        DataFlowNode dfn = exp.getDataFlowNode().getFlow().get(2);
        assertTrue(dfn.isType(NodeType.WHILE_EXPR));
        dfn = exp.getDataFlowNode().getFlow().get(3);
        assertTrue(dfn.isType(NodeType.WHILE_LAST_STATEMENT));
    }

    @Test
    public void testForStmtHasCorrectTypes() {
        ASTExpression exp = getExpr(TEST5);
        DataFlowNode dfn = null;
        dfn = exp.getDataFlowNode().getFlow().get(0);
        assertTrue(dfn instanceof StartOrEndDataFlowNode);
        dfn = exp.getDataFlowNode().getFlow().get(1);
        assertTrue(dfn.getNode() instanceof ASTProgramUnit);
        assertEquals(2, dfn.getLine());
        dfn = exp.getDataFlowNode().getFlow().get(2);
        assertEquals(3, dfn.getLine());
        assertTrue(dfn.isType(NodeType.FOR_EXPR));
        assertTrue(dfn.isType(NodeType.FOR_BEFORE_FIRST_STATEMENT));
        dfn = exp.getDataFlowNode().getFlow().get(3);
        assertEquals(3, dfn.getLine());
        assertTrue(dfn.isType(NodeType.FOR_END));
    }

    @Test
    public void testSimpleCaseStmtHasCorrectTypes() {
        ASTExpression exp = getExpr(TEST6);
        DataFlowNode dfn = null;
        dfn = exp.getDataFlowNode().getFlow().get(0);
        assertTrue(dfn instanceof StartOrEndDataFlowNode);
        dfn = exp.getDataFlowNode().getFlow().get(1);
        assertEquals(2, dfn.getLine());
        assertTrue(dfn.getNode() instanceof ASTProgramUnit);
        dfn = exp.getDataFlowNode().getFlow().get(2);
        assertEquals(4, dfn.getLine());
        assertTrue(dfn.isType(NodeType.SWITCH_START));
        assertTrue(dfn.isType(NodeType.CASE_LAST_STATEMENT));
        dfn = exp.getDataFlowNode().getFlow().get(3);
        assertEquals(5, dfn.getLine());
        assertTrue(dfn.isType(NodeType.CASE_LAST_STATEMENT));
        assertTrue(dfn.isType(NodeType.BREAK_STATEMENT));
        dfn = exp.getDataFlowNode().getFlow().get(4);
        assertEquals(6, dfn.getLine());
        assertTrue(dfn.isType(NodeType.SWITCH_LAST_DEFAULT_STATEMENT));
        assertTrue(dfn.isType(NodeType.BREAK_STATEMENT));
        dfn = exp.getDataFlowNode().getFlow().get(5);
        assertEquals(7, dfn.getLine());
        assertTrue(dfn.isType(NodeType.SWITCH_END));
    }

    /*
     * @Test public void testSearchedCaseStmtHasCorrectTypes()
     * { List<ASTStatement> statements = getOrderedNodes(ASTStatement.class,
     * TEST7); List<ASTExpression> expressions =
     * getOrderedNodes(ASTExpression.class, TEST7);
     *
     * ASTStatement st = statements.get(0); ASTStatement st1 =
     * statements.get(1); ASTStatement st2 = statements.get(2); ASTStatement st3
     * = statements.get(3);
     * System.err.println("testSearchedCaseStmtHasCorrectTypes-st(0)="+st.
     * getBeginLine());
     *
     * ASTExpression ex = expressions.get(0); ASTExpression ex1 =
     * expressions.get(1); ASTExpression ex2 = expressions.get(2); ASTExpression
     * ex3 = expressions.get(3); ASTExpression ex4 = expressions.get(4);
     * System.err.println("ASTExpression="+ex );
     *
     * DataFlowNode dfn = null; //dfn = ex.getDataFlowNode().getFlow().get(0);
     * //dfn = st.getDataFlowNode().getFlow().get(0); dfn = (DataFlowNode)
     * st.getDataFlowNode(); System.err.println("DataFlowNode(st-0)="+dfn ) ;
     * System.err.println("DataFlowNode(st-1)="+st1.getDataFlowNode() ) ;
     * System.err.println("DataFlowNode(st-2)="+st2.getDataFlowNode() ) ;
     * System.err.println("DataFlowNode(st-3)="+st3.getDataFlowNode() ) ;
     *
     * System.err.println("DataFlowNode(ex-0)="+ex.getDataFlowNode() ) ;
     * System.err.println("DataFlowNode(ex-1)="+ex1.getDataFlowNode() ) ;
     * System.err.println("DataFlowNode(ex-2)="+ex2.getDataFlowNode() ) ;
     * System.err.println("DataFlowNode(ex-3)="+ex3.getDataFlowNode() ) ;
     * System.err.println("DataFlowNode(ex-4)="+ex4.getDataFlowNode() ) ;
     * List<DataFlowNode> dfns = dfn.getFlow();
     * System.err.println("DataFlowNodes List size="+dfns.size()) ; DataFlowNode
     * firstDfn = dfns.get(0); System.err.println("firstDataFlowNode="+firstDfn
     * ) ;
     * System.err.println("testSearchedCaseStmtHasCorrectTypes-dfn(0)="+dfn);
     * dfn = st.getDataFlowNode().getFlow().get(1);
     * System.err.println("testSearchedCaseStmtHasCorrectTypes-dfn(1)="+dfn);
     * dfn = st.getDataFlowNode().getFlow().get(2);
     * System.err.println("testSearchedCaseStmtHasCorrectTypes-dfn(2)="+dfn);
     * assertTrue(dfn.isType(NodeType.SWITCH_START)); dfn =
     * st.getDataFlowNode().getFlow().get(3);
     * System.err.println("testSearchedCaseStmtHasCorrectTypes-dfn(3)="+dfn);
     * assertTrue(dfn.isType(NodeType.CASE_LAST_STATEMENT)); //dfn =
     * st.getDataFlowNode().getFlow().get(4);
     * System.err.println("testSearchedCaseStmtHasCorrectTypes-dfn(5)="+dfn);
     * assertTrue(dfn.isType(NodeType.CASE_LAST_STATEMENT)); dfn =
     * st.getDataFlowNode().getFlow().get(5);
     * System.err.println("testSearchedCaseStmtHasCorrectTypes-dfn(7)="+dfn);
     * assertTrue(dfn.isType(NodeType.SWITCH_LAST_DEFAULT_STATEMENT)); dfn =
     * st.getDataFlowNode().getFlow().get(6);
     * System.err.println("testSearchedCaseStmtHasCorrectTypes-dfn(8)="+dfn);
     * assertTrue(dfn.isType(NodeType.SWITCH_END)); }
     */
    @Test
    public void testLabelledStmtHasCorrectTypes() {
        ASTExpression exp = getExpr(TEST8);
        DataFlowNode dfn = exp.getDataFlowNode().getFlow().get(2);
        assertEquals(3, dfn.getLine());
        assertTrue(dfn.isType(NodeType.LABEL_STATEMENT));
    }

    @Test
    public void testOnlyWorksForMethodsAndConstructors() {
        StatementAndBraceFinder sbf = new StatementAndBraceFinder(plsql.getDefaultHandler().getDataFlowHandler());
        PLSQLNode node = new ASTMethodDeclaration(1);
        ((AbstractNode) node).testingOnlySetBeginColumn(1);
        sbf.buildDataFlowFor(node);
        // sbf.buildDataFlowFor(new ASTConstructorDeclaration(1));
        node = new ASTProgramUnit(1);
        ((AbstractNode) node).testingOnlySetBeginColumn(1);
        sbf.buildDataFlowFor(node);
    }

    private static final String TEST1 = "CREATE OR REPLACE PACKAGE BODY Foo AS" + PMD.EOL + " PROCEDURE bar IS BEGIN"
            + PMD.EOL + "  x := 2;" + PMD.EOL + " END bar;" + PMD.EOL + "END foo;";

    private static final String TEST2 = "CREATE OR REPLACE PACKAGE BODY Foo AS" + PMD.EOL + " PROCEDURE bar IS "
            + PMD.EOL + "  int x; " + PMD.EOL + "  BEGIN NULL ;" + PMD.EOL + " END bar;" + PMD.EOL + "END foo;";

    private static final String TEST3 = "CREATE OR REPLACE PACKAGE BODY Foo AS" + PMD.EOL + " PROCEDURE bar IS BEGIN"
            + PMD.EOL + "  if (x) THEN NULL; END IF; " + PMD.EOL + " END bar;" + PMD.EOL + "END foo;";

    private static final String TEST4 = "CREATE OR REPLACE PACKAGE BODY Foo AS" + PMD.EOL + " PROCEDURE bar IS BEGIN"
            + PMD.EOL + "  while (x) LOOP NULL; END LOOP;" + PMD.EOL + " END bar;" + PMD.EOL + "END foo;";

    private static final String TEST5 = "CREATE OR REPLACE PACKAGE BODY Foo AS" + PMD.EOL + " PROCEDURE bar IS BEGIN"
            + PMD.EOL + "  for i in 0..9 LOOP NULL; END LOOP;" + PMD.EOL + " END bar;" + PMD.EOL + "END foo;";

    private static final String TEST6 = "CREATE OR REPLACE PACKAGE BODY Foo AS" + PMD.EOL + " PROCEDURE bar IS "
            + PMD.EOL + " BEGIN" + PMD.EOL + " CASE 1 " + PMD.EOL + " WHEN 0 THEN NULL; " + PMD.EOL
            + " WHEN 1 THEN NULL; " + PMD.EOL + " ELSE NULL;" + PMD.EOL + " END CASE; " + PMD.EOL + " END bar; "
            + PMD.EOL + "END foo;";

    private static final String TEST7 = "CREATE OR REPLACE PACKAGE BODY Foo AS" + PMD.EOL + " PROCEDURE bar IS "
            + PMD.EOL + " BEGIN" + PMD.EOL + " CASE " + PMD.EOL + " WHEN 0=1 THEN NULL; " + PMD.EOL
            + " WHEN 1=1 THEN NULL; " + PMD.EOL + " ELSE NULL;" + PMD.EOL + " END CASE;" + PMD.EOL + " END bar;"
            + PMD.EOL + "END foo;";

    private static final String TEST8 = "CREATE OR REPLACE PACKAGE BODY Foo AS" + PMD.EOL + " PROCEDURE bar IS BEGIN"
            + PMD.EOL + " <<label>> NULL;" + PMD.EOL + " END bar;" + PMD.EOL + "END foo;";
}
