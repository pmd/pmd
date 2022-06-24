/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.collection.IsEmptyCollection.empty;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.TokenMgrError;
import net.sourceforge.pmd.lang.ast.test.BaseParsingHelper;
import net.sourceforge.pmd.lang.java.BaseJavaTreeDumpTest;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.types.AstTestUtil;

public class ParserCornersTest extends BaseJavaTreeDumpTest {
    private final JavaParsingHelper java = JavaParsingHelper.DEFAULT.withResourceContext(getClass());

    private final JavaParsingHelper java4 = java.withDefaultVersion("1.4");
    private final JavaParsingHelper java5 = java.withDefaultVersion("1.5");
    private final JavaParsingHelper java7 = java.withDefaultVersion("1.7");
    private final JavaParsingHelper java8 = java.withDefaultVersion("1.8");
    private final JavaParsingHelper java9 = java.withDefaultVersion("9");

    @Rule
    public ExpectedException expect = ExpectedException.none();


    @Override
    public @NonNull BaseParsingHelper<?, ?> getParser() {
        return java4;
    }

    @Test
    public void testInvalidUnicodeEscape() {
        expect.expect(TokenMgrError.class); // previously Error
        expect.expectMessage("Lexical error in file x/filename.java at line 1, column 2.  Encountered: Invalid unicode escape");
        java.parse("\\u00k0", null, "x/filename.java");
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
    public final void testGetFirstASTNameImageNull() {
        java4.parse("public class Test {\n"
            + "  void bar() {\n"
            + "   abstract class X { public abstract void f(); }\n"
            + "   class Y extends X { public void f() { new Y().f(); } }\n"
            + "  }\n"
            + "}");
    }

    @Test
    public final void testCastLookaheadProblem() {
        java4.parse("public class BadClass {\n  public Class foo() {\n    return (byte[].class);\n  }\n}");
    }

    @Test
    public final void testTryWithResourcesConcise() {
        // https://github.com/pmd/pmd/issues/3697
        java9.parse("import java.io.InputStream;\n"
                        + "public class Foo {\n"
                        + "    public InputStream in;\n"
                        + "    public void bar() {\n"
                        + "        Foo f = this;\n"
                        + "        try (f.in) {\n"
                        + "        }\n"
                        + "    }\n"
                        + "}");
    }

    @Test
    public final void testTryWithResourcesThis() {
        // https://github.com/pmd/pmd/issues/3697
        java9.parse("import java.io.InputStream;\n"
                        + "public class Foo {\n"
                        + "    public InputStream in;\n"
                        + "    public void bar() {\n"
                        + "        try (this.in) {\n"
                        + "        }\n"
                        + "    }\n"
                        + "}");
    }

    /**
     * Tests a specific generic notation for calling methods. See:
     * https://jira.codehaus.org/browse/MPMD-139
     */
    @Test
    public void testGenericsProblem() {
        String code = "public class Test {\n"
            + " public void test() {\n"
            + "   String o = super.<String> doStuff(\"\");\n"
            + " }\n"
            + "}";
        java5.parse(code);
        java7.parse(code);
    }

    @Test
    public void testUnicodeIndent() {
        // https://github.com/pmd/pmd/issues/3423
        java7.parseResource("UnicodeIdentifier.java");
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
        java.withDefaultVersion("16")
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

    @Test(timeout = 30000)
    public void testInfiniteLoopInLookahead() {
        expect.expect(ParseException.class);
        // https://github.com/pmd/pmd/issues/3117
        java8.parseResource("InfiniteLoopInLookahead.java");
    }

    @Test
    public void stringConcatentationShouldNotBeCast() {
        // https://github.com/pmd/pmd/issues/1484
        String code = "public class Test {\n" + "    public static void main(String[] args) {\n"
            + "        System.out.println(\"X\" + (args) + \"Y\");\n" + "    }\n" + "}";
        Assert.assertEquals(0, java8.parse(code).descendants(ASTCastExpression.class).count());
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
    public void testMethodReferenceConfused() {
        ASTCompilationUnit ast = java.parseResource("MethodReferenceConfused.java", "10");
        ASTVariableDeclaratorId varWithMethodName = AstTestUtil.varId(ast, "method");
        ASTVariableDeclaratorId someObject = AstTestUtil.varId(ast, "someObject");

        assertThat(varWithMethodName.getLocalUsages(), empty());
        assertThat(someObject.getLocalUsages(), hasSize(1));
        ASTNamedReferenceExpr usage = someObject.getLocalUsages().get(0);
        assertThat(usage.getParent(), instanceOf(ASTCastExpression.class));
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


    @Test
    public void testGithubBug3101UnresolvedTypeParams() {
        java.parseResource("GitHubBug3101.java");
    }
}
