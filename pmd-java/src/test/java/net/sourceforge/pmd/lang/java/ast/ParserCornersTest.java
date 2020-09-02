/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.List;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sourceforge.pmd.lang.ast.impl.javacc.io.MalformedSourceException;
import net.sourceforge.pmd.lang.ast.test.BaseParsingHelper;
import net.sourceforge.pmd.lang.java.BaseJavaTreeDumpTest;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

public class ParserCornersTest extends BaseJavaTreeDumpTest {
    private final JavaParsingHelper java = JavaParsingHelper.WITH_PROCESSING.withResourceContext(getClass());
    private final JavaParsingHelper java4 = java.withDefaultVersion("1.4");
    private final JavaParsingHelper java7 = java.withDefaultVersion("1.7");
    private final JavaParsingHelper java8 = java.withDefaultVersion("1.8");
    private final JavaParsingHelper java5 = java.withDefaultVersion("1.7");
    @Rule
    public ExpectedException expect = ExpectedException.none();


    @Override
    public @NonNull BaseParsingHelper<?, ?> getParser() {
        return java4;
    }

    @Test
    public void testInvalidUnicodeEscape() {
        expect.expect(MalformedSourceException.class); // previously Error
        expect.expectMessage("Source format error at line 1, column 1: Invalid unicode escape");
        java.parse("\\u00k0");
    }

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
    public void testUnicodeEscapes() {
        java8.parse("public class Foo { String[] s = { \"Ven\\u00E4j\\u00E4\" }; }");
    }

    @Test
    public void testUnicodeEscapes2() {
        java.parse("\n"
                       + "public final class TimeZoneNames_zh_TW extends TimeZoneNamesBundle {\n"
                       + "\n"
                       + "        String ACT[] = new String[] {\"Acre \\u6642\\u9593\", \"ACT\",\n"
                       + "                                     \"Acre \\u590f\\u4ee4\\u6642\\u9593\", \"ACST\",\n"
                       + "                                     \"Acre \\u6642\\u9593\", \"ACT\"};"
                       + "}");
    }

    @Test
    public void testUnicodeEscapesInComment() {
        java.parse("class Foo {"
                       + "\n"
                       + "    /**\n"
                       + "     * The constant value of this field is the smallest value of type\n"
                       + "     * {@code char}, {@code '\\u005Cu0000'}.\n"
                       + "     *\n"
                       + "     * @since   1.0.2\n"
                       + "     */\n"
                       + "    public static final char MIN_VALUE = '\\u0000';\n"
                       + "\n"
                       + "    /**\n"
                       + "     * The constant value of this field is the largest value of type\n"
                       + "     * {@code char}, {@code '\\u005C\\uFFFF'}.\n"
                       + "     *\n"
                       + "     * @since   1.0.2\n"
                       + "     */\n"
                       + "    public static final char MAX_VALUE = '\\uFFFF';"
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
        doTest("ParserCornerCases", java5);
    }

    @Test
    public void testParsersCases17() {
        doTest("ParserCornerCases17", java7);
    }

    @Test
    public void testParsersCases18() {
        doTest("ParserCornerCases18", java8);
    }

    /**
     * Test for https://sourceforge.net/p/pmd/bugs/1333/
     */
    @Test
    public void testLambdaBug1333() {
        doTest("LambdaBug1333", java8);
    }

    @Test
    public void testLambdaBug1470() {
        doTest("LambdaBug1470", java8);
    }

    /**
     * Test for https://sourceforge.net/p/pmd/bugs/1355/
     */
    @Test
    public void emptyFileJustComment() {
        getParser().parse("// just a comment");
    }


    @Test
    public void testBug1429ParseError() {
        doTest("Bug1429", java8);
    }

    @Test
    public void testBug1530ParseError() {
        doTest("Bug1530", java8);
    }

    @Test
    public void testGitHubBug207() {
        doTest("GitHubBug207", java8);
    }

    @Test
    public void testLambda2783() {
        java8.parseResource("LambdaBug2783.java");
    }

    @Test
    public void testGitHubBug2767() {
        // PMD fails to parse an initializer block.
        // PMD 6.26.0 parses this code just fine.
        java.withDefaultVersion("15-preview")
            .parse("class Foo {\n"
                       + "    {final int I;}\n"
                       + "}\n");
    }

    @Test
    public void testBug206() {
        doTest("LambdaBug206", java8);
    }

    @Test
    public void testGitHubBug208ParseError() {
        doTest("GitHubBug208", java5);
    }

    @Test
    public void testGitHubBug309() {
        doTest("GitHubBug309", java8);
    }


    /**
     * Empty statements should be allowed.
     *
     * @see <a href="https://github.com/pmd/pmd/issues/378">github issue 378</a>
     */
    @Test
    public void testEmptyStatements1() {
        doTest("EmptyStmts1");
    }

    @Test
    public void testEmptyStatements2() {
        doTest("EmptyStmts2");
    }

    @Test
    public void testEmptyStatements3() {
        doTest("EmptyStmts3");
    }

    @Test
    @Ignore("this test depends on usage resolution")
    public void testMethodReferenceConfused() {
        ASTCompilationUnit compilationUnit = java.parseResource("MethodReferenceConfused.java", "10");
        Assert.assertNotNull(compilationUnit);
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
        doTest("SwitchWithFallthrough");
    }

    @Test
    public void testSwitchStatements() {
        doTest("SwitchStatements");
    }

    @Test
    public void testSynchronizedStatements() {
        doTest("SynchronizedStmts");
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
