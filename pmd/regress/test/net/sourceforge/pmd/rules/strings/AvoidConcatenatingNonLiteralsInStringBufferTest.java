/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules.strings;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class AvoidConcatenatingNonLiteralsInStringBufferTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws Exception {
        rule = findRule("strings", "AvoidConcatenatingNonLiteralsInStringBuffer");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
               new TestDescriptor(TEST1, "concatenating a literal to a method return value", 1, rule),
               new TestDescriptor(TEST2, "same as TEST1, but in SB constructor", 1, rule),
               new TestDescriptor(TEST3, "chained appends", 0, rule),
               new TestDescriptor(TEST4, "concatenating two literals in SB constructor", 0, rule),
               new TestDescriptor(TEST5, "concatenating two literals post-construction", 0, rule),
               new TestDescriptor(TEST6, "case where concatenation is not a child of a BlockStatement, but instead is a child of an ExplicitConstructorInvocation", 0, rule),
               new TestDescriptor(TEST7, "don't error out on array instantiation", 0, rule),
               //new TestDescriptor(TEST8, "usage of the StringBuffer constructor that takes an int", 0, rule),
               new TestDescriptor(TEST9, "nested", 0, rule),
       });
    }

   private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " private void baz() {" + PMD.EOL +
    "  StringBuffer sb = new StringBuffer();" + PMD.EOL +
    "  sb.append(\"hello\"+ world()); "+
    " }" + PMD.EOL +
    "}";

   private static final String TEST2 =
       "public class Foo {" + PMD.EOL +
       " private void baz() {" + PMD.EOL +
       "  StringBuffer sb = new StringBuffer(\"hello\"+ world());" + PMD.EOL +
       " }" + PMD.EOL +
       "}";

   private static final String TEST3 =
       "public class Foo {" + PMD.EOL +
       " private void baz() {" + PMD.EOL +
       "  StringBuffer sb = new StringBuffer();" + PMD.EOL +
       "  sb.append(\"hello\").append(world()); "+
       " }" + PMD.EOL +
       "}";

   private static final String TEST4 =
       "public class Foo {" + PMD.EOL +
       " private void baz() {" + PMD.EOL +
       "  StringBuffer sb = new StringBuffer(\"hello\"+ \"world\");" + PMD.EOL +
       " }" + PMD.EOL +
       "}";
   
   private static final String TEST5 =
       "public class Foo {" + PMD.EOL +
       " private void baz() {" + PMD.EOL +
       "  StringBuffer sb = new StringBuffer();" + PMD.EOL +
       "  sb.append(\"hello\"+\"world\"); "+
       " }" + PMD.EOL +
       "}";

   private static final String TEST6 =
       "public class Foo {" + PMD.EOL +
       " public Foo() {" + PMD.EOL +
       "  super(\"CauseMsg:\" + ex.getMessage(), ex); " + PMD.EOL +
       " }" + PMD.EOL +
       "}";

    private static final String TEST7 =
        "public class Foo {" + PMD.EOL +
        " public void bar() {" + PMD.EOL +
        "  int t[] = new int[x+y+1];" + PMD.EOL +
        " }" + PMD.EOL +
        "}";


   private static final String TEST8 =
       "public class Foo {" + PMD.EOL +
       " public void bar(int x) {" + PMD.EOL +
       "  StringBuffer buf = new StringBuffer(x);" + PMD.EOL +
       " }" + PMD.EOL +
       "}";

   private static final String TEST9 =
       "public class Foo {" + PMD.EOL +
       " public void bar(int x) {" + PMD.EOL +
       "  StringBuffer buf = new StringBuffer(x);" + PMD.EOL +
       " }" + PMD.EOL +
       "}";

}
