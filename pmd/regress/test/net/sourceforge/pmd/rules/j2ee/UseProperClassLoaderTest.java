package test.net.sourceforge.pmd.rules.j2ee;

import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class UseProperClassLoaderTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() {
        rule = findRule("j2ee", "UseProperClassLoader");
    }

    public void testAll() {
        runTests(rule);
    }
}
