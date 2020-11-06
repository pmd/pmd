/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.jaxen.JaxenException;
import org.junit.Ignore;
import org.junit.Test;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;

public class SimpleNodeTest extends BaseParserTest {

    @Test
    public void testMethodDiffLines() {
        List<ASTMethodDeclaration> methods = java.getNodes(ASTMethodDeclaration.class, METHOD_DIFF_LINES);
        verifyNode(methods.iterator().next(), 2, 9, 4, 2);
    }

    @Test
    public void testMethodSameLine() {
        List<ASTMethodDeclaration> methods = java.getNodes(ASTMethodDeclaration.class, METHOD_SAME_LINE);
        verifyNode(methods.iterator().next(), 2, 9, 2, 21);
    }

    @Test
    public void testNoLookahead() {
        List<ASTClassOrInterfaceDeclaration> uCD = java.getNodes(ASTClassOrInterfaceDeclaration.class, NO_LOOKAHEAD);
        verifyNode(uCD.iterator().next(), 1, 8, 1, 20);
    }

    @Test
    public void testHasExplicitExtends() {
        ASTClassOrInterfaceDeclaration ucd = java.getNodes(ASTClassOrInterfaceDeclaration.class, HAS_EXPLICIT_EXTENDS).iterator().next();
        assertTrue(ucd.getChild(0) instanceof ASTExtendsList);
    }

    @Test
    public void testNoExplicitExtends() {
        ASTClassOrInterfaceDeclaration ucd = java.getNodes(ASTClassOrInterfaceDeclaration.class, NO_EXPLICIT_EXTENDS).iterator().next();
        assertFalse(ucd.getChild(0) instanceof ASTExtendsList);
    }

    @Test
    public void testHasExplicitImplements() {
        ASTClassOrInterfaceDeclaration ucd = java.getNodes(ASTClassOrInterfaceDeclaration.class, HAS_EXPLICIT_IMPLEMENTS).iterator().next();
        assertTrue(ucd.getChild(0) instanceof ASTImplementsList);
    }

    @Test
    public void testNoExplicitImplements() {
        ASTClassOrInterfaceDeclaration ucd = java.getNodes(ASTClassOrInterfaceDeclaration.class, NO_EXPLICIT_IMPLEMENTS).iterator().next();
        assertFalse(ucd.getChild(0) instanceof ASTImplementsList);
    }

    @Test
    public void testColumnsOnQualifiedName() {
        for (Node node : java.getNodes(ASTName.class, QUALIFIED_NAME)) {
            if (node.getImage().equals("java.io.File")) {
                verifyNode(node, 1, 8, 1, 19);
            }
        }
    }

    @Test
    public void testLineNumbersForNameSplitOverTwoLines() {
        for (Node node : java.getNodes(ASTName.class, BROKEN_LINE_IN_NAME)) {
            if (node.getImage().equals("java.io.File")) {
                verifyNode(node, 1, 8, 2, 4);
            }
            if (node.getImage().equals("Foo")) {
                verifyNode(node, 2, 15, 2, 18);
            }
        }
    }

    @Test
    public void testLineNumbersAreSetOnAllSiblings() {
        for (ASTBlock b : java.getNodes(ASTBlock.class, LINE_NUMBERS_ON_SIBLINGS)) {
            assertTrue(b.getBeginLine() > 0);
        }
        for (ASTVariableInitializer b : java.getNodes(ASTVariableInitializer.class, LINE_NUMBERS_ON_SIBLINGS)) {
            assertTrue(b.getBeginLine() > 0);
        }
        for (ASTExpression b : java.getNodes(ASTExpression.class, LINE_NUMBERS_ON_SIBLINGS)) {
            assertTrue(b.getBeginLine() > 0);
        }
    }

    @Test
    public void testFindDescendantsOfType() {
        ASTBlock block = new ASTBlock(2);
        block.jjtAddChild(new ASTReturnStatement(1), 0);
        assertEquals(1, block.findDescendantsOfType(ASTReturnStatement.class).size());
    }

