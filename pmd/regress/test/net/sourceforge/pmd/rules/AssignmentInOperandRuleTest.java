package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.rules.XPathRule;

public class AssignmentInOperandRuleTest extends RuleTst {

    private static final String TEST1 =
    "public class AssignmentInOperand1 {" + CPD.EOL +
    " public void bar() {" + CPD.EOL +
    "  int x = 2;" + CPD.EOL +
    "  if ((x = getX()) == 3) {" + CPD.EOL +
    "   System.out.println(\"3!\");" + CPD.EOL +
    "  }" + CPD.EOL +
    " }" + CPD.EOL +
    " private int getX() {" + CPD.EOL +
    "  return 3;" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST2 =
    "public class AssignmentInOperand2 {" + CPD.EOL +
    " public void bar() {" + CPD.EOL +
    "  if (false) {}" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST3 =
    "public class AssignmentInOperand3 {" + CPD.EOL +
    " public void bar() {" + CPD.EOL +
    "  if (false) {" + CPD.EOL +
    "   int x =2;" + CPD.EOL +
    "  }" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST4 =
    "public class AssignmentInOperand4 {" + CPD.EOL +
    " public void bar() {" + CPD.EOL +
    "  int x = 2;" + CPD.EOL +
    "  while ( (x = getX()) != 0 ) {}" + CPD.EOL +
    " }" + CPD.EOL +
    " private int getX() {return 2;}" + CPD.EOL +
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
