package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.UnusedLocalVariableRule;
import net.sourceforge.pmd.cpd.CPD;

public class UnusedLocalVariableTest extends RuleTst {

    private static final String TEST1 =
    "import java.io.*;" + CPD.EOL +
    "public class UnusedLocal1 {" + CPD.EOL +
    " public foo() {" + CPD.EOL +
    "  try {" + CPD.EOL +
    "   FileReader fr = new FileReader(\"/dev/null\");" + CPD.EOL +
    "   } catch (Exception e) {}" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST2 =
    "public class UnusedLocal2 {" + CPD.EOL +
    " public void method() {" + CPD.EOL +
    "  int x;" + CPD.EOL +
    " }" + CPD.EOL +
    "}" + CPD.EOL +
    "";

    private static final String TEST3 =
    "import java.util.*;" + CPD.EOL +
    "public class UnusedLocal3 {" + CPD.EOL +
    "    public UnusedLocal3() {" + CPD.EOL +
    "       List a = new ArrayList();" + CPD.EOL +
    "    }" + CPD.EOL +
    "}" + CPD.EOL +
    "";

    private static final String TEST4 =
    "import java.util.*;" + CPD.EOL +
    "public class UnusedLocal4 {" + CPD.EOL +
    "    public UnusedLocal4() {" + CPD.EOL +
    "       List a = new ArrayList();" + CPD.EOL +
    "       if (true == true) {" + CPD.EOL +
    "               a.size();" + CPD.EOL +
    "       }" + CPD.EOL +
    "    }" + CPD.EOL +
    "}" + CPD.EOL +
    "";

    private static final String TEST5 =
    "import java.util.*;" + CPD.EOL +
    "public class UnusedLocal5 {" + CPD.EOL +
    "static {" + CPD.EOL +
    "       String x;" + CPD.EOL +
    "}" + CPD.EOL +
    "}" + CPD.EOL +
    "";

    private static final String TEST6 =
    "public class UnusedLocal6 {" + CPD.EOL +
    " public int x;" + CPD.EOL +
    "}";

    private static final String TEST7 =
    "public class UnusedLocal7 {" + CPD.EOL +
    " public void foo() {" + CPD.EOL +
    "  for (int i=0;i<10; i++);" + CPD.EOL +
    "  for (int i=0;i<10; i++);" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST8 =
    "public class UnusedLocal8 {" + CPD.EOL +
    "  String foo = System.getProperty(\"line.separator\", \"\\n\");" + CPD.EOL +
    "}";

    private static final String TEST9 =
    "public class UnusedLocal9 {" + CPD.EOL +
    " public void foo() {" + CPD.EOL +
    "  final String x = \"baf\";" + CPD.EOL +
    "   new Runnable() {" + CPD.EOL +
    "    public void run() {" + CPD.EOL +
    "     System.out.println(x);" + CPD.EOL +
    "    }" + CPD.EOL +
    "   };  " + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST10 =
    "public interface UnusedLocal10 {" + CPD.EOL +
    " public void foo();" + CPD.EOL +
    " public String bar();" + CPD.EOL +
    "}";

    private static final String TEST11 =
    "public class UnusedLocal11 {" + CPD.EOL +
    " public void foo() {" + CPD.EOL +
    "  String x = \"hi\";" + CPD.EOL +
    "  class Bar {" + CPD.EOL +
    "   public void buz() {" + CPD.EOL +
    "    String x = \"howdy\";" + CPD.EOL +
    "   }" + CPD.EOL +
    "  }" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST12 =
    "public class UnusedLocal12 {" + CPD.EOL +
    " public void foo() {" + CPD.EOL +
    "  for (int x = 0; ; ) { // USED" + CPD.EOL +
    "   x++;" + CPD.EOL +
    "  }" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST13 =
    "public class UnusedLocal13 {" + CPD.EOL +
    " public void foo() {" + CPD.EOL +
    "  final String x = \"hi\";" + CPD.EOL +
    "   new Runnable() {" + CPD.EOL +
    "    public void run() {" + CPD.EOL +
    "     x.toString();" + CPD.EOL +
    "    }" + CPD.EOL +
    "   };" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST14 =
    "public class UnusedLocal14 {" + CPD.EOL +
    " public void foo() {" + CPD.EOL +
    "  int x,y=0;" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST15 =
    "public class UnusedLocal15 {" + CPD.EOL +
    " public void bar() {" + CPD.EOL +
    "  try {" + CPD.EOL +
    "   //int x =2;" + CPD.EOL +
    "  } catch (RuntimeException e) {" + CPD.EOL +
    "  } catch (Exception e) {" + CPD.EOL +
    "  }" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private UnusedLocalVariableRule rule;

    public void setUp() {
        rule = new UnusedLocalVariableRule();
        rule.setMessage("Avoid this stuff -> ''{0}''");
    }

    public void test1() throws Throwable {
        runTestFromString(TEST1, 1, rule);
    }
    public void test2() throws Throwable {
        runTestFromString(TEST2, 1, rule);
    }
    public void test3() throws Throwable {
        runTestFromString(TEST3, 1, rule);
    }
    public void test4() throws Throwable {
        runTestFromString(TEST4, 0, rule);
    }
    public void test5() throws Throwable {
        runTestFromString(TEST5, 1, rule);
    }
    public void test6() throws Throwable {
        runTestFromString(TEST6, 0, rule);
    }
    public void test7() throws Throwable {
        runTestFromString(TEST7, 0, rule);
    }
    public void test8() throws Throwable {
        runTestFromString(TEST8, 0, rule);
    }
    public void test9() throws Throwable {
        runTestFromString(TEST9, 0, rule);
    }
    public void test10() throws Throwable {
        runTestFromString(TEST10, 0, rule);
    }
    public void test11() throws Throwable {
        runTestFromString(TEST11, 2, rule);
    }
    public void test12() throws Throwable {
        runTestFromString(TEST12, 0, rule);
    }
    public void test13() throws Throwable {
        runTestFromString(TEST13, 0, rule);
    }
    public void test14() throws Throwable {
        runTestFromString(TEST14, 2, rule);
    }
    public void test15() throws Throwable {
        runTestFromString(TEST15, 0, rule);
    }
}