    @Test
    public void testFindDescendantsOfTypeMultiple() {
        ASTBlock block = new ASTBlock(1);
        block.jjtAddChild(new ASTBlockStatement(2), 0);
        block.jjtAddChild(new ASTBlockStatement(3), 1);
        List<ASTBlockStatement> nodes = block.findDescendantsOfType(ASTBlockStatement.class);
        assertEquals(2, nodes.size());
    }

    @Test
    public void testFindDescendantsOfTypeRecurse() {
        ASTBlock block = new ASTBlock(1);
        ASTBlock childBlock = new ASTBlock(2);
        block.jjtAddChild(childBlock, 0);
        childBlock.jjtAddChild(new ASTMethodDeclaration(3), 0);
        List<ASTMethodDeclaration> nodes = block.findDescendantsOfType(ASTMethodDeclaration.class);
        assertEquals(1, nodes.size());
    }

    @Test
    public void testGetFirstChild() {
        ASTBlock block = new ASTBlock(1);
        ASTStatement x = new ASTStatement(2);
        block.jjtAddChild(x, 0);
        block.jjtAddChild(new ASTStatement(3), 1);

        Node n = block.getFirstDescendantOfType(ASTStatement.class);
        assertNotNull(n);
        assertTrue(n instanceof ASTStatement);
        assertEquals(x, n);
    }

    @Test
    public void testGetFirstChildNested() {
        ASTBlock block = new ASTBlock(1);
        ASTStatement x = new ASTStatement(2);
        ASTAssignmentOperator x1 = new ASTAssignmentOperator(4);
        x.jjtAddChild(x1, 0);
        block.jjtAddChild(x, 0);
        block.jjtAddChild(new ASTStatement(3), 1);

        Node n = block.getFirstDescendantOfType(ASTAssignmentOperator.class);
        assertNotNull(n);
        assertTrue(n instanceof ASTAssignmentOperator);
        assertEquals(x1, n);
    }

    @Test
    public void testGetFirstChildNestedDeeper() {
        ASTBlock block = new ASTBlock(1);
        ASTStatement x = new ASTStatement(2);
        ASTAssignmentOperator x1 = new ASTAssignmentOperator(4);
        ASTName x2 = new ASTName(5);

        x.jjtAddChild(x1, 0);
        x1.jjtAddChild(x2, 0);
        block.jjtAddChild(x, 0);
        block.jjtAddChild(new ASTStatement(3), 1);

        Node n = block.getFirstDescendantOfType(ASTName.class);
        assertNotNull(n);
        assertTrue(n instanceof ASTName);
        assertEquals(x2, n);
    }

    @Test
    public void testParentMethods() {
        ASTCompilationUnit u = JavaParsingHelper.JUST_PARSE.parse(TEST1);

        ASTMethodDeclarator d = u.getFirstDescendantOfType(ASTMethodDeclarator.class);
        assertSame("getFirstParentOfType ASTMethodDeclaration", d.getParent(),
                d.getFirstParentOfType(ASTMethodDeclaration.class));
        assertNull("getFirstParentOfType ASTName", d.getFirstParentOfType(ASTName.class));

        assertSame("getNthParent 1", d.getParent(), d.getNthParent(1));
        assertSame("getNthParent 2", d.getParent().getParent(), d.getNthParent(2));
        assertSame("getNthParent 6", u, d.getNthParent(6));
        assertNull("getNthParent 7", d.getNthParent(7));
        assertNull("getNthParent 8", d.getNthParent(8));
    }

    private static final String TEST1 = "public class Test {\n  void bar(String s) {\n   s = s.toLowerCase();\n  }\n}";

