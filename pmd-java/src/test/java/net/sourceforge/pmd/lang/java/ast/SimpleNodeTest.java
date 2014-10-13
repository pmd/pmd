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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ParserTst;

import org.junit.Ignore;
import org.junit.Test;


public class SimpleNodeTest extends ParserTst {

    @Test
    public void testMethodDiffLines() throws Throwable {
        Set<ASTMethodDeclaration> methods = getNodes(ASTMethodDeclaration.class, METHOD_DIFF_LINES);
        verifyNode(methods.iterator().next(), 2, 9, 4, 2);
    }

    @Test
    public void testMethodSameLine() throws Throwable {
        Set<ASTMethodDeclaration> methods = getNodes(ASTMethodDeclaration.class, METHOD_SAME_LINE);
        verifyNode(methods.iterator().next(), 2, 9, 2, 21);
    }

    @Test
    public void testNoLookahead() throws Throwable {
        String code = NO_LOOKAHEAD; // 1, 8 -> 1, 20
        Set<ASTClassOrInterfaceDeclaration> uCD = getNodes(ASTClassOrInterfaceDeclaration.class, code);
        verifyNode(uCD.iterator().next(), 1, 8, 1, 20);
    }

    @Test
    public void testHasExplicitExtends() throws Throwable {
        String code = HAS_EXPLICIT_EXTENDS;
        ASTClassOrInterfaceDeclaration ucd = getNodes(ASTClassOrInterfaceDeclaration.class, code).iterator().next();
        assertTrue(ucd.jjtGetChild(0) instanceof ASTExtendsList);
    }

    @Test
    public void testNoExplicitExtends() throws Throwable {
        String code = NO_EXPLICIT_EXTENDS;
        ASTClassOrInterfaceDeclaration ucd = getNodes(ASTClassOrInterfaceDeclaration.class, code).iterator().next();
        assertFalse(ucd.jjtGetChild(0) instanceof ASTExtendsList);
    }

    @Test
    public void testHasExplicitImplements() throws Throwable {
        String code = HAS_EXPLICIT_IMPLEMENTS;
        ASTClassOrInterfaceDeclaration ucd = getNodes(ASTClassOrInterfaceDeclaration.class, code).iterator().next();
        assertTrue(ucd.jjtGetChild(0) instanceof ASTImplementsList);
    }

    @Test
    public void testNoExplicitImplements() throws Throwable {
        String code = NO_EXPLICIT_IMPLEMENTS;
        ASTClassOrInterfaceDeclaration ucd = getNodes(ASTClassOrInterfaceDeclaration.class, code).iterator().next();
        assertFalse(ucd.jjtGetChild(0) instanceof ASTImplementsList);
    }

    @Test
    public void testColumnsOnQualifiedName() throws Throwable {
        Set<ASTName> name = getNodes(ASTName.class, QUALIFIED_NAME);
        Iterator<ASTName> i = name.iterator();
        while (i.hasNext()) {
            Node node = i.next();
            if (node.getImage().equals("java.io.File")) {
                verifyNode(node, 1, 8, 1, 19);
            }
        }
    }

    @Test
    public void testLineNumbersForNameSplitOverTwoLines() throws Throwable {
        Set<ASTName> name = getNodes(ASTName.class, BROKEN_LINE_IN_NAME);
        Iterator<ASTName> i = name.iterator();
        while (i.hasNext()) {
            Node node = i.next();
            if (node.getImage().equals("java.io.File")) {
                verifyNode(node, 1, 8, 2, 4);
            }
            if (node.getImage().equals("Foo")) {
                verifyNode(node, 2, 15, 2, 18);
            }
        }
    }

