package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.DuplicateImportsRule;

public class DuplicateImportsRuleTest extends RuleTst {

    private DuplicateImportsRule rule;

    public void setUp() {
        rule = new DuplicateImportsRule();
        rule.setMessage("Avoid this stuff -> ''{0}''");
    }

    public void test1() throws Throwable {
        runTestFromFile("DuplicateImports.java", 1, rule);
    }

    public void test2() throws Throwable {
        runTestFromFile("DuplicateImports2.java", 1, rule);
    }

    public void test3() throws Throwable {
        runTestFromFile("DuplicateImports3.java", 1, rule);
    }

    public void test4() throws Throwable {
        runTestFromFile("DuplicateImports4.java", 0, rule);
    }
}
