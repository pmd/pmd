/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.ast;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.ast.ASTBlock;
import net.sourceforge.pmd.ast.ASTBlockStatement;
import net.sourceforge.pmd.ast.ASTEqualityExpression;
import net.sourceforge.pmd.ast.ASTExpression;
import net.sourceforge.pmd.ast.ASTInstanceOfExpression;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTRelationalExpression;
import net.sourceforge.pmd.ast.ASTReturnStatement;
import net.sourceforge.pmd.ast.ASTUnmodifiedClassDeclaration;
import net.sourceforge.pmd.ast.ASTVariableInitializer;
import net.sourceforge.pmd.ast.SimpleNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class SimpleNodeTest extends ParserTst {

    public void testMethodDiffLines() throws Throwable {
        Set methods = getNodes(ASTMethodDeclaration.class, METHOD_DIFF_LINES);
        Iterator iter = methods.iterator();
        verifyNode((SimpleNode) iter.next(), 2, 2, 4, 2);
    }

    public void testMethodSameLine() throws Throwable {
        Set methods = getNodes(ASTMethodDeclaration.class, METHOD_SAME_LINE);
        verifyNode((SimpleNode) methods.iterator().next(), 2, 2, 2, 21);
    }

    public void testNoLookahead() throws Throwable {
        String code = NO_LOOKAHEAD; // 1, 8 -> 1, 20
        Set uCD = getNodes(ASTUnmodifiedClassDeclaration.class, code);
        verifyNode((SimpleNode) uCD.iterator().next(), 1, 8, 1, 20);
    }

    public void testHasExplicitExtends() throws Throwable {
        String code = HAS_EXPLICIT_EXTENDS;
        ASTUnmodifiedClassDeclaration ucd = (ASTUnmodifiedClassDeclaration)(getNodes(ASTUnmodifiedClassDeclaration.class, code).iterator().next());
        assertTrue(ucd.hasExplicitExtends());
    }

    public void testNoExplicitExtends() throws Throwable {
        String code = NO_EXPLICIT_EXTENDS;
        ASTUnmodifiedClassDeclaration ucd = (ASTUnmodifiedClassDeclaration)(getNodes(ASTUnmodifiedClassDeclaration.class, code).iterator().next());
        assertTrue(!ucd.hasExplicitExtends());
    }

    public void testHasExplicitImplements() throws Throwable {
        String code = HAS_EXPLICIT_IMPLEMENTS;
        ASTUnmodifiedClassDeclaration ucd = (ASTUnmodifiedClassDeclaration)(getNodes(ASTUnmodifiedClassDeclaration.class, code).iterator().next());
        assertTrue(ucd.hasExplicitImplements());
    }

    public void testNoExplicitImplements() throws Throwable {
        String code = NO_EXPLICIT_IMPLEMENTS;
        ASTUnmodifiedClassDeclaration ucd = (ASTUnmodifiedClassDeclaration)(getNodes(ASTUnmodifiedClassDeclaration.class, code).iterator().next());
        assertTrue(!ucd.hasExplicitImplements());
    }

    public void testColumnsOnQualifiedName() throws Throwable {
        Set name = getNodes(ASTName.class, QUALIFIED_NAME);
        Iterator i = name.iterator();
        while (i.hasNext()) {
            SimpleNode node = (SimpleNode) i.next();
            if (node.getImage().equals("java.io.File")) {
                verifyNode(node, 1, 8, 1, 19);
            }
        }
    }

    public void testLineNumbersForNameSplitOverTwoLines() throws Throwable {
        Set name = getNodes(ASTName.class, BROKEN_LINE_IN_NAME);
        Iterator i = name.iterator();
        while (i.hasNext()) {
            SimpleNode node = (SimpleNode) i.next();
            if (node.getImage().equals("java.io.File")) {
                verifyNode(node, 1, 8, 2, 4);
            }
            if (node.getImage().equals("Foo")) {
                verifyNode(node, 2, 15, 2, 18);
            }
        }
    }

    public void testLineNumbersAreSetOnAllSiblings() throws Throwable {
        Set blocks = getNodes(ASTBlock.class, LINE_NUMBERS_ON_SIBLINGS);
        Iterator i = blocks.iterator();
        while (i.hasNext()) {
            ASTBlock b = (ASTBlock)i.next();
            assertTrue(b.getBeginLine() > 0);
        }
        blocks = getNodes(ASTVariableInitializer.class, LINE_NUMBERS_ON_SIBLINGS);
        i = blocks.iterator();
        while (i.hasNext()) {
            ASTVariableInitializer b = (ASTVariableInitializer)i.next();
            assertTrue(b.getBeginLine() > 0);
        }
        blocks = getNodes(ASTExpression.class, LINE_NUMBERS_ON_SIBLINGS);
        i = blocks.iterator();
        while (i.hasNext()) {
            ASTExpression b = (ASTExpression)i.next();
            assertTrue(b.getBeginLine() > 0);
        }
    }

    public void testFindChildrenOfType() {
        ASTBlock block = new ASTBlock(2);
        block.jjtAddChild(new ASTReturnStatement(1), 0);
        assertEquals(1, block.findChildrenOfType(ASTReturnStatement.class).size());
    }

    public void testFindChildrenOfTypeMultiple() {
        ASTBlock block = new ASTBlock(1);
        block.jjtAddChild(new ASTBlockStatement(2), 0);
        block.jjtAddChild(new ASTBlockStatement(3), 1);
        List nodes = new ArrayList();
        block.findChildrenOfType(ASTBlockStatement.class, nodes);
        assertEquals(2, nodes.size());
    }

    public void testFindChildrenOfTypeRecurse() {
        ASTBlock block = new ASTBlock(1);
        ASTBlock childBlock = new ASTBlock(2);
        block.jjtAddChild(childBlock, 0);
        childBlock.jjtAddChild(new ASTMethodDeclaration(3), 0);
        List nodes = new ArrayList();
        block.findChildrenOfType(ASTMethodDeclaration.class, nodes);
        assertEquals(1, nodes.size());
    }

    public void testReplaceChild() {
        ASTEqualityExpression ee = new ASTEqualityExpression(1);
        ASTInstanceOfExpression io1 = new ASTInstanceOfExpression(2);
        ASTRelationalExpression re = new ASTRelationalExpression(3);
        ASTInstanceOfExpression io2 = new ASTInstanceOfExpression(2);
        ee.jjtAddChild(io1, 0);
        ee.jjtAddChild(io2, 1);
        io1.jjtAddChild(re, 0);
        ee.jjtReplaceChild(io1, re);
        assertEquals(ee.jjtGetChild(0), re);
        assertEquals(ee.jjtGetChild(1), io2);
    }



    private void verifyNode(SimpleNode node, int beginLine, int beginCol, int endLine, int endCol) {
        assertEquals("Wrong beginning line: ", beginLine, node.getBeginLine());
        assertEquals("Wrong beginning column: ", beginCol, node.getBeginColumn());
        assertEquals("Wrong ending line:", endLine, node.getEndLine());
        assertEquals("Wrong ending column:", endCol, node.getEndColumn());
    }

    private static final String HAS_EXPLICIT_EXTENDS =
    "public class Test extends Foo {}";

    private static final String NO_EXPLICIT_EXTENDS =
    "public class Test {}";

    private static final String HAS_EXPLICIT_IMPLEMENTS =
    "public class Test implements Foo {}";

    private static final String NO_EXPLICIT_IMPLEMENTS =
    "public class Test {}";

    private static final String METHOD_DIFF_LINES =
    "public class Test {" + PMD.EOL +
    " public void foo() {" + PMD.EOL +
    "  int x;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String METHOD_SAME_LINE =
    "public class Test {" + PMD.EOL +
    " public void foo() {}" + PMD.EOL +
    "}";

    private static final String QUALIFIED_NAME =
    "import java.io.File;" + PMD.EOL +
    "public class Foo{}";

    private static final String BROKEN_LINE_IN_NAME =
    "import java.io." + PMD.EOL +
    "File;" + PMD.EOL +
    "public class Foo{}";

    private static final String LINE_NUMBERS_ON_SIBLINGS =
    "public class Foo {" + PMD.EOL +
    " void bar() {" + PMD.EOL +
    "  try {" + PMD.EOL +
    "  } catch (Exception1 e) {" + PMD.EOL +
    "   int x =2;" + PMD.EOL +
    "  }" + PMD.EOL +
    " if (x != null) {}" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String NO_LOOKAHEAD = "public class Foo { }";
}
