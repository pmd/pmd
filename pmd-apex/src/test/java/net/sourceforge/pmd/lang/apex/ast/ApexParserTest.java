/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import static net.sourceforge.pmd.lang.test.ast.NodeExtensionsKt.textOfReportLocation;
import static net.sourceforge.pmd.lang.test.ast.TestUtilsKt.assertPosition;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.internal.util.IOUtil;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.ParseException;
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
        List<ASTMethod> methods = rootNode.descendants(ASTMethod.class).toList();
        assertEquals(1, methods.size());
    }

    @Test
    void parseErrors() {
        ParseException exception = assertThrows(ParseException.class, () -> parse("public class SimpleClass { String x = \"a\"; }"));
        assertThat(exception.getMessage(), containsString("Syntax error at 1:38: token recognition error at: '\"'"));
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
        
        // identifier: "SimpleClass"
        assertEquals("SimpleClass", classNode.getSimpleName());
        // Class location starts at the "class" keyword. (It excludes modifiers.)
        assertPosition(classNode, 1, 8, 6, 2);
        // "public" modifier for class
        assertPosition(classNode.getChild(0), 1, 1, 1, 7);

        // identifier: "method1"                                                                                                                                                                  
        Node method1 = classNode.getChild(1);
        assertEquals("method1", ((ASTMethod) method1).getCanonicalName());
        // "method1" - spans from return type to end of its block statement. (It excludes modifiers.)
        assertPosition(method1, 2, 12, 5, 6);
        // "public" modifier for "method1"
        assertPosition(method1.getChild(0), 2, 5, 2, 11);

        // BlockStatement - the whole method body
        Node blockStatement = method1.getChild(1);
        assertTrue(((ASTBlockStatement) blockStatement).hasCurlyBrace(), "should detect curly brace");
        assertPosition(blockStatement, 2, 27, 5, 6);

        // the expression ("System.out...")
        Node expressionStatement = blockStatement.getChild(0);
        assertPosition(expressionStatement, 3, 9, 3, 35);
        assertTextEquals("System.out.println('abc');", expressionStatement);
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

        // The method subtree spans the return type to the end of the method body.
        Node method1 = rootNode.getChild(1);
        assertPosition(method1, 2, 12, 3, 6);

        Node method2 = rootNode.getChild(2);
        assertPosition(method2, 4, 12, 5, 6);
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
        assertEquals("/** Comment on Class */", ((ASTFormalComment) comment).getImage());

        ApexNode<?> m1 = root.getChild(2);
        assertThat(m1, instanceOf(ASTMethod.class));

        ApexNode<?> comment2 = m1.getChild(0);
        assertThat(comment2, instanceOf(ASTFormalComment.class));
        assertEquals("/** Comment on m1 */", ((ASTFormalComment) comment2).getImage());
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
        assertEquals(471, count);
    }

    @Test
    void verifyLineColumnNumbersInnerClasses() {
        ASTApexFile rootNode = apex.parseResource("InnerClassLocations.cls");
        assertNotNull(rootNode);

        visitPosition(rootNode, 0);

        ASTUserClassOrInterface<?> classNode = rootNode.getMainNode();
        assertEquals("InnerClassLocations", classNode.getSimpleName());
        // Class location starts at the "class" keyword. (It excludes any modifiers.)                                                                                                             
        assertPosition(classNode, 1, 8, 16, 2);

        List<ASTUserClass> classes = classNode.descendants(ASTUserClass.class).toList();
        assertEquals(2, classes.size());
        assertEquals("bar1", classes.get(0).getSimpleName());
        List<ASTMethod> methods = classes.get(0).children(ASTMethod.class).toList();
        assertEquals(1, methods.size()); // m(). No synthetic clone()
        assertEquals("m", methods.get(0).getImage());
        assertPosition(methods.get(0), 4, 16, 7, 10);

        // Position of the first inner class is its identifier
        assertPosition(classes.get(0), 3, 12, 8, 6);

        assertEquals("bar2", classes.get(1).getSimpleName());
        assertPosition(classes.get(1), 10, 12, 15, 6);
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
