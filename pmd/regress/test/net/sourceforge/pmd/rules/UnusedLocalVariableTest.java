package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.rules.UnusedLocalVariableRule;

public class UnusedLocalVariableTest extends SimpleAggregatorTst {

    private UnusedLocalVariableRule rule;

    public void setUp() {
        rule = new UnusedLocalVariableRule();
        rule.setMessage("Avoid this stuff -> ''{0}''");
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
           new TestDescriptor(TEST8, "", 0, rule),
           new TestDescriptor(TEST9, "", 0, rule),
           new TestDescriptor(TEST10, "", 2, rule),
           new TestDescriptor(TEST11, "", 0, rule),
           new TestDescriptor(TEST12, "", 0, rule),
           new TestDescriptor(TEST13, "", 2, rule),
           new TestDescriptor(TEST14, "", 0, rule)
       });
    }

    private static final String TEST1 =
    "public class Foo {" + CPD.EOL +
    " public foo() {" + CPD.EOL +
    "   String fr = new String();" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + CPD.EOL +
    " public void method() {" + CPD.EOL +
    "  int x;" + CPD.EOL +
    " }" + CPD.EOL +
    "}" + CPD.EOL +
    "";

    private static final String TEST3 =
    "import java.util.*;" + CPD.EOL +
    "public class Foo {" + CPD.EOL +
    "    public Foo() {" + CPD.EOL +
    "       List a = new ArrayList();" + CPD.EOL +
    "    }" + CPD.EOL +
    "}" + CPD.EOL +
    "";

    private static final String TEST4 =
    "import java.util.*;" + CPD.EOL +
    "public class Foo {" + CPD.EOL +
    "    public Foo() {" + CPD.EOL +
    "       List a = new ArrayList();" + CPD.EOL +
    "       if (true == true) {" + CPD.EOL +
    "               a.size();" + CPD.EOL +
    "       }" + CPD.EOL +
    "    }" + CPD.EOL +
    "}" + CPD.EOL +
    "";

    private static final String TEST5 =
    "import java.util.*;" + CPD.EOL +
    "public class Foo {" + CPD.EOL +
    "static {" + CPD.EOL +
    "       String x;" + CPD.EOL +
    "}" + CPD.EOL +
    "}" + CPD.EOL +
    "";

    private static final String TEST6 =
    "public class Foo {" + CPD.EOL +
    " public int x;" + CPD.EOL +
    "}";

    private static final String TEST7 =
    "public class Foo {" + CPD.EOL +
    " public void foo() {" + CPD.EOL +
    "  for (int i=0;i<10; i++);" + CPD.EOL +
    "  for (int i=0;i<10; i++);" + CPD.EOL +
    " }" + CPD.EOL +
    "}";


    private static final String TEST8 =
    "public class Foo {" + CPD.EOL +
    " public void foo() {" + CPD.EOL +
    "  final String x = \"baf\";" + CPD.EOL +
    "   new Runnable() {" + CPD.EOL +
    "    public void run() {" + CPD.EOL +
    "     System.out.println(x);" + CPD.EOL +
    "    }" + CPD.EOL +
    "   };  " + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST9 =
    "public interface Foo {" + CPD.EOL +
    " public void foo();" + CPD.EOL +
    " public String bar();" + CPD.EOL +
    "}";

    private static final String TEST10 =
    "public class Foo {" + CPD.EOL +
    " public void foo() {" + CPD.EOL +
    "  String x = \"hi\";" + CPD.EOL +
    "  class Bar {" + CPD.EOL +
    "   public void buz() {" + CPD.EOL +
    "    String x = \"howdy\";" + CPD.EOL +
    "   }" + CPD.EOL +
    "  }" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST11 =
    "public class Foo {" + CPD.EOL +
    " public void foo() {" + CPD.EOL +
    "  for (int x = 0; ; ) { // USED" + CPD.EOL +
    "   x++;" + CPD.EOL +
    "  }" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST12 =
    "public class Foo {" + CPD.EOL +
    " public void foo() {" + CPD.EOL +
    "  final String x = \"hi\";" + CPD.EOL +
    "   new Runnable() {" + CPD.EOL +
    "    public void run() {" + CPD.EOL +
    "     x.toString();" + CPD.EOL +
    "    }" + CPD.EOL +
    "   };" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST13 =
    "public class Foo {" + CPD.EOL +
    " public void foo() {" + CPD.EOL +
    "  int x,y=0;" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST14 =
    "public class Foo {" + CPD.EOL +
    " public void bar() {" + CPD.EOL +
    "  try {" + CPD.EOL +
    "   //int x =2;" + CPD.EOL +
    "  } catch (RuntimeException e) {" + CPD.EOL +
    "  } catch (Exception e) {" + CPD.EOL +
    "  }" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

}
