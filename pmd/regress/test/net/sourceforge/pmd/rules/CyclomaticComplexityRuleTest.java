package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.rules.CyclomaticComplexityRule;

import java.util.Iterator;

public class CyclomaticComplexityRuleTest extends RuleTst {

    private CyclomaticComplexityRule rule = new CyclomaticComplexityRule();

    public void setUp() {
        rule.setMessage("The {0} ''{1}'' has a Cyclomatic Complexity of {2}.");
    }

    public void testOneMethod() throws Throwable {
        rule.addProperty("reportLevel", "1");
        Report report = new Report();
        runTestFromString(TEST1, rule, report);
        Iterator i = report.iterator();
        RuleViolation rv = (RuleViolation) i.next();
        assertTrue(rv.getDescription().indexOf("Highest = 1") != -1);
    }

    public void testNastyComplicatedMethod() throws Throwable {
        rule.addProperty("reportLevel", "10");
        Report report = new Report();
        runTestFromString(TEST2, rule, report);
        Iterator i = report.iterator();
        RuleViolation rv = (RuleViolation) i.next();
        assertTrue(rv.getDescription().indexOf("Highest = 12") != -1);
    }

    public void testConstructor() throws Throwable {
        rule.addProperty("reportLevel", "1");
        Report report = new Report();
        runTestFromString(TEST3, rule, report);
        Iterator i = report.iterator();
        RuleViolation rv = (RuleViolation) i.next();
        assertTrue(rv.getDescription().indexOf("Highest = 1") != -1);
    }

    public void testLessComplicatedThanReportLevel() throws Throwable {
        rule.addProperty("reportLevel", "10");
        Report report = new Report();
        runTestFromString(TEST1, rule, report);
        assertEquals(0, report.size());
    }

    private static final String TEST1 =
    "public class CyclomaticComplexity1 {" + CPD.EOL +
    " public void foo() {}" + CPD.EOL +
    "}";

    private static final String TEST2 =
    "public class CyclomaticComplexity2 {" + CPD.EOL +
    " public void example() {" + CPD.EOL +
    "  int x = 0;" + CPD.EOL +
    "  int a = 0;" + CPD.EOL +
    "  int b = 0;" + CPD.EOL +
    "  int c = 0;" + CPD.EOL +
    "  int d = 0;" + CPD.EOL +
    "  int a1 = 0;" + CPD.EOL +
    "  int a2 = 0;" + CPD.EOL +
    "  int b1 = 0;" + CPD.EOL +
    "  int b2 = 0;" + CPD.EOL +
    "  int z = 0;" + CPD.EOL +
    "  int h = 0;" + CPD.EOL +
    "  int e = 0;" + CPD.EOL +
    "  int f = 0;" + CPD.EOL +
    "" + CPD.EOL +
    "  if (a == b) {" + CPD.EOL +
    "   if (a1 == b1) {" + CPD.EOL +
    "     x=2;" + CPD.EOL +
    "   } else if (a2 == b2) {" + CPD.EOL +
    "     x=2;" + CPD.EOL +
    "   }" + CPD.EOL +
    "            else" + CPD.EOL +
    "            {" + CPD.EOL +
    "                x=2;" + CPD.EOL +
    "            }" + CPD.EOL +
    "        }" + CPD.EOL +
    "       else if (c == d)" + CPD.EOL +
    "        {" + CPD.EOL +
    "           while (c == d)" + CPD.EOL +
    "            {" + CPD.EOL +
    "                x=2;" + CPD.EOL +
    "            }" + CPD.EOL +
    "        }" + CPD.EOL +
    "       else if (e == f)" + CPD.EOL +
    "        {" + CPD.EOL +
    "           for (int n = 0; n < h; n++)" + CPD.EOL +
    "            {" + CPD.EOL +
    "                x=2;" + CPD.EOL +
    "            }" + CPD.EOL +
    "        }" + CPD.EOL +
    "        else" + CPD.EOL +
    "        {" + CPD.EOL +
    "            switch (z)" + CPD.EOL +
    "            {" + CPD.EOL +
    "               case 1:" + CPD.EOL +
    "                x=2;" + CPD.EOL +
    "                    break;" + CPD.EOL +
    "" + CPD.EOL +
    "              case 2:" + CPD.EOL +
    "                x=2;" + CPD.EOL +
    "                    break;" + CPD.EOL +
    "" + CPD.EOL +
    "              case 3:" + CPD.EOL +
    "                x=2;" + CPD.EOL +
    "                    break;" + CPD.EOL +
    "" + CPD.EOL +
    "              default:" + CPD.EOL +
    "                x=2;" + CPD.EOL +
    "                    break;" + CPD.EOL +
    "            }" + CPD.EOL +
    "        }" + CPD.EOL +
    "    }" + CPD.EOL +
    "}";

    private static final String TEST3 =
    "public class CyclomaticComplexity3 {" + CPD.EOL +
    " public CyclomaticComplexity3() {}" + CPD.EOL +
    "}";

}
