/*
 * User: tom
 * Date: Sep 5, 2002
 * Time: 2:37:46 PM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.rules.XPathRule;

public class IfStmtsMustUseBracesRuleTest extends RuleTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//IfStatement[count(*) < 3][not(Statement/Block)]");
    }

    public void testSimpleBad() throws Throwable {
        runTestFromFile("IfStmtsMustUseBraces1.java", 1, rule);
    }

    public void testSimpleOK() throws Throwable {
        runTestFromFile("IfStmtsMustUseBraces2.java", 0, rule);
    }

    public void testNexted() throws Throwable {
        runTestFromFile("IfStmtsMustUseBraces3.java", 1, rule);
    }
}
