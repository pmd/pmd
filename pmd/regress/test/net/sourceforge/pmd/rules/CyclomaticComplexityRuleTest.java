/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.rules.CyclomaticComplexityRule;
import test.net.sourceforge.pmd.testframework.RuleTst;

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
    "public class CyclomaticComplexity1 {" + PMD.EOL +
    " public void foo() {}" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class CyclomaticComplexity2 {" + PMD.EOL +
    " public void example() {" + PMD.EOL +
    "  int x = 0;" + PMD.EOL +
    "  int a = 0;" + PMD.EOL +
    "  int b = 0;" + PMD.EOL +
    "  int c = 0;" + PMD.EOL +
    "  int d = 0;" + PMD.EOL +
    "  int a1 = 0;" + PMD.EOL +
    "  int a2 = 0;" + PMD.EOL +
    "  int b1 = 0;" + PMD.EOL +
    "  int b2 = 0;" + PMD.EOL +
    "  int z = 0;" + PMD.EOL +
    "  int h = 0;" + PMD.EOL +
    "  int e = 0;" + PMD.EOL +
    "  int f = 0;" + PMD.EOL +
    "" + PMD.EOL +
    "  if (a == b) {" + PMD.EOL +
    "   if (a1 == b1) {" + PMD.EOL +
    "     x=2;" + PMD.EOL +
    "   } else if (a2 == b2) {" + PMD.EOL +
    "     x=2;" + PMD.EOL +
    "   }" + PMD.EOL +
    "            else" + PMD.EOL +
    "            {" + PMD.EOL +
    "                x=2;" + PMD.EOL +
    "            }" + PMD.EOL +
    "        }" + PMD.EOL +
    "       else if (c == d)" + PMD.EOL +
    "        {" + PMD.EOL +
    "           while (c == d)" + PMD.EOL +
    "            {" + PMD.EOL +
    "                x=2;" + PMD.EOL +
    "            }" + PMD.EOL +
    "        }" + PMD.EOL +
    "       else if (e == f)" + PMD.EOL +
    "        {" + PMD.EOL +
    "           for (int n = 0; n < h; n++)" + PMD.EOL +
    "            {" + PMD.EOL +
    "                x=2;" + PMD.EOL +
    "            }" + PMD.EOL +
    "        }" + PMD.EOL +
    "        else" + PMD.EOL +
    "        {" + PMD.EOL +
    "            switch (z)" + PMD.EOL +
    "            {" + PMD.EOL +
    "               case 1:" + PMD.EOL +
    "                x=2;" + PMD.EOL +
    "                    break;" + PMD.EOL +
    "" + PMD.EOL +
    "              case 2:" + PMD.EOL +
    "                x=2;" + PMD.EOL +
    "                    break;" + PMD.EOL +
    "" + PMD.EOL +
    "              case 3:" + PMD.EOL +
    "                x=2;" + PMD.EOL +
    "                    break;" + PMD.EOL +
    "" + PMD.EOL +
    "              default:" + PMD.EOL +
    "                x=2;" + PMD.EOL +
    "                    break;" + PMD.EOL +
    "            }" + PMD.EOL +
    "        }" + PMD.EOL +
    "    }" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class CyclomaticComplexity3 {" + PMD.EOL +
    " public CyclomaticComplexity3() {}" + PMD.EOL +
    "}";

}
