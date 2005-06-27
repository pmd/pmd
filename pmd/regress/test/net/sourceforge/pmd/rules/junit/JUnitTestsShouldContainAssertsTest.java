/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules.junit;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class JUnitTestsShouldContainAssertsTest extends SimpleAggregatorTst {
    private Rule rule;
    
    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("rulesets/junit.xml", "JUnitTestsShouldIncludeAssert");
    }
    public void testAll() throws Throwable {

       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "Contains assert", 0, rule),
           new TestDescriptor(TEST2, "Missing assert", 1, rule),
           new TestDescriptor(TEST3, "All ok", 0, rule),
           new TestDescriptor(TEST4, "Two wrong", 2, rule),
           new TestDescriptor(TEST5, "Contains fail", 0, rule),
           new TestDescriptor(TEST6, "One wrong", 1, rule),
           new TestDescriptor(TEST7, "Skip interfaces", 0, rule),
           new TestDescriptor(TEST8, "Skip abstract methods", 0, rule),
           new TestDescriptor(TEST9, "Another fail() case", 0, rule),
           new TestDescriptor(TEST10, "BUG 1105633 - False +: JUnit testcases could have fail() instead of assert", 0, rule),
		   new TestDescriptor(BUG_1146116, "BUG 1146116 PMDException with inner interfaces", 0, rule),
		   new TestDescriptor(TEST12, "skip static test methods", 0, rule),
       });
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " public void test1() {" + PMD.EOL +
    "  assertEquals(\"1 == 1\", 1, 1);" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " public void test1() {" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class Foo {" + PMD.EOL +
    " public void setUp() {" + PMD.EOL +
    " }" + PMD.EOL +
    " public void test1() {" + PMD.EOL +
    "  assertTrue(\"foo\", \"foo\".equals(\"foo\"));" + PMD.EOL +
    " }" + PMD.EOL +
    " public void test2() {" + PMD.EOL +
    "  assertEquals(\"foo\", \"foo\");" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST4 =
        "public class Foo {" + PMD.EOL +
        " public void setUp() {" + PMD.EOL +
        " }" + PMD.EOL +
        " public void test1() {" + PMD.EOL +
        " 	int a;" + PMD.EOL +
        " 	callMethod(a);" + PMD.EOL +
        " }" + PMD.EOL +
        " public void test2() {" + PMD.EOL +
        " }" + PMD.EOL +
        "}";


    private static final String TEST5 =
        "public class Foo {" + PMD.EOL +
        " public void test1() {" + PMD.EOL +
        "  fail(\"1 == 1\");" + PMD.EOL +
        " }" + PMD.EOL +
        "}";


    private static final String TEST6 =
        "public class Foo {" + PMD.EOL +
        " public void setUp() {" + PMD.EOL +
        " }" + PMD.EOL +
        " public void test1() {" + PMD.EOL +
        " 	int a;" + PMD.EOL +
        " 	callMethod(a);" + PMD.EOL +
        " }" + PMD.EOL +
        " public void test2() {" + PMD.EOL +
        " 	fail();" + PMD.EOL +
        " }" + PMD.EOL +
        "}";

    private static final String TEST7 =
        "public interface Foo {" + PMD.EOL +
        " public void setUp() throws Exception;" + PMD.EOL +
        "}";

    private static final String TEST8 =
        "public class Foo {" + PMD.EOL +
        " public abstract void setUp() throws Exception;" + PMD.EOL +
        "}";

    private static final String TEST9 =
        "public abstract class AbstractAggregateCreator {" + PMD.EOL + 
            "    public abstract int getType();" + PMD.EOL + 
            "    public abstract ProfileAggregate create(DatabaseTransaction db," + PMD.EOL + 
            "        DailyProfileList profiles, ProfileType type, ProfileStatus status)" + PMD.EOL + 
            "        throws VixenException;" + PMD.EOL + 
            "}";
    
    private static final String TEST10 =
        "public class FooTest {" +  PMD.EOL +
        " public void testNPEThrown() {" +  PMD.EOL +
        "  try {" + PMD.EOL + 
        "   methodCall(null);" + PMD.EOL + 
        "   fail(\"Expected NullPointerException to be thrown.\");" + PMD.EOL + 
        "  } catch (NullPointerException npe) {" + PMD.EOL + 
        "   // Caught expected exception" + PMD.EOL + 
        "  }" +  PMD.EOL +
        " }" +  PMD.EOL +
        "}";
    
    private static final String BUG_1146116 = "package at.herold.anthilltest;" + PMD.EOL +  
	"public class TestJunitRuleException {" +  PMD.EOL + 
	"   interface I1 { " +  PMD.EOL + 
	"      public void meth(); // this is ok" + PMD.EOL +  
	"   }" + PMD.EOL +  
	"   interface I2 {" +  PMD.EOL + 
	"      public void meth() throws Exception; // this causes PMDException" + PMD.EOL +  
	"   }" + PMD.EOL +  
	"}";

    private static final String TEST12 =
	"public class Foo {" +  PMD.EOL +
	" public static void testfoo() {}" + PMD.EOL +
	"}";

}
