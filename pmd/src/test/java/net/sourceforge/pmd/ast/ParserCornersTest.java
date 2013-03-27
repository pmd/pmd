/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.ast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.java.ast.ParseException;
import net.sourceforge.pmd.testframework.ParserTst;

import org.apache.commons.io.IOUtils;
import org.junit.Test;


public class ParserCornersTest extends ParserTst {

    @Test
    public final void testGetFirstASTNameImageNull() throws Throwable {
        parseJava14(ABSTRACT_METHOD_LEVEL_CLASS_DECL);
    }

    @Test
    public final void testCastLookaheadProblem() throws Throwable {
        parseJava14(CAST_LOOKAHEAD_PROBLEM);
    }
    
    /**
     * Tests a specific generic notation for calling methods.
     * See: https://jira.codehaus.org/browse/MPMD-139
     */
    @Test
    public void testGenericsProblem() {
    	parseJava15(GENERICS_PROBLEM);
    	parseJava17(GENERICS_PROBLEM);
    }
    
    @Test
    public void testParsersCases() {
    	String test15 = readAsString("/net/sourceforge/pmd/ast/ParserCornerCases.java");
    	parseJava15(test15);
    	
    	String test17 = readAsString("/net/sourceforge/pmd/ast/ParserCornerCases17.java");
    	parseJava17(test17);
    }

    @Test
    public void testMultipleExceptionCatching() {
    	String code = "public class Foo { public void bar() { "
    			+ "try { System.out.println(); } catch (RuntimeException | IOException e) {} } }";
    	try {
    		parseJava15(code);
    		fail("Expected exception");
    	} catch (ParseException e) {
    		assertEquals("Cannot catch multiple exceptions when running in JDK inferior to 1.7 mode!", e.getMessage());
    	}

    	try {
    		parseJava17(code);
    		// no exception expected
    	} catch (ParseException e) {
    		fail();
    	}
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
    
    private static final String GENERICS_PROBLEM =
    		"public class Test {" + PMD.EOL +
    		" public void test() {" + PMD.EOL +
    		"   String o = super.<String> doStuff(\"\");" + PMD.EOL +
    		" }" + PMD.EOL +
    		"}";

    private static final String ABSTRACT_METHOD_LEVEL_CLASS_DECL =
            "public class Test {" + PMD.EOL +
            "  void bar() {" + PMD.EOL +
            "   abstract class X { public abstract void f(); }" + PMD.EOL +
            "   class Y extends X { public void f() {" + PMD.EOL +
            "    new Y().f();" + PMD.EOL +
            "   }}" + PMD.EOL +
            "  }" + PMD.EOL +
            "}";

    private static final String CAST_LOOKAHEAD_PROBLEM =
        "public class BadClass {" + PMD.EOL +
        "  public Class foo() {" + PMD.EOL +
        "    return (byte[].class);" + PMD.EOL +
        "  }" + PMD.EOL +
        "}";

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(ParserCornersTest.class);
    }
}
