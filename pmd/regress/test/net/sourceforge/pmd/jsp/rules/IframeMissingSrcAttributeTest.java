package test.net.sourceforge.pmd.jsp.rules;

import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class IframeMissingSrcAttributeTest extends SimpleAggregatorTst {
    private Rule rule;

    public void setUp() {
        rule = findRule("jsp", "IframeMissingSrcAttribute");
    }

    public void testAll() {
        runTests(rule);
    }
}
