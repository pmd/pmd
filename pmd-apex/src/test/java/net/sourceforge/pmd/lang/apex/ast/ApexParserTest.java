/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import static net.sourceforge.pmd.lang.ast.test.NodeExtensionsKt.textOfReportLocation;
import static net.sourceforge.pmd.lang.ast.test.TestUtilsKt.assertPosition;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.internal.util.IOUtil;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.document.FileLocation;

class ApexParserTest extends ApexParserTestBase {

    @Test
    void understandsSimpleFile() {

        // Setup
        String code = "@isTest\n"
            + " public class SimpleClass {\n"
            + "    @isTest\n public static void testAnything() {\n"
            + "        \n"
            + "    }\n"
            + "}";

        // Exercise
        ASTUserClassOrInterface<?> rootNode = parse(code);

        // Verify
        List<ASTMethod> methods = rootNode.findDescendantsOfType(ASTMethod.class);
        assertEquals(4, methods.size());
    }

    @Test
    void fileName() {
        String code = "class Outer { class Inner {}}";

        ASTUserClass rootNode = (ASTUserClass) parse(code, "src/filename.cls");

        assertEquals("src/filename.cls", rootNode.getTextDocument().getDisplayName());
    }

    private final String testCodeForLineNumbers =
              "public class SimpleClass {\n" // line 1
            + "    public void method1() {\n" // line 2
            + "        System.out.println('abc');\n" // line 3
            + "        // this is a comment\n" // line 4
            + "    }\n" // line 5
            + "}"; // line 6

    @Test
    void verifyLineColumnNumbers() {
        ASTUserClassOrInterface<?> rootNode = parse(testCodeForLineNumbers);
        assertLineNumbersForTestCode(rootNode);
    }

    @Test
    void verifyLineColumnNumbersWithWindowsLineEndings() {
        String windowsLineEndings = testCodeForLineNumbers.replaceAll(" \n", "\r\n");
        ASTUserClassOrInterface<?> rootNode = parse(windowsLineEndings);
        assertLineNumbersForTestCode(rootNode);
    }

    private void assertLineNumbersForTestCode(ASTUserClassOrInterface<?> classNode) {
        // whole source code, well from the beginning of the class
        // name Modifier of the class - doesn't work. This node just
        // sees the identifier ("SimpleClass")
        // assertPosition(rootNode.getChild(0), 1, 1, 1, 6);

        // identifier: "SimpleClass"
        assertPosition(classNode, 1, 14, 1, 25);
        assertTextEquals("SimpleClass", classNode);

        // identifier: "method1"
        Node method1 = classNode.getChild(1);
        assertTextEquals("method1", method1);
        assertPosition(method1, 2, 17, 2, 24);
        // modifiers have same location
        assertPosition(method1.getChild(0), 2, 17, 2, 24);

        // BlockStatement - the whole method body
        Node blockStatement = method1.getChild(1);
        assertTrue(((ASTBlockStatement) blockStatement).hasCurlyBrace(), "should detect curly brace");
        assertPosition(blockStatement, 2, 27, 5, 6);

        // the expression ("System.out...")
        Node expressionStatement = blockStatement.getChild(0);
        assertPosition(expressionStatement, 3, 20, 3, 35);
        assertTextEquals("println('abc');", expressionStatement);
    }

    @Test
    void verifyEndLine() {

        String code = "public class SimpleClass {\n" // line 1
                + "    public void method1() {\n" // line 2
                + "    }\n" // line 3
                + "    public void method2() {\n" // line 4
                + "    }\n" // line 5
                + "}\n"; // line 6

        ASTUserClassOrInterface<?> rootNode = parse(code);

        Node method1 = rootNode.getChild(1);
        assertPosition(method1, 2, 17, 2, 24);

        Node method2 = rootNode.getChild(2);
        assertPosition(method2, 4, 17, 4, 24);
    }

