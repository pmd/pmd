package net.sourceforge.pmd.lang.plsql.dfa;

import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.dfa.DataFlowNode;
import net.sourceforge.pmd.lang.dfa.NodeType;
import net.sourceforge.pmd.lang.plsql.ast.ASTProgramUnit;
//import net.sourceforge.pmd.lang.plsql.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.plsql.ast.ASTExpression;
import net.sourceforge.pmd.lang.plsql.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.plsql.ast.ASTStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTVariableOrConstantDeclarator;
import net.sourceforge.pmd.lang.plsql.ast.SimpleNode;
import net.sourceforge.pmd.testframework.plsql.ParserTst;

import org.junit.Test;


public class StatementAndBraceFinderTest extends ParserTst {

    /**Java ASTStetamentExpressionequivalent is inferred as an Expression() which has
     * an UnlabelledStatement as a parent.
     * 
     * @throws Throwable 
     */
    @Test
    public void testExpressionParentChildLinks() throws Throwable {
        ASTExpression ex = getOrderedNodes(ASTExpression.class, TEST1).get(0);
        System.err.println("ASTExpression="+ex );
        DataFlowNode dfn = (DataFlowNode) ex.getDataFlowNode();
        System.err.println("DataFlowNode="+dfn ) ;
        List<DataFlowNode> dfns = dfn.getParents();
        System.err.println("DataFlowNodes List size="+dfns.size()) ;
        DataFlowNode parentDfn =  dfns.get(0);
        System.err.println("parentDataFlowNode="+parentDfn ) ;
        SimpleNode simpleNode = (SimpleNode) parentDfn.getNode();
        System.err.println("parentDataFlowNode="+parentDfn ) ;
        ASTProgramUnit exParent = (ASTProgramUnit) parentDfn.getNode();
        System.err.println("ASTProgramUnit="+exParent ); 
       //Validate the two-way link betwen Program Unit and Statement 
        assertEquals(ex, ((DataFlowNode) exParent.getDataFlowNode().getChildren().get(0)).getNode());
        assertEquals(exParent, ((DataFlowNode) ex.getDataFlowNode().getParents().get(0)).getNode());
    }

    @Test
    public void testVariableOrConstantDeclaratorParentChildLinks() throws Throwable {
        ASTVariableOrConstantDeclarator vd = getOrderedNodes(ASTVariableOrConstantDeclarator.class, TEST2).get(0);
        //ASTMethodDeclaration vdParent = (ASTMethodDeclaration) ((DataFlowNode) vd.getDataFlowNode().getParents().get(0)).getNode();
        ASTProgramUnit vdParent = (ASTProgramUnit) ((DataFlowNode) vd.getDataFlowNode().getParents().get(0)).getNode();
        //Validate the two-way link betwen Program Unit and Variable 
        assertEquals(vd, ((DataFlowNode) vdParent.getDataFlowNode().getChildren().get(0)).getNode());
        assertEquals(vdParent, ((DataFlowNode) vd.getDataFlowNode().getParents().get(0)).getNode());
    }

    @Test
    public void testIfStmtHasCorrectTypes() throws Throwable {
        ASTExpression exp = getOrderedNodes(ASTExpression.class, TEST3).get(0);
        DataFlowNode dfn = exp.getDataFlowNode().getFlow().get(2);
        System.err.println("testIfStmtHasCorrectTypes-dfn(2)="+dfn);
        assertTrue(dfn.isType(NodeType.IF_EXPR));
        dfn = exp.getDataFlowNode().getFlow().get(3);
        System.err.println("testIfStmtHasCorrectTypes-dfn(3)="+dfn);
        assertTrue(dfn.isType(NodeType.IF_LAST_STATEMENT_WITHOUT_ELSE));
    }