    @Test
    public void testLineNumbersAreSetOnAllSiblings() throws Throwable {
        for (ASTBlock b: getNodes(ASTBlock.class, LINE_NUMBERS_ON_SIBLINGS)) {
            assertTrue(b.getBeginLine() > 0);
        }
        for (ASTVariableInitializer b: getNodes(ASTVariableInitializer.class, LINE_NUMBERS_ON_SIBLINGS)) {
            assertTrue(b.getBeginLine() > 0);
        }
        for (ASTExpression b: getNodes(ASTExpression.class, LINE_NUMBERS_ON_SIBLINGS)) {
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
    public void testParentMethods() throws Throwable {
    	ASTCompilationUnit u = parseJava14(TEST1);

    	ASTMethodDeclarator d = u.getFirstDescendantOfType(ASTMethodDeclarator.class);
    	assertSame("getFirstParentOfType ASTMethodDeclaration", d.jjtGetParent(), d.getFirstParentOfType(ASTMethodDeclaration.class));
    	assertNull("getFirstParentOfType ASTName", d.getFirstParentOfType(ASTName.class));

    	assertSame("getNthParent 1", d.jjtGetParent(), d.getNthParent(1));
    	assertSame("getNthParent 2", d.jjtGetParent().jjtGetParent(), d.getNthParent(2));
    	assertSame("getNthParent 6", u, d.getNthParent(6));
    	assertNull("getNthParent 7", d.getNthParent(7));
    	assertNull("getNthParent 8", d.getNthParent(8));
    }

    private static final String TEST1 =
            "public class Test {" + PMD.EOL +
            "  void bar(String s) {" + PMD.EOL +
            "   s = s.toLowerCase();" + PMD.EOL +
            "  }" + PMD.EOL +
            "}";

    @Ignore
    @Test
    public void testContainsNoInner() throws Throwable {
        ASTCompilationUnit c = getNodes(ASTCompilationUnit.class, CONTAINS_NO_INNER).iterator().next();
        List<ASTFieldDeclaration> res = new ArrayList<ASTFieldDeclaration>();
        c.findDescendantsOfType(ASTFieldDeclaration.class, res, false);
        assertTrue(res.isEmpty());
/*        String expectedXml = "<CompilationUnit BeginColumn=\"1\" BeginLine=\"5\" EndColumn=\"1\" EndLine=\"5\">" +
                "<TypeDeclaration BeginColumn=\"1\" BeginLine=\"1\" EndColumn=\"1\" EndLine=\"5\">" +
                "<ClassOrInterfaceDeclaration Abstract=\"false\" BeginColumn=\"8\" BeginLine=\"1\" EndColumn=\"1\" " +
                "EndLine=\"5\" Final=\"false\" Image=\"Test\" Interface=\"false\" Native=\"false\" Nested=\"false\" PackagePrivate=\"false\" Private=\"false\" Protected=\"false\" Public=\"true\" Static=\"false\" Strictfp=\"false\" Synchronized=\"false\" Transient=\"false\" Volatile=\"false\">" +
                "<ClassOrInterfaceBody BeginColumn=\"19\" BeginLine=\"1\" EndColumn=\"1\" EndLine=\"5\">" +
                "<ClassOrInterfaceBodyDeclaration AnonymousInnerClass=\"false\" BeginColumn=\"3\" BeginLine=\"2\" EndColumn=\"3\" EndLine=\"4\">" +
                "<ClassOrInterfaceDeclaration Abstract=\"false\" BeginColumn=\"10\" BeginLine=\"2\" EndColumn=\"3\" EndLine=\"4\" Final=\"false\" " +
                "Image=\"Inner\" Interface=\"false\" Native=\"false\" Nested=\"true\" PackagePrivate=\"false\" Private=\"false\" Protected=\"false\" " +
                "Public=\"true\" Static=\"false\" Strictfp=\"false\" Synchronized=\"false\" Transient=\"false\" Volatile=\"false\">" +
                "<ClassOrInterfaceBody BeginColumn=\"22\" BeginLine=\"2\" EndColumn=\"3\" EndLine=\"4\">" +
                "<ClassOrInterfaceBodyDeclaration AnonymousInnerClass=\"false\" BeginColumn=\"4\" BeginLine=\"3\" EndColumn=\"11\" EndLine=\"3\">" +
                "<FieldDeclaration Abstract=\"false\" Array=\"false\" ArrayDepth=\"0\" BeginColumn=\"4\" BeginLine=\"3\" EndColumn=\"11\" EndLine=\"3\" Final=\"false\" Native=\"false\" PackagePrivate=\"true\" Private=\"false\" Protected=\"false\" Public=\"false\" Static=\"false\" Strictfp=\"false\" Synchronized=\"false\" Transient=\"false\" VariableName=\"foo\" Volatile=\"false\"><Type Array=\"false\" ArrayDepth=\"0\" BeginColumn=\"4\" BeginLine=\"3\" EndColumn=\"6\" EndLine=\"3\">" +
                "<PrimitiveType Array=\"false\" ArrayDepth=\"0\" BeginColumn=\"4\" BeginLine=\"3\" Boolean=\"false\" EndColumn=\"6\" EndLine=\"3\" Image=\"int\"/>" +
                "</Type>" +
                "<VariableDeclarator BeginColumn=\"8\" BeginLine=\"3\" EndColumn=\"10\" EndLine=\"3\">" +
                "<VariableDeclaratorId Array=\"false\" ArrayDepth=\"0\" BeginColumn=\"8\" BeginLine=\"3\" EndColumn=\"10\" EndLine=\"3\" ExceptionBlockParameter=\"false\" Image=\"foo\"/>" +
                "</VariableDeclarator></FieldDeclaration></ClassOrInterfaceBodyDeclaration></ClassOrInterfaceBody>" +
                "</ClassOrInterfaceDeclaration></ClassOrInterfaceBodyDeclaration></ClassOrInterfaceBody></ClassOrInterfaceDeclaration>" +
                "</TypeDeclaration></CompilationUnit>";
        assertEquals( expectedXml, getXmlString( c ) );
*/    }

    @Test
    public void testContainsNoInnerWithAnonInner() throws Throwable {
        ASTCompilationUnit c = getNodes(ASTCompilationUnit.class, CONTAINS_NO_INNER_WITH_ANON_INNER).iterator().next();
        List<ASTFieldDeclaration> res = new ArrayList<ASTFieldDeclaration>();
        c.findDescendantsOfType(ASTFieldDeclaration.class, res, false);
        assertTrue(res.isEmpty());
    }

    @Test
    public void testContainsChildOfType() throws Throwable {
        ASTClassOrInterfaceDeclaration c = getNodes(ASTClassOrInterfaceDeclaration.class, CONTAINS_CHILDREN_OF_TYPE).iterator().next();
        assertTrue(c.hasDescendantOfType(ASTFieldDeclaration.class));
    }

    @Test
    public void testXPathNodeSelect() throws Throwable {
        ASTClassOrInterfaceDeclaration c = getNodes(ASTClassOrInterfaceDeclaration.class, TEST_XPATH).iterator().next();
        List nodes = c.findChildNodesWithXPath("//FieldDeclaration");
        assertEquals(2, nodes.size());
        assertTrue(nodes.get(0) instanceof ASTFieldDeclaration);

        assertTrue(c.hasDescendantMatchingXPath("//FieldDeclaration"));
        assertFalse(c.hasDescendantMatchingXPath("//MethodDeclaration"));
    }
    
    @Test
    public void testUserData() throws Throwable {
        ASTClassOrInterfaceDeclaration c = getNodes(ASTClassOrInterfaceDeclaration.class, HAS_EXPLICIT_EXTENDS).iterator().next();
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

    private static final String HAS_EXPLICIT_EXTENDS =
            "public class Test extends Foo {}";

    private static final String NO_EXPLICIT_EXTENDS =
            "public class Test {}";

    private static final String HAS_EXPLICIT_IMPLEMENTS =
            "public class Test implements Foo {}";

    private static final String NO_EXPLICIT_IMPLEMENTS =
            "public class Test {}";

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

    private static final String METHOD_DIFF_LINES =
            "public class Test {" + PMD.EOL +
            " public void foo() {" + PMD.EOL +
            "  int x;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String CONTAINS_CHILDREN_OF_TYPE =
            "public class Test {" + PMD.EOL +
            "  int x;" + PMD.EOL +
            "}";

    private static final String CONTAINS_NO_INNER =
            "public class Test {" + PMD.EOL +
            "  public class Inner {" + PMD.EOL +
            "   int foo;" + PMD.EOL +
            "  }" + PMD.EOL +
            "}";

    private static final String CONTAINS_NO_INNER_WITH_ANON_INNER =
            "public class Test {" + PMD.EOL +
            "  void bar() {" + PMD.EOL +
            "   foo(new Fuz() { int x = 2;});" + PMD.EOL +
            "  }" + PMD.EOL +
            "}";

    private static final String TEST_XPATH =
            "public class Test {" + PMD.EOL +
            "  int x = 2;" + PMD.EOL +
            "  int y = 42;" + PMD.EOL +
            "}";

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(SimpleNodeTest.class);
    }
}
