/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class UnusedLocalVariableTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws Exception {
        rule = findRule("unusedcode", "UnusedLocalVariable");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "unused local with assignment", 1, rule),
           new TestDescriptor(TEST2, "unused local w/o assignment", 1, rule),
           new TestDescriptor(TEST3, "unused local in constructor", 1, rule),
           new TestDescriptor(TEST4, "local used on rhs", 0, rule),
           new TestDescriptor(TEST5, "unused local in static initializer", 1, rule),
           new TestDescriptor(TEST6, "unused field", 0, rule),
           new TestDescriptor(TEST7, "loop indexes are not unused locals", 0, rule),
           new TestDescriptor(TEST8, "local used in anonymous inner class", 0, rule),
           new TestDescriptor(TEST9, "two unused locals of same name, one in nested class", 2, rule),
           new TestDescriptor(TEST10, "two locals declared on same line", 2, rule),
           new TestDescriptor(TEST11, "an assignment does not a usage make", 1, rule),
           new TestDescriptor(TEST12, "a compound assignment operator doth a usage make", 0, rule),
           new TestDescriptor(TEST13, "assignment to a member field means used", 0, rule),
           new TestDescriptor(TEST14, "make sure scopes are working", 3, rule),
           new TestDescriptor(TEST15, "another scope test", 1, rule),
           new TestDescriptor(TEST16, "assignment to an array member will be treated as a usage", 0, rule),
           new TestDescriptor(TEST17, "local variable used in postfix expression as child of StatementExpression", 1, rule),
           new TestDescriptor(TEST18, "local variable used in postfix expression on right hand side", 0, rule),
           new TestDescriptor(TEST19, "local variable, object ref, public field of which is incremented via in postfix expression", 0, rule),
           new TestDescriptor(TEST20, "local used in right shift", 0, rule),
       });
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " void foo() {" + PMD.EOL +
    "   String fr = new String();" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " void foo() {" + PMD.EOL +
    "  int x;" + PMD.EOL +
    " }" + PMD.EOL +
    "}" + PMD.EOL +
    "";

    private static final String TEST3 =
    "import java.util.*;" + PMD.EOL +
    "public class Foo {" + PMD.EOL +
    "    public Foo() {" + PMD.EOL +
    "       List a = new ArrayList();" + PMD.EOL +
    "    }" + PMD.EOL +
    "}" + PMD.EOL +
    "";

    private static final String TEST4 =
    "import java.util.*;" + PMD.EOL +
    "public class Foo {" + PMD.EOL +
    "    public Foo() {" + PMD.EOL +
    "       List a = new ArrayList();" + PMD.EOL +
    "       if (true) {" + PMD.EOL +
    "               a.size();" + PMD.EOL +
    "       }" + PMD.EOL +
    "    }" + PMD.EOL +
    "}";

    private static final String TEST5 =
    "import java.util.*;" + PMD.EOL +
    "public class Foo {" + PMD.EOL +
    " static {" + PMD.EOL +
    "  String x;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST6 =
    "public class Foo {" + PMD.EOL +
    " public int x;" + PMD.EOL +
    "}";

    private static final String TEST7 =
    "public class Foo {" + PMD.EOL +
    " void foo() {" + PMD.EOL +
    "  for (int i=0;i<10; i++);" + PMD.EOL +
    "  for (int i=0;i<10; i++);" + PMD.EOL +
    " }" + PMD.EOL +
    "}";


    private static final String TEST8 =
    "public class Foo {" + PMD.EOL +
    " public void foo() {" + PMD.EOL +
    "  final int x = 2;" + PMD.EOL +
    "   new Runnable() {" + PMD.EOL +
    "    public void run() {" + PMD.EOL +
    "     System.out.println(x);" + PMD.EOL +
    "    }" + PMD.EOL +
    "   };  " + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST9=
    "public class Foo {" + PMD.EOL +
    " void foo() {" + PMD.EOL +
    "  int x = 2;" + PMD.EOL +
    "  class Bar {" + PMD.EOL +
    "   void buz() {" + PMD.EOL +
    "    int x = 4;" + PMD.EOL +
    "   }" + PMD.EOL +
    "  }" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST10 =
    "public class Foo {" + PMD.EOL +
    " void foo() {" + PMD.EOL +
    "  int x,y=0;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST11 =
    "public class Foo {" + PMD.EOL +
    " void bar() {" + PMD.EOL +
    "   int x;" + PMD.EOL +
    "   x = 4;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST12 =
    "public class Foo {" + PMD.EOL +
    " void bar() {" + PMD.EOL +
    "   int x = 0;" + PMD.EOL +
    "   x += 2;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST13 =
    "public class Foo {" + PMD.EOL +
    " void bar() {" + PMD.EOL +
    "   Bar b = new Bar();" + PMD.EOL +
    "   b.buz = 2;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST14 =
    "public class Foo {" + PMD.EOL +
    " void bar() {" + PMD.EOL +
    "   int x = 2;" + PMD.EOL +
    "   if (true) {int y =2;int j =3;} " + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST15 =
    "public class Foo {" + PMD.EOL +
    " void bar() {" + PMD.EOL +
    "   int x = 2;" + PMD.EOL +
    "   if (true) {int y =2;foo(y);int j =3;foo(j);} " + PMD.EOL +
    " }" + PMD.EOL +
    " void bar2() {" + PMD.EOL +
    "   int x = 2;" + PMD.EOL +
    "   fiddle(x);" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST16 =
    "public class Foo {" + PMD.EOL +
    " void bar() {" + PMD.EOL +
    "   int[] x = {2};" + PMD.EOL +
    "   x[1] = 2; " + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST17 =
    "public class Foo {" + PMD.EOL +
    " void bar() {" + PMD.EOL +
    "   int x = 2;" + PMD.EOL +
    "   x++; " + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST18 =
    "public class Foo {" + PMD.EOL +
    " void bar() {" + PMD.EOL +
    "   int x = 2;" + PMD.EOL +
    "   foo(x++); " + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST19 =
    "public class Foo {" + PMD.EOL +
    " void bar() {" + PMD.EOL +
    "   Baz b = getBaz();" + PMD.EOL +
    "   b.x++; " + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST20 =
    "public class Foo {" + PMD.EOL +
    " void bar() {" + PMD.EOL +
    "   int x = 2;" + PMD.EOL +
    "   int y = 4 >> x;" + PMD.EOL +
    "   foo(y);" + PMD.EOL +
    " }" + PMD.EOL +
    "}";
}
