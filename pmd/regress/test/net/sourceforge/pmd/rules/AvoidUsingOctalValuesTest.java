package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class AvoidUsingOctalValuesTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("basic", "AvoidUsingOctalValues");
    }

    public void testAll() {
        runTests(rule);
    }

}
