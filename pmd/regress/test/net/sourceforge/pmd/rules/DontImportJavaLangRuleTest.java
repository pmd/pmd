/*
 * User: tom
 * Date: Jul 15, 2002
 * Time: 8:46:03 AM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.XPathRule;
import net.sourceforge.pmd.Rule;

public class DontImportJavaLangRuleTest extends RuleTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//ImportDeclaration"
                + "[starts-with(Name/@Image, 'java.lang')]"
                + "[not(starts-with(Name/@Image, 'java.lang.ref'))]"
                + "[not(starts-with(Name/@Image, 'java.lang.reflect'))]");
    }

    public void test1() throws Throwable {
        runTest("DontImportJavaLang1.java", 1, rule);
    }

    public void test2() throws Throwable {
        runTest("DontImportJavaLang2.java", 1, rule);
    }

    public void test3() throws Throwable {
        runTest("DontImportJavaLang3.java", 0, rule);
    }
}
