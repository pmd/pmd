/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.ast.Node;

import apex.jorje.semantic.ast.compilation.Compilation;

public class ApexParserTest extends ApexParserTestBase {

    @Test
    public void understandsSimpleFile() {

        // Setup
        String code = "@isTest\n public class SimpleClass {\n" + "    @isTest\n public static void testAnything() {\n"
            + "        \n" + "    }\n" + "}";

        // Exercise
        ApexNode<Compilation> rootNode = parse(code);

        // Verify
        List<ASTMethod> methods = rootNode.findDescendantsOfType(ASTMethod.class);
        assertEquals(4, methods.size());
    }

    private String testCodeForLineNumbers =
              "public class SimpleClass {\n" // line 1
            + "    public void method1() {\n" // line 2
            + "        System.out.println(\"abc\");\n" // line 3
            + "        // this is a comment\n" // line 4
            + "    }\n" // line 5
            + "}\n"; // line 6

    @Test
    public void verifyLineColumNumbers() {
        ApexNode<Compilation> rootNode = parse(testCodeForLineNumbers);
        assertLineNumbersForTestCode(rootNode);
    }

    @Test
    public void verifyLineColumNumbersWithWindowsLineEndings() {
        String windowsLineEndings = testCodeForLineNumbers.replaceAll(" \n", "\r\n");
        ApexNode<Compilation> rootNode = parse(windowsLineEndings);
        assertLineNumbersForTestCode(rootNode);
    }

    private void assertLineNumbersForTestCode(ApexNode<Compilation> rootNode) {
        // whole source code, well from the beginning of the class
        // name Modifier of the class - doesn't work. This node just
        // sees the identifier ("SimpleClass")
        // assertPosition(rootNode.getChild(0), 1, 1, 1, 6);

        // "public"
        assertPosition(rootNode, 1, 14, 6, 2);

        // "method1" - starts with identifier until end of its block statement
        Node method1 = rootNode.getChild(1);
        assertPosition(method1, 2, 17, 5, 5);
        // Modifier of method1 - doesn't work. This node just sees the
        // identifier ("method1")
        // assertPosition(method1.getChild(0), 2, 17, 2, 20); // "public" for
        // method1

        // BlockStatement - the whole method body
        Node blockStatement = method1.getChild(1);
        assertTrue(((ASTBlockStatement) blockStatement).hasCurlyBrace());
        assertPosition(blockStatement, 2, 27, 5, 5);

        // the expression ("System.out...")
        Node expressionStatement = blockStatement.getChild(0);
        assertPosition(expressionStatement, 3, 9, 3, 34);
    }

    @Test
    public void verifyEndLine() {

        String code = "public class SimpleClass {\n" // line 1
                + "    public void method1() {\n" // line 2
                + "    }\n" // line 3
                + "    public void method2() {\n" // line 4
                + "    }\n" // line 5
                + "}\n"; // line 6

        ApexNode<Compilation> rootNode = parse(code);

        Node method1 = rootNode.getChild(1);
        assertEquals("Wrong begin line", 2, method1.getBeginLine());
        assertEquals("Wrong end line", 3, method1.getEndLine());

        Node method2 = rootNode.getChild(2);
        assertEquals("Wrong begin line", 4, method2.getBeginLine());
        assertEquals("Wrong end line", 5, method2.getEndLine());
    }

    @Test
    public void checkComments() {

        String code = "public  /** Comment on Class */ class SimpleClass {\n" // line 1
            + "    /** Comment on m1 */"
            + "    public void method1() {\n" // line 2
            + "    }\n" // line 3
            + "    public void method2() {\n" // line 4
            + "    }\n" // line 5
            + "}\n"; // line 6

        ApexNode<Compilation> root = parse(code);

        assertThat(root, instanceOf(ASTUserClass.class));
        ApexNode<?> comment = root.getChild(0);
        assertThat(comment, instanceOf(ASTFormalComment.class));

        assertNotEquals(comment.getNode(), null);
        assertThat(comment.getNode(), instanceOf(ASTFormalComment.AstComment.class));
        assertPosition(comment, 1, 9, 1, 31);
        assertEquals("/** Comment on Class */", ((ASTFormalComment) comment).getToken());

        ApexNode<?> m1 = root.getChild(2);
        assertThat(m1, instanceOf(ASTMethod.class));

        ApexNode<?> comment2 = m1.getChild(0);
        assertThat(comment2, instanceOf(ASTFormalComment.class));
        assertEquals("/** Comment on m1 */", ((ASTFormalComment) comment2).getToken());
    }

    @Test
    public void parsesRealWorldClasses() throws Exception {
        File directory = new File("src/test/resources");
        File[] fList = directory.listFiles();

        for (File file : fList) {
            if (file.isFile() && file.getName().endsWith(".cls")) {
                String sourceCode = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
                ApexNode<Compilation> rootNode = parse(sourceCode);
                Assert.assertNotNull(rootNode);
            }
        }
    }

    /**
     * See github issue #1546
     * @see <a href="https://github.com/pmd/pmd/issues/1546">[apex] PMD parsing exception for Apex classes using 'inherited sharing' keyword</a>
     */
    @Test
    public void parseInheritedSharingClass() throws IOException {
        String source = IOUtils.toString(ApexParserTest.class.getResourceAsStream("InheritedSharing.cls"),
                StandardCharsets.UTF_8);
        ApexNode<Compilation> rootNode = parse(source);
        Assert.assertNotNull(rootNode);
    }

    /**
     * See bug #1485
     *
     * @see <a href="https://sourceforge.net/p/pmd/bugs/1485/">#1485 [apex] Analysis of some apex classes cause a stackoverflow error</a>
     */
    @Test
    public void stackOverflowDuringClassParsing() throws Exception {
        String source = IOUtils.toString(ApexParserTest.class.getResourceAsStream("StackOverflowClass.cls"),
                StandardCharsets.UTF_8);
        ApexNode<Compilation> rootNode = parse(source);
        Assert.assertNotNull(rootNode);

        int count = visitPosition(rootNode, 0);
        Assert.assertEquals(427, count);
    }

    private int visitPosition(Node node, int count) {
        int result = count + 1;
        Assert.assertTrue(node.getBeginLine() > 0);
        Assert.assertTrue(node.getBeginColumn() > 0);
        Assert.assertTrue(node.getEndLine() > 0);
        Assert.assertTrue(node.getEndColumn() > 0);
        for (int i = 0; i < node.getNumChildren(); i++) {
            result = visitPosition(node.getChild(i), result);
        }
        return result;
    }

    // TEST HELPER

    private static void assertPosition(Node node, int beginLine, int beginColumn, int endLine, int endColumn) {
        assertEquals("Wrong begin line", beginLine, node.getBeginLine());
        assertEquals("Wrong begin column", beginColumn, node.getBeginColumn());
        assertEquals("Wrong end line", endLine, node.getEndLine());
        assertEquals("Wrong end column", endColumn, node.getEndColumn());
    }
}
