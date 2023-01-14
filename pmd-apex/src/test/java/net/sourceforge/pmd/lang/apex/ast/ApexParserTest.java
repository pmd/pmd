/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.xpath.internal.FileNameXPathFunction;
import net.sourceforge.pmd.util.IOUtil;

public class ApexParserTest extends ApexParserTestBase {

    @Test
    public void understandsSimpleFile() {

        // Setup
        String code = "@isTest\n public class SimpleClass {\n" + "    @isTest\n public static void testAnything() {\n"
            + "        \n" + "    }\n" + "}";

        // Exercise
        ApexNode<?> rootNode = parse(code);

        // Verify
        List<ASTMethod> methods = rootNode.findDescendantsOfType(ASTMethod.class);
        assertEquals(1, methods.size());
    }

    @Test
    public void fileName() {
        String code = "class Outer { class Inner {}}";

        ASTUserClass rootNode = (ASTUserClass) parse(code, "src/filename.cls");

        assertEquals("filename.cls", rootNode.getUserMap().get(FileNameXPathFunction.FILE_NAME_KEY));
    }

    private String testCodeForLineNumbers =
              "public class SimpleClass {\n" // line 1
            + "    public void method1() {\n" // line 2
            + "        System.out.println('abc');\n" // line 3
            + "        // this is a comment\n" // line 4
            + "    }\n" // line 5
            + "}\n"; // line 6

    @Test
    public void verifyLineColumnNumbers() {
        ApexNode<?> rootNode = parse(testCodeForLineNumbers);
        assertLineNumbersForTestCode(rootNode);
    }

    @Test
    public void verifyLineColumnNumbersWithWindowsLineEndings() {
        String windowsLineEndings = testCodeForLineNumbers.replaceAll(" \n", "\r\n");
        ApexNode<?> rootNode = parse(windowsLineEndings);
        assertLineNumbersForTestCode(rootNode);
    }

    private void assertLineNumbersForTestCode(ApexNode<?> rootNode) {
        // Class location starts at the "class" keyword. (It excludes modifiers.)
        assertPosition(rootNode, 1, 8, 6, 2);
        // "public" modifier for class
        assertPosition(rootNode.getChild(0), 1, 1, 1, 6);

        // "method1" - spans from return type to end of its block statement. (It excluded modifiers.)
        Node method1 = rootNode.getChild(1);
        assertPosition(method1, 2, 12, 5, 5);
        // "public" modifier for "method1"
        assertPosition(method1.getChild(0), 2, 5, 2, 10);

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

        ApexNode<?> rootNode = parse(code);

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

        ApexNode<?> root = parse(code);

        assertThat(root, instanceOf(ASTUserClass.class));
        ApexNode<?> comment = root.getChild(0);
        assertThat(comment, instanceOf(ASTFormalComment.class));

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
                String sourceCode = IOUtil.readFileToString(file, StandardCharsets.UTF_8);
                ApexNode<?> rootNode = parse(sourceCode);
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
        String source = IOUtil.readToString(ApexParserTest.class.getResourceAsStream("InheritedSharing.cls"),
                StandardCharsets.UTF_8);
        ApexNode<?> rootNode = parse(source);
        Assert.assertNotNull(rootNode);
    }

    /**
     * See bug #1485
     *
     * @see <a href="https://sourceforge.net/p/pmd/bugs/1485/">#1485 [apex] Analysis of some apex classes cause a stackoverflow error</a>
     */
    @Test
    public void stackOverflowDuringClassParsing() throws Exception {
        String source = IOUtil.readToString(ApexParserTest.class.getResourceAsStream("StackOverflowClass.cls"),
                StandardCharsets.UTF_8);
        ApexNode<?> rootNode = parse(source);
        Assert.assertNotNull(rootNode);

        int count = visitPosition(rootNode, 0);
        Assert.assertEquals(471, count);
    }

    @Test
    public void verifyLineColumnNumbersInnerClasses() throws Exception {
        String source = IOUtil.readToString(ApexParserTest.class.getResourceAsStream("InnerClassLocations.cls"),
                StandardCharsets.UTF_8);
        source = source.replaceAll("\r\n", "\n");
        ApexNode<?> rootNode = parse(source);
        Assert.assertNotNull(rootNode);

        visitPosition(rootNode, 0);

        Assert.assertEquals("InnerClassLocations", rootNode.getImage());
        // Class location starts at the "class" keyword. (It excludes any modifiers.)
        assertPosition(rootNode, 1, 8, 16, 2);

        List<ASTUserClass> classes = rootNode.findDescendantsOfType(ASTUserClass.class);
        Assert.assertEquals(2, classes.size());
        Assert.assertEquals("bar1", classes.get(0).getImage());
        List<ASTMethod> methods = classes.get(0).findChildrenOfType(ASTMethod.class);
        Assert.assertEquals(1, methods.size()); // m()
        Assert.assertEquals("m", methods.get(0).getImage());
        assertPosition(methods.get(0), 4, 16, 7, 9);

        // Position of the first inner class: starts with the identifier "class" and ends with
        // the last real method m(). The last bracket it actually on the next line 8, but we
        // don't see this in the AST.
        assertPosition(classes.get(0), 3, 12, 7, 9);

        Assert.assertEquals("bar2", classes.get(1).getImage());
        assertPosition(classes.get(1), 10, 12, 14, 9);
    }

    // TEST HELPER

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

    private static void assertPosition(Node node, int beginLine, int beginColumn, int endLine, int endColumn) {
        assertEquals("Wrong begin line", beginLine, node.getBeginLine());
        assertEquals("Wrong begin column", beginColumn, node.getBeginColumn());
        assertEquals("Wrong end line", endLine, node.getEndLine());
        assertEquals("Wrong end column", endColumn, node.getEndColumn());
    }
}