    @Test
    void checkComments() {

        String code = "public  /** Comment on Class */ class SimpleClass {\n" // line 1
            + "    /** Comment on m1 */"
            + "    public void method1() {\n" // line 2
            + "    }\n" // line 3
            + "    public void method2() {\n" // line 4
            + "    }\n" // line 5
            + "}\n"; // line 6

        ASTUserClassOrInterface<?> root = parse(code);

        assertThat(root, instanceOf(ASTUserClass.class));
        ApexNode<?> comment = root.getChild(0);
        assertThat(comment, instanceOf(ASTFormalComment.class));

        assertPosition(comment, 1, 9, 1, 32);
        assertEquals("/** Comment on Class */", ((ASTFormalComment) comment).getToken().toString());

        ApexNode<?> m1 = root.getChild(2);
        assertThat(m1, instanceOf(ASTMethod.class));

        ApexNode<?> comment2 = m1.getChild(0);
        assertThat(comment2, instanceOf(ASTFormalComment.class));
        assertEquals("/** Comment on m1 */", ((ASTFormalComment) comment2).getToken().toString());
    }

    @Test
    void parsesRealWorldClasses() throws Exception {
        File directory = new File("src/test/resources");
        File[] fList = directory.listFiles();

        for (File file : fList) {
            if (file.isFile() && file.getName().endsWith(".cls")) {
                String sourceCode = IOUtil.readFileToString(file, StandardCharsets.UTF_8);
                assertNotNull(parse(sourceCode));
            }
        }
    }

    /**
     * See github issue #1546
     * @see <a href="https://github.com/pmd/pmd/issues/1546">[apex] PMD parsing exception for Apex classes using 'inherited sharing' keyword</a>
     */
    @Test
    void parseInheritedSharingClass() throws IOException {
        String source = IOUtil.readToString(ApexParserTest.class.getResourceAsStream("InheritedSharing.cls"),
                StandardCharsets.UTF_8);
        assertNotNull(parse(source));
    }

    /**
     * See bug #1485
     *
     * @see <a href="https://sourceforge.net/p/pmd/bugs/1485/">#1485 [apex] Analysis of some apex classes cause a stackoverflow error</a>
     */
    @Test
    void stackOverflowDuringClassParsing() throws Exception {
        String source = IOUtil.readToString(ApexParserTest.class.getResourceAsStream("StackOverflowClass.cls"),
                                            StandardCharsets.UTF_8);
        ASTUserClassOrInterface<?> rootNode = parse(source);
        assertNotNull(rootNode);

        int count = visitPosition(rootNode, 0);
        assertEquals(487, count);
    }

    @Test
    void verifyLineColumnNumbersInnerClasses() {
        ASTApexFile rootNode = apex.parseResource("InnerClassLocations.cls");
        assertNotNull(rootNode);

        visitPosition(rootNode, 0);

        ASTUserClassOrInterface<?> classNode = rootNode.getMainNode();
        assertEquals("InnerClassLocations", classNode.getSimpleName());
        assertTextEquals("InnerClassLocations", classNode);
        // Note: Apex parser doesn't provide positions for "public class" keywords. The
        // position of the UserClass node is just the identifier. So, the node starts
        // with the identifier and not with the first keyword in the file...
        assertPosition(classNode, 1, 14, 1, 33);

        List<ASTUserClass> classes = classNode.descendants(ASTUserClass.class).toList();
        assertEquals(2, classes.size());
        assertEquals("bar1", classes.get(0).getSimpleName());
        List<ASTMethod> methods = classes.get(0).children(ASTMethod.class).toList();
        assertEquals(2, methods.size()); // m() and synthetic clone()
        assertEquals("m", methods.get(0).getImage());
        assertPosition(methods.get(0), 4, 21, 4, 22);
        assertEquals("clone", methods.get(1).getImage());
        assertFalse(methods.get(1).hasRealLoc());
        assertPosition(methods.get(1), 3, 18, 3, 22);

        // Position of the first inner class is its identifier
        assertPosition(classes.get(0), 3, 18, 3, 22);

        assertEquals("bar2", classes.get(1).getSimpleName());
        assertPosition(classes.get(1), 10, 18, 10, 22);
    }

    // TEST HELPER

    private int visitPosition(Node node, int count) {
        int result = count + 1;
        FileLocation loc = node.getReportLocation();
        assertTrue(loc.getStartLine() > 0);
        assertTrue(loc.getStartColumn() > 0);
        assertTrue(loc.getEndLine() > 0);
        assertTrue(loc.getEndColumn() > 0);
        for (int i = 0; i < node.getNumChildren(); i++) {
            result = visitPosition(node.getChild(i), result);
        }
        return result;
    }

    private void assertTextEquals(String expected, Node expressionStatement) {
        assertEquals(expected, textOfReportLocation(expressionStatement));
    }
}
