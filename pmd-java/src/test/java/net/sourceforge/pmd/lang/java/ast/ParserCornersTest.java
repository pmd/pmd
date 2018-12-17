/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static net.sourceforge.pmd.lang.java.ParserTstUtil.parseJava14;
import static net.sourceforge.pmd.lang.java.ParserTstUtil.parseJava15;
import static net.sourceforge.pmd.lang.java.ParserTstUtil.parseJava17;
import static net.sourceforge.pmd.lang.java.ParserTstUtil.parseJava18;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.java.ParserTstUtil;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

public class ParserCornersTest {

    /**
     * #1107 PMD 5.0.4 couldn't parse call of parent outer java class method
     * from inner class.
     */
    @Test
    public void testInnerOuterClass() {
        parseJava17("/**\n" + " * @author azagorulko\n" + " *\n" + " */\n"
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
        parseJava18("public class PMDExceptionTest {\n"
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
        parseJava14(ABSTRACT_METHOD_LEVEL_CLASS_DECL);
    }

    @Test
    public final void testCastLookaheadProblem() {
        parseJava14(CAST_LOOKAHEAD_PROBLEM);
    }

    /**
     * Tests a specific generic notation for calling methods. See:
     * https://jira.codehaus.org/browse/MPMD-139
     */
    @Test
    public void testGenericsProblem() {
        parseJava15(GENERICS_PROBLEM);
        parseJava17(GENERICS_PROBLEM);
    }

    @Test
    public void testParsersCases15() {
        String test15 = readAsString("/net/sourceforge/pmd/ast/ParserCornerCases.java");
        parseJava15(test15);
    }

    @Test
    public void testParsersCases17() {
        String test17 = readAsString("/net/sourceforge/pmd/ast/ParserCornerCases17.java");
        parseJava17(test17);
    }

    @Test
    public void testParsersCases18() throws Exception {
        String test18 = readAsString("/net/sourceforge/pmd/ast/ParserCornerCases18.java");
        ASTCompilationUnit cu = parseJava18(test18);

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
        parseJava18("final class Bug1333 {\n"
                + "    private static final Logger LOG = LoggerFactory.getLogger(Foo.class);\n" + "\n"
                + "    public void deleteDirectoriesByNamePattern() {\n"
                + "        delete(path -> deleteDirectory(path));\n" + "    }\n" + "\n"
                + "    private void delete(Consumer<? super String> consumer) {\n"
                + "        LOG.debug(consumer.toString());\n" + "    }\n" + "\n"
                + "    private void deleteDirectory(String path) {\n" + "        LOG.debug(path);\n" + "    }\n" + "}");
    }

    @Test
    public void testLambdaBug1470() {
        String code = readAsString("LambdaBug1470.java");
        parseJava18(code);
    }

    /**
     * Test for https://sourceforge.net/p/pmd/bugs/1355/
     */
    @Test
    public void emptyFileJustComment() {
        parseJava18("// just a comment");
    }

    @Test
    public void testMultipleExceptionCatching() {
        String code = "public class Foo { public void bar() { "
                + "try { System.out.println(); } catch (RuntimeException | IOException e) {} } }";
        try {
            parseJava15(code);
            fail("Expected exception");
        } catch (ParseException e) {
            assertEquals(
                    "Line 1, Column 94: Cannot catch multiple exceptions when running in JDK inferior to 1.7 mode!",
                    e.getMessage());
        }

        try {
            parseJava17(code);
            // no exception expected
        } catch (ParseException e) {
            fail();
        }
    }

    @Test
    public void testBug1429ParseError() {
        String c = readAsString("Bug1429.java");
        parseJava18(c);
    }

    @Test
    public void testBug1530ParseError() {
        String c = readAsString("Bug1530.java");
        parseJava18(c);
    }
    
    @Test
    public void testGitHubBug207() {
        String c = readAsString("GitHubBug207.java");
        parseJava18(c);
    }

    @Test
    public void testBug206() throws Exception {
        String code = "public @interface Foo {" + PMD.EOL
            + "static final ThreadLocal<Interner<Integer>> interner =" + PMD.EOL
            + "    ThreadLocal.withInitial(Interners::newStrongInterner);" + PMD.EOL
            + "}";
        parseJava18(code);
    }

    @Test
    public void testGitHubBug208ParseError() {
        String c = readAsString("GitHubBug208.java");
        parseJava15(c);
    }
    
    @Test
    public void testGitHubBug257NonExistingCast() throws Exception {
        String code = "public class Test {" + PMD.EOL
                + "     public static void main(String[] args) {" + PMD.EOL
                + "         double a = 4.0;" + PMD.EOL
                + "         double b = 2.0;" + PMD.EOL
                + "         double result = Math.sqrt((a)   - b);" + PMD.EOL
                + "         System.out.println(result);" + PMD.EOL
                + "     }" + PMD.EOL
                + "}";
        ASTCompilationUnit compilationUnit = parseJava15(code);
        assertEquals("A cast was found when none expected", 0, compilationUnit.findDescendantsOfType(ASTCastExpression.class).size());
    }

    @Test
    public void testGitHubBug309() throws Exception {
        String code = readAsString("GitHubBug309.java");
        ASTCompilationUnit compilationUnit = parseJava18(code);
        assertNotNull(compilationUnit);
    }

    /**
     * This triggered bug #1484 UnusedLocalVariable - false positive -
     * parenthesis
     * 
     * @throws Exception
     */
    @Test
    public void stringConcatentationShouldNotBeCast() throws Exception {
        String code = "public class Test {\n" + "    public static void main(String[] args) {\n"
                + "        System.out.println(\"X\" + (args) + \"Y\");\n" + "    }\n" + "}";
        ASTCompilationUnit cu = parseJava18(code);
        Assert.assertEquals(0, cu.findDescendantsOfType(ASTCastExpression.class).size());
    }

    /**
     * Empty statements should be allowed.
     * @throws Exception
     * @see <a href="https://github.com/pmd/pmd/issues/378">github issue 378</a>
     */
    @Test
    public void testParseEmptyStatements() throws Exception {
        String code = "import a;;import b; public class Foo {}";
        ASTCompilationUnit cu = parseJava18(code);
        assertNotNull(cu);
        Assert.assertEquals(ASTEmptyStatement.class, cu.jjtGetChild(1).getClass());

        String code2 = "package c;; import a; import b; public class Foo {}";
        ASTCompilationUnit cu2 = parseJava18(code2);
        assertNotNull(cu2);
        Assert.assertEquals(ASTEmptyStatement.class, cu2.jjtGetChild(1).getClass());

        String code3 = "package c; import a; import b; public class Foo {};";
        ASTCompilationUnit cu3 = parseJava18(code3);
        assertNotNull(cu3);
        Assert.assertEquals(ASTEmptyStatement.class, cu3.jjtGetChild(4).getClass());
    }

    @Test
    public void testMethodReferenceConfused() throws Exception {
        String code = readAsString("MethodReferenceConfused.java");
        ASTCompilationUnit compilationUnit = ParserTstUtil.parseAndTypeResolveJava("10", code);
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

    private String readAsString(String resource) {
        try (InputStream in = ParserCornersTest.class.getResourceAsStream(resource)) {
            return IOUtils.toString(in, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final String GENERICS_PROBLEM = "public class Test {" + PMD.EOL + " public void test() {" + PMD.EOL
            + "   String o = super.<String> doStuff(\"\");" + PMD.EOL + " }" + PMD.EOL + "}";

    private static final String ABSTRACT_METHOD_LEVEL_CLASS_DECL = "public class Test {" + PMD.EOL + "  void bar() {"
            + PMD.EOL + "   abstract class X { public abstract void f(); }" + PMD.EOL
            + "   class Y extends X { public void f() {" + PMD.EOL + "    new Y().f();" + PMD.EOL + "   }}" + PMD.EOL
            + "  }" + PMD.EOL + "}";

    private static final String CAST_LOOKAHEAD_PROBLEM = "public class BadClass {" + PMD.EOL + "  public Class foo() {"
            + PMD.EOL + "    return (byte[].class);" + PMD.EOL + "  }" + PMD.EOL + "}";
}
