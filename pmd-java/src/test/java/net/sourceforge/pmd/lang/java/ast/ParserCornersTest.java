/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

public class ParserCornersTest {

    private static final String MULTICATCH = "public class Foo { public void bar() { "
        + "try { System.out.println(); } catch (RuntimeException | IOException e) {} } }";
    private final JavaParsingHelper java = JavaParsingHelper.WITH_PROCESSING.withResourceContext(ParserCornersTest.class);
    private final JavaParsingHelper java8 = java.withDefaultVersion("1.8");
    private final JavaParsingHelper java4 = java.withDefaultVersion("1.4");
    private final JavaParsingHelper java5 = java.withDefaultVersion("1.5");
    private final JavaParsingHelper java7 = java.withDefaultVersion("1.7");
    @Rule
    public ExpectedException expect = ExpectedException.none();

    /**
     * #1107 PMD 5.0.4 couldn't parse call of parent outer java class method
     * from inner class.
     */
    @Test
    public void testInnerOuterClass() {
        java7.parse("/**\n" + " * @author azagorulko\n" + " *\n" + " */\n"
                        + "public class TestInnerClassCallsOuterParent {\n" + "\n" + "    public void test() {\n"
                        + "        new Runnable() {\n" + "            @Override\n" + "            public void run() {\n"
                        + "                TestInnerClassCallsOuterParent.super.toString();\n" + "            }\n"
                        + "        };\n" + "    }\n" + "}\n");
    }

    /**
     * #888 PMD 6.0.0 can't parse valid <> under 1.8.
     */
    @Test
    public void testDiamondUsageJava8() {
        java8.parse("public class PMDExceptionTest {\n"
                + "  private Component makeUI() {\n"
                + "    String[] model = {\"123456\", \"7890\"};\n"
                + "    JComboBox<String> comboBox = new JComboBox<>(model);\n"
                + "    comboBox.setEditable(true);\n"
                + "    comboBox.setEditor(new BasicComboBoxEditor() {\n"
                + "      private Component editorComponent;\n"
                + "      @Override public Component getEditorComponent() {\n"
                + "        if (editorComponent == null) {\n"
                + "          JTextField tc = (JTextField) super.getEditorComponent();\n"
                + "          editorComponent = new JLayer<>(tc, new ValidationLayerUI<>());\n"
                + "        }\n"
                + "        return editorComponent;\n"
                + "      }\n"
                + "    });\n"
                + "    JPanel p = new JPanel();\n"
                + "    p.add(comboBox);\n"
                + "    return p;\n"
                + "  }\n"
                + "}");
    }

    @Test
    public final void testGetFirstASTNameImageNull() {
        java4.parse(ABSTRACT_METHOD_LEVEL_CLASS_DECL);
    }

    @Test
    public final void testCastLookaheadProblem() {
        java4.parse(CAST_LOOKAHEAD_PROBLEM);
    }

    /**
     * Tests a specific generic notation for calling methods. See:
     * https://jira.codehaus.org/browse/MPMD-139
     */
    @Test
    public void testGenericsProblem() {
        java5.parse(GENERICS_PROBLEM);
        java7.parse(GENERICS_PROBLEM);
    }

    @Test
    public void testParsersCases15() {
        java5.parseResource("ParserCornerCases.java");
    }

    @Test
    public void testParsersCases17() {
        java7.parseResource("ParserCornerCases17.java");
    }

    @Test
    public void testParsersCases18() throws Exception {
        ASTCompilationUnit cu = java8.parseResource("ParserCornerCases18.java");

        Assert.assertEquals(21, cu.findChildNodesWithXPath("//FormalParameter").size());
        Assert.assertEquals(4,
                cu.findChildNodesWithXPath("//FormalParameter[@ExplicitReceiverParameter='true']").size());
        Assert.assertEquals(17,
                cu.findChildNodesWithXPath("//FormalParameter[@ExplicitReceiverParameter='false']").size());
    }

    /**
     * Test for https://sourceforge.net/p/pmd/bugs/1333/
     */
    @Test
    public void testLambdaBug1333() {
        java8.parse("final class Bug1333 {\n"
                        + "    private static final Logger LOG = LoggerFactory.getLogger(Foo.class);\n" + "\n"
                        + "    public void deleteDirectoriesByNamePattern() {\n"
                        + "        delete(path -> deleteDirectory(path));\n" + "    }\n" + "\n"
                        + "    private void delete(Consumer<? super String> consumer) {\n"
                        + "        LOG.debug(consumer.toString());\n" + "    }\n" + "\n"
                        + "    private void deleteDirectory(String path) {\n" + "        LOG.debug(path);\n" + "    }\n"
                        + "}");
    }

    @Test
    public void testLambdaBug1470() {
        java8.parseResource("LambdaBug1470.java");
    }

    /**
     * Test for https://sourceforge.net/p/pmd/bugs/1355/
     */
    @Test
    public void emptyFileJustComment() {
        java8.parse("// just a comment");
    }

    @Test
    public void testMultipleExceptionCatchingJava5() {
        expect.expect(ParseException.class);
        expect.expectMessage("Line 1, Column 94: Cannot catch multiple exceptions when running in JDK inferior to 1.7 mode!");

        java5.parse(MULTICATCH);
    }

    @Test
    public void testMultipleExceptionCatchingJava7() {
        java7.parse(MULTICATCH);
    }

