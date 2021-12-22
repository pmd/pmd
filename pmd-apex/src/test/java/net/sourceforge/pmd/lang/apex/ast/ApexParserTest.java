/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import static net.sourceforge.pmd.lang.ast.test.TestUtilsKt.assertPosition;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.util.document.FileLocation;

public class ApexParserTest extends ApexParserTestBase {

    @Test
    public void understandsSimpleFile() {

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
    public void fileName() {
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
    public void verifyLineColumnNumbers() {
        ASTUserClassOrInterface<?> rootNode = parse(testCodeForLineNumbers);
        assertLineNumbersForTestCode(rootNode);
    }

    @Test
    public void verifyLineColumnNumbersWithWindowsLineEndings() {
        String windowsLineEndings = testCodeForLineNumbers.replaceAll(" \n", "\r\n");
        ASTUserClassOrInterface<?> rootNode = parse(windowsLineEndings);
        assertLineNumbersForTestCode(rootNode);
    }

    private void assertLineNumbersForTestCode(ASTUserClassOrInterface<?> rootNode) {
        // whole source code, well from the beginning of the class
        // name Modifier of the class - doesn't work. This node just
        // sees the identifier ("SimpleClass")
        // assertPosition(rootNode.getChild(0), 1, 1, 1, 6);

        // "public"
        assertPosition(rootNode, 1, 14, 1, 25);

        // "method1" - starts with identifier until end of its block statement
        Node method1 = rootNode.getChild(1);
        assertPosition(method1, 2, 17, 2, 24);
        // Modifier of method1 - doesn't work. This node just sees the
        // identifier ("method1")
        // assertPosition(method1.getChild(0), 2, 17, 2, 20); // "public" for
        // method1

        // BlockStatement - the whole method body
        Node blockStatement = method1.getChild(1);
        assertTrue("should detect curly brace", ((ASTBlockStatement) blockStatement).hasCurlyBrace());
        assertPosition(blockStatement, 2, 27, 5, 6);

        // the expression ("System.out...")
        Node expressionStatement = blockStatement.getChild(0);
        assertPosition(expressionStatement, 3, 20, 3, 35);
    }

    @Test
    public void verifyEndLine() {

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
    public void checkComments() {

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
    public void parsesRealWorldClasses() throws Exception {
        File directory = new File("src/test/resources");
        File[] fList = directory.listFiles();

        for (File file : fList) {
            if (file.isFile() && file.getName().endsWith(".cls")) {
                String sourceCode = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
                Assert.assertNotNull(parse(sourceCode));
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
        parse(source);
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
        ASTUserClassOrInterface<?> rootNode = parse(source);

        int count = visitPosition(rootNode, 0);
        Assert.assertEquals(487, count);
    }

    @Test
    @Ignore("This is buggy, I'd like to stop pretending our reportLocation is a real node position")
    public void verifyLineColumnNumbersInnerClasses() throws Exception {
        ASTApexFile rootNode = apex.parseResource("InnerClassLocations.cls");
        Assert.assertNotNull(rootNode);

        visitPosition(rootNode, 0);

        Assert.assertEquals("InnerClassLocations", rootNode.getMainNode().getSimpleName());
        // Note: Apex parser doesn't provide positions for "public class" keywords. The
        // position of the UserClass node is just the identifier. So, the node starts
        // with the identifier and not with the first keyword in the file...
        assertPosition(rootNode, 1, 14, 16, 2);

        List<ASTUserClass> classes = rootNode.descendants(ASTUserClass.class).toList();
        Assert.assertEquals(2, classes.size());
        Assert.assertEquals("bar1", classes.get(0).getSimpleName());
        List<ASTMethod> methods = classes.get(0).children(ASTMethod.class).toList();
        Assert.assertEquals(2, methods.size()); // m() and synthetic clone()
        Assert.assertEquals("m", methods.get(0).getImage());
        assertPosition(methods.get(0), 4, 21, 7, 9);
        Assert.assertEquals("clone", methods.get(1).getImage());
        assertPosition(methods.get(1), 7, 9, 7, 9);

        // Position of the first inner class: starts with the identifier "bar1" and ends with
        // the last real method m(). The last bracket it actually on the next line 8, but we
        // don't see this in the AST.
        assertPosition(classes.get(0), 3, 18, 7, 9);

        Assert.assertEquals("bar2", classes.get(1).getImage());
        assertPosition(classes.get(1), 10, 18, 14, 9);
    }

    // TEST HELPER

    private int visitPosition(Node node, int count) {
        int result = count + 1;
        FileLocation loc = node.getReportLocation();
        Assert.assertTrue(loc.getBeginLine() > 0);
        Assert.assertTrue(loc.getBeginColumn() > 0);
        Assert.assertTrue(loc.getEndLine() > 0);
        Assert.assertTrue(loc.getEndColumn() > 0);
        for (int i = 0; i < node.getNumChildren(); i++) {
            result = visitPosition(node.getChild(i), result);
        }
        return result;
    }
}
