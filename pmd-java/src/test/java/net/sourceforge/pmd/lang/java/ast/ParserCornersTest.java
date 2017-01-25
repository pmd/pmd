/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.ast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.java.ParserTst;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

public class ParserCornersTest extends ParserTst {

    /**
     * #1107 PMD 5.0.4 couldn't parse call of parent outer java class method
     * from inner class
     * 
     * @throws Exception
     *             any error
     */
    @Test
    public void testInnerOuterClass() throws Exception {
        parseJava17("/**\n" + " * @author azagorulko\n" + " *\n" + " */\n"
                + "public class TestInnerClassCallsOuterParent {\n" + "\n" + "    public void test() {\n"
                + "        new Runnable() {\n" + "            @Override\n" + "            public void run() {\n"
                + "                TestInnerClassCallsOuterParent.super.toString();\n" + "            }\n"
                + "        };\n" + "    }\n" + "}\n");
    }

    @Test
    public final void testGetFirstASTNameImageNull() throws Throwable {
        parseJava14(ABSTRACT_METHOD_LEVEL_CLASS_DECL);
    }

    @Test
    public final void testCastLookaheadProblem() throws Throwable {
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

        Assert.assertEquals(13, cu.findChildNodesWithXPath("//FormalParameter").size());
        Assert.assertEquals(4, cu.findChildNodesWithXPath("//FormalParameter[@ExplicitReceiverParameter='true']").size());
        Assert.assertEquals(9, cu.findChildNodesWithXPath("//FormalParameter[@ExplicitReceiverParameter='false']").size());
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
    public void testLambdaBug1470() throws Exception {
        String code = IOUtils.toString(ParserCornersTest.class.getResourceAsStream("LambdaBug1470.java"), "UTF-8");
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
    public void testBug1429ParseError() throws Exception {
        String c = IOUtils.toString(this.getClass().getResourceAsStream("Bug1429.java"));
        parseJava18(c);
    }
    
    @Test
    public void testBug1530ParseError() throws Exception {
        String c = IOUtils.toString(this.getClass().getResourceAsStream("Bug1530.java"));
        parseJava18(c);
    }
    
    @Test
    public void testGitHubBug207() throws Exception {
        String c = IOUtils.toString(this.getClass().getResourceAsStream("GitHubBug207.java"));
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

    /**
     * This triggered bug #1484 UnusedLocalVariable - false positive - parenthesis
     * @throws Exception
     */
    @Test
    public void stringConcatentationShouldNotBeCast() throws Exception {
        String code = "public class Test {\n" + 
                "    public static void main(String[] args) {\n" + 
                "        System.out.println(\"X\" + (args) + \"Y\");\n" + 
                "    }\n" + 
                "}";
        ASTCompilationUnit cu = parseJava18(code);
        Assert.assertEquals(0, cu.findDescendantsOfType(ASTCastExpression.class).size());
    }

    private String readAsString(String resource) {
        InputStream in = ParserCornersTest.class.getResourceAsStream(resource);
        try {
            return IOUtils.toString(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(in);
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