    @Ignore
    @Test
    public void testContainsNoInner() {
        ASTCompilationUnit c = java.getNodes(ASTCompilationUnit.class, CONTAINS_NO_INNER).iterator().next();
        List<ASTFieldDeclaration> res = c.findDescendantsOfType(ASTFieldDeclaration.class);
        assertTrue(res.isEmpty());
        /*
         * String expectedXml =
         * "<CompilationUnit BeginColumn=\"1\" BeginLine=\"5\" EndColumn=\"1\" EndLine=\"5\">"
         * +
         * "<TypeDeclaration BeginColumn=\"1\" BeginLine=\"1\" EndColumn=\"1\" EndLine=\"5\">"
         * +
         * "<ClassOrInterfaceDeclaration Abstract=\"false\" BeginColumn=\"8\" BeginLine=\"1\" EndColumn=\"1\" "
         * +
         * "EndLine=\"5\" Final=\"false\" Image=\"Test\" Interface=\"false\" Native=\"false\" Nested=\"false\" PackagePrivate=\"false\" Private=\"false\" Protected=\"false\" Public=\"true\" Static=\"false\" Strictfp=\"false\" Synchronized=\"false\" Transient=\"false\" Volatile=\"false\">"
         * +
         * "<ClassOrInterfaceBody BeginColumn=\"19\" BeginLine=\"1\" EndColumn=\"1\" EndLine=\"5\">"
         * +
         * "<ClassOrInterfaceBodyDeclaration AnonymousInnerClass=\"false\" BeginColumn=\"3\" BeginLine=\"2\" EndColumn=\"3\" EndLine=\"4\">"
         * +
         * "<ClassOrInterfaceDeclaration Abstract=\"false\" BeginColumn=\"10\" BeginLine=\"2\" EndColumn=\"3\" EndLine=\"4\" Final=\"false\" "
         * +
         * "Image=\"Inner\" Interface=\"false\" Native=\"false\" Nested=\"true\" PackagePrivate=\"false\" Private=\"false\" Protected=\"false\" "
         * +
         * "Public=\"true\" Static=\"false\" Strictfp=\"false\" Synchronized=\"false\" Transient=\"false\" Volatile=\"false\">"
         * +
         * "<ClassOrInterfaceBody BeginColumn=\"22\" BeginLine=\"2\" EndColumn=\"3\" EndLine=\"4\">"
         * +
         * "<ClassOrInterfaceBodyDeclaration AnonymousInnerClass=\"false\" BeginColumn=\"4\" BeginLine=\"3\" EndColumn=\"11\" EndLine=\"3\">"
         * +
         * "<FieldDeclaration Abstract=\"false\" Array=\"false\" ArrayDepth=\"0\" BeginColumn=\"4\" BeginLine=\"3\" EndColumn=\"11\" EndLine=\"3\" Final=\"false\" Native=\"false\" PackagePrivate=\"true\" Private=\"false\" Protected=\"false\" Public=\"false\" Static=\"false\" Strictfp=\"false\" Synchronized=\"false\" Transient=\"false\" VariableName=\"foo\" Volatile=\"false\"><Type Array=\"false\" ArrayDepth=\"0\" BeginColumn=\"4\" BeginLine=\"3\" EndColumn=\"6\" EndLine=\"3\">"
         * +
         * "<PrimitiveType Array=\"false\" ArrayDepth=\"0\" BeginColumn=\"4\" BeginLine=\"3\" Boolean=\"false\" EndColumn=\"6\" EndLine=\"3\" Image=\"int\"/>"
         * + "</Type>" +
         * "<VariableDeclarator BeginColumn=\"8\" BeginLine=\"3\" EndColumn=\"10\" EndLine=\"3\">"
         * +
         * "<VariableDeclaratorId Array=\"false\" ArrayDepth=\"0\" BeginColumn=\"8\" BeginLine=\"3\" EndColumn=\"10\" EndLine=\"3\" ExceptionBlockParameter=\"false\" Image=\"foo\"/>"
         * +
         * "</VariableDeclarator></FieldDeclaration></ClassOrInterfaceBodyDeclaration></ClassOrInterfaceBody>"
         * +
         * "</ClassOrInterfaceDeclaration></ClassOrInterfaceBodyDeclaration></ClassOrInterfaceBody></ClassOrInterfaceDeclaration>"
         * + "</TypeDeclaration></CompilationUnit>"; assertEquals( expectedXml,
         * getXmlString( c ) );
         */ }