    @Test
    public void testBug1429ParseError() {
        java8.parseResource("Bug1429.java");
    }

    @Test
    public void testBug1530ParseError() {
        java8.parseResource("Bug1530.java");
    }

    @Test
    public void testGitHubBug207() {
        java8.parseResource("GitHubBug207.java");
    }

    @Test
    public void testBug206() {
        java8.parse("public @interface Foo {" + "\n"
                        + "static final ThreadLocal<Interner<Integer>> interner =" + "\n"
                        + "    ThreadLocal.withInitial(Interners::newStrongInterner);" + "\n"
                        + "}");
    }

    @Test
    public void testGitHubBug208ParseError() {
        java5.parseResource("GitHubBug208.java");
    }

    @Test
    public void testGitHubBug257NonExistingCast() {
        String code = "public class Test {" + "\n"
            + "     public static void main(String[] args) {" + "\n"
            + "         double a = 4.0;" + "\n"
            + "         double b = 2.0;" + "\n"
            + "         double result = Math.sqrt((a)   - b);" + "\n"
            + "         System.out.println(result);" + "\n"
            + "     }" + "\n"
            + "}";

        assertEquals("A cast was found when none expected",
                     0,
                     java5.parse(code).findDescendantsOfType(ASTCastExpression.class).size());
    }

    @Test
    public void testGitHubBug309() {
        java8.parseResource("GitHubBug309.java");
    }

    /**
     * This triggered bug #1484 UnusedLocalVariable - false positive -
     * parenthesis
     */
    @Test
    public void stringConcatentationShouldNotBeCast() {
        String code = "public class Test {\n" + "    public static void main(String[] args) {\n"
            + "        System.out.println(\"X\" + (args) + \"Y\");\n" + "    }\n" + "}";
        Assert.assertEquals(0, java8.parse(code).findDescendantsOfType(ASTCastExpression.class).size());
    }

    /**
     * Empty statements should be allowed.
     * @throws Exception
     * @see <a href="https://github.com/pmd/pmd/issues/378">github issue 378</a>
     */
    @Test
    public void testParseEmptyStatements() {
        String code = "import a;;import b; public class Foo {}";
        ASTCompilationUnit cu = java8.parse(code);
        assertNotNull(cu);
        Assert.assertEquals(ASTEmptyStatement.class, cu.getChild(1).getClass());

        String code2 = "package c;; import a; import b; public class Foo {}";
        ASTCompilationUnit cu2 = java8.parse(code2);
        assertNotNull(cu2);
        Assert.assertEquals(ASTEmptyStatement.class, cu2.getChild(1).getClass());

        String code3 = "package c; import a; import b; public class Foo {};";
        ASTCompilationUnit cu3 = java8.parse(code3);
        assertNotNull(cu3);
        Assert.assertEquals(ASTEmptyStatement.class, cu3.getChild(4).getClass());
    }

    @Test
    public void testMethodReferenceConfused() {
        ASTCompilationUnit compilationUnit = java.parseResource("MethodReferenceConfused.java", "10");
        ASTBlock firstBlock = compilationUnit.getFirstDescendantOfType(ASTBlock.class);
        Map<NameDeclaration, List<NameOccurrence>> declarations = firstBlock.getScope().getDeclarations();
        boolean foundVariable = false;
        for (Map.Entry<NameDeclaration, List<NameOccurrence>> declaration : declarations.entrySet()) {
            String varName = declaration.getKey().getImage();
            if ("someVarNameSameAsMethodReference".equals(varName)) {
                foundVariable = true;
                Assert.assertTrue("no usages expected", declaration.getValue().isEmpty());
            } else if ("someObject".equals(varName)) {
                Assert.assertEquals("1 usage expected", 1, declaration.getValue().size());
                Assert.assertEquals(6, declaration.getValue().get(0).getLocation().getBeginLine());
            }
        }
        Assert.assertTrue("Test setup wrong - variable 'someVarNameSameAsMethodReference' not found anymore!", foundVariable);
    }

    @Test
    public void testSwitchWithFallthrough() {
        ASTCompilationUnit compilationUnit = java.parseResource("SwitchWithFallthrough.java", "11");
        ASTSwitchStatement switchStatement = compilationUnit.getFirstDescendantOfType(ASTSwitchStatement.class);
        Assert.assertEquals(2, switchStatement.findChildrenOfType(ASTSwitchLabel.class).size());
    }

    @Test
    public void testSwitchStatements() {
        ASTCompilationUnit compilationUnit = java.parseResource("SwitchStatements.java", "11");
        ASTSwitchStatement switchStatement = compilationUnit.getFirstDescendantOfType(ASTSwitchStatement.class);
        Assert.assertEquals(2, switchStatement.findChildrenOfType(ASTSwitchLabel.class).size());
    }


    private static final String GENERICS_PROBLEM =
        "public class Test {\n public void test() {\n   String o = super.<String> doStuff(\"\");\n }\n}";

    private static final String ABSTRACT_METHOD_LEVEL_CLASS_DECL =
        "public class Test {\n"
            + "  void bar() {\n"
            + "   abstract class X { public abstract void f(); }\n"
            + "   class Y extends X { public void f() { new Y().f(); } }\n"
            + "  }\n"
            + "}";

    private static final String CAST_LOOKAHEAD_PROBLEM =
        "public class BadClass {\n  public Class foo() {\n    return (byte[].class);\n  }\n}";
}
