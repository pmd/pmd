package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.rules.XPathRule;

public class AssignmentInOperandRuleTest extends RuleTst {

    private static final String TEST1 =
    "public class AssignmentInOperand1 {" + PMD.EOL +
    " public void bar() {" + PMD.EOL +
    "  int x = 2;" + PMD.EOL +
    "  if ((x = getX()) == 3) {" + PMD.EOL +
    "   System.out.println(\"3!\");" + PMD.EOL +
    "  }" + PMD.EOL +
    " }" + PMD.EOL +
    " private int getX() {" + PMD.EOL +
    "  return 3;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class AssignmentInOperand2 {" + PMD.EOL +
    " public void bar() {" + PMD.EOL +
    "  if (false) {}" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class AssignmentInOperand3 {" + PMD.EOL +
    " public void bar() {" + PMD.EOL +
    "  if (false) {" + PMD.EOL +
    "   int x =2;" + PMD.EOL +
    "  }" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST4 =
    "public class AssignmentInOperand4 {" + PMD.EOL +
    " public void bar() {" + PMD.EOL +
    "  int x = 2;" + PMD.EOL +
    "  while ( (x = getX()) != 0 ) {}" + PMD.EOL +
    " }" + PMD.EOL +
    " private int getX() {return 2;}" + PMD.EOL +
    "}";


    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//*[name()='WhileStatement' or name()='IfStatement'][Expression//AssignmentOperator]");
    }

    public void testSimple() throws Throwable {
        runTestFromString(TEST1, 1, rule);
    }
    public void testOK() throws Throwable {
        runTestFromString(TEST2, 0, rule);
    }
    public void testAssignmentInIfBody() throws Throwable {
        runTestFromString(TEST3, 0, rule);
    }
    public void testAssignmentInWhileLoop() throws Throwable {
        runTestFromString(TEST4, 1, rule);
    }
}
