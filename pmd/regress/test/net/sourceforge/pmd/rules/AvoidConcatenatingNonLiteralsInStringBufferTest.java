/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class AvoidConcatenatingNonLiteralsInStringBufferTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws Exception {
        rule = findRule("rulesets/strings.xml", "AvoidConcatenatingNonLiteralsInStringBuffer");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
               new TestDescriptor(TEST1, "TEST1", 1, rule),
               new TestDescriptor(TEST2, "TEST2", 1, rule),
               new TestDescriptor(TEST3, "TEST3", 0, rule),
               new TestDescriptor(TEST4, "TEST4", 0, rule),
               new TestDescriptor(TEST5, "TEST5", 0, rule),
               new TestDescriptor(TEST6, "case where append is not a child of a BlockStatement, but instead is a child of an ExplicitConstructorInvocation", 0, rule),
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

}