    @Test
    public void testContainsNoInnerWithAnonInner() {
        ASTCompilationUnit c = java.parse(CONTAINS_NO_INNER_WITH_ANON_INNER);
        List<ASTFieldDeclaration> res = c.findDescendantsOfType(ASTFieldDeclaration.class);
        assertTrue(res.isEmpty());
    }

    @Test
    public void testContainsChildOfType() {
        ASTClassOrInterfaceDeclaration c = java.getNodes(ASTClassOrInterfaceDeclaration.class, CONTAINS_CHILDREN_OF_TYPE)
                .iterator().next();
        assertTrue(c.hasDescendantOfType(ASTFieldDeclaration.class));
    }

    @Test
    public void testXPathNodeSelect() throws JaxenException {
        ASTClassOrInterfaceDeclaration c = java.getNodes(ASTClassOrInterfaceDeclaration.class, TEST_XPATH).iterator().next();
        List<Node> nodes = c.findChildNodesWithXPath("//FieldDeclaration");
        assertEquals(2, nodes.size());
        assertTrue(nodes.get(0) instanceof ASTFieldDeclaration);

        assertTrue(c.hasDescendantMatchingXPath("//FieldDeclaration"));
        assertFalse(c.hasDescendantMatchingXPath("//MethodDeclaration"));
    }

    @Test
    public void testUserData() {
        ASTClassOrInterfaceDeclaration c = java.getNodes(ASTClassOrInterfaceDeclaration.class, HAS_EXPLICIT_EXTENDS)
                .iterator().next();
        assertNull(c.getUserData());
        c.setUserData("foo");
        assertEquals("foo", c.getUserData());
        c.setUserData(null);
        assertNull(c.getUserData());
    }

    private void verifyNode(Node node, int beginLine, int beginCol, int endLine, int endCol) {
        assertEquals("Unexpected beginning line: ", beginLine, node.getBeginLine());
        assertEquals("Unexpected beginning column: ", beginCol, node.getBeginColumn());
        assertEquals("Unexpected ending line:", endLine, node.getEndLine());
        assertEquals("Unexpected ending column:", endCol, node.getEndColumn());
    }

    private static final String HAS_EXPLICIT_EXTENDS = "public class Test extends Foo {}";

    private static final String NO_EXPLICIT_EXTENDS = "public class Test {}";

    private static final String HAS_EXPLICIT_IMPLEMENTS = "public class Test implements Foo {}";

    private static final String NO_EXPLICIT_IMPLEMENTS = "public class Test {}";

    private static final String METHOD_SAME_LINE = "public class Test {\n public void foo() {}\n}";

    private static final String QUALIFIED_NAME = "import java.io.File;\npublic class Foo{}";

    private static final String BROKEN_LINE_IN_NAME = "import java.io.\nFile;\npublic class Foo{}";

    private static final String LINE_NUMBERS_ON_SIBLINGS =
        "public class Foo {\n void bar() {\n  try {\n  } catch (Exception1 e) {\n   int x =2;\n  }\n if (x != null) {}\n }\n}";

    private static final String NO_LOOKAHEAD = "public class Foo { }";

    private static final String METHOD_DIFF_LINES = "public class Test {\n public void foo() {\n  int x;\n }\n}";

    private static final String CONTAINS_CHILDREN_OF_TYPE = "public class Test {\n  int x;\n}";

    private static final String CONTAINS_NO_INNER = "public class Test {\n  public class Inner {\n   int foo;\n  }\n}";

    private static final String CONTAINS_NO_INNER_WITH_ANON_INNER = "public class Test {\n  void bar() {\n   foo(new Fuz() { int x = 2;});\n  }\n}";

    private static final String TEST_XPATH = "public class Test {\n  int x = 2;\n  int y = 42;\n}";
}