    @Test
    public void testWhileStmtHasCorrectTypes() throws Throwable {
        ASTExpression exp = getOrderedNodes(ASTExpression.class, TEST4).get(0);
        DataFlowNode dfn = exp.getDataFlowNode().getFlow().get(2);
        System.err.println("testWhileStmtHasCorrectTypes-dfn(2)="+dfn);
        assertTrue(dfn.isType(NodeType.WHILE_EXPR));
        dfn = exp.getDataFlowNode().getFlow().get(3);
        System.err.println("testWhileStmtHasCorrectTypes-dfn(3)="+dfn);
        assertTrue(dfn.isType(NodeType.WHILE_LAST_STATEMENT));
    }

    @Test
    public void testForStmtHasCorrectTypes() throws Throwable {
        ASTExpression exp = getOrderedNodes(ASTExpression.class, TEST5).get(0);
        DataFlowNode dfn = null;
        dfn = exp.getDataFlowNode().getFlow().get(0);
        System.err.println("testForStmtHasCorrectTypes-dfn(0)="+dfn);
        dfn = exp.getDataFlowNode().getFlow().get(1);
        System.err.println("testForStmtHasCorrectTypes-dfn(1)="+dfn);
        dfn = exp.getDataFlowNode().getFlow().get(2);
        System.err.println("testForStmtHasCorrectTypes-dfn(2)="+dfn);
        assertTrue(dfn.isType(NodeType.FOR_EXPR));
        assertTrue(dfn.isType(NodeType.FOR_BEFORE_FIRST_STATEMENT));
        dfn = exp.getDataFlowNode().getFlow().get(3);
        System.err.println("testForStmtHasCorrectTypes-dfn(3)="+dfn);
        assertTrue(dfn.isType(NodeType.FOR_END));
    }

    @Test(expected = RuntimeException.class)
    public void testOnlyWorksForMethodsAndConstructors() {
        StatementAndBraceFinder sbf = new StatementAndBraceFinder(Language.PLSQL.getDefaultVersion().getLanguageVersionHandler().getDataFlowHandler());
        sbf.buildDataFlowFor(new ASTMethodDeclaration(1));
        //sbf.buildDataFlowFor(new ASTConstructorDeclaration(1));
        sbf.buildDataFlowFor(new ASTProgramUnit(1));
    }

    private static final String TEST1 =
            "CREATE OR REPLACE PACKAGE BODY Foo AS" + PMD.EOL +
            " PROCEDURE bar IS BEGIN" + PMD.EOL +
            "  x := 2;" + PMD.EOL +
            " END bar;" + PMD.EOL +
            "END foo;";

    private static final String TEST2 =
            "CREATE OR REPLACE PACKAGE BODY Foo AS" + PMD.EOL +
            " PROCEDURE bar IS " + PMD.EOL +
            "  int x; " + PMD.EOL +
	    "  BEGIN NULL ;" + PMD.EOL +
            " END bar;" + PMD.EOL +
            "END foo;";

    private static final String TEST3 =
           "CREATE OR REPLACE PACKAGE BODY Foo AS" + PMD.EOL +
            " PROCEDURE bar IS BEGIN" + PMD.EOL +
            "  if (x) THEN NULL; END IF; " + PMD.EOL +
            " END bar;" + PMD.EOL +
            "END foo;";

    private static final String TEST4 =
            "CREATE OR REPLACE PACKAGE BODY Foo AS" + PMD.EOL +
            " PROCEDURE bar IS BEGIN" + PMD.EOL +
            "  while (x) LOOP NULL; END LOOP;" + PMD.EOL +
            " END bar;" + PMD.EOL +
            "END foo;";

    private static final String TEST5 =
            "CREATE OR REPLACE PACKAGE BODY Foo AS" + PMD.EOL +
            " PROCEDURE bar IS BEGIN" + PMD.EOL +
            "  for i in 0..9 LOOP NULL; END LOOP;" + PMD.EOL +
            " END bar;" + PMD.EOL +
            "END foo;";

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(StatementAndBraceFinderTest.class);
    }
}
