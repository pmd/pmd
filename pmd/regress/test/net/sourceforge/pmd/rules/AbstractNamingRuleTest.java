/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class AbstractNamingRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("rulesets/naming.xml", "AbstractNamingRule");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "ok, abstract class AbstractFoo", 0, rule),
           new TestDescriptor(TEST2, "bad, abstract class named Foo", 1, rule),
           new TestDescriptor(TEST3, "ok, concrete class named AbstractFoo", 0, rule),
           new TestDescriptor(TEST4, "ok, concrete class named Foo", 0, rule),
       });
    }

    private static final String TEST1 =
    "public abstract class AbstractFoo {}";

    private static final String TEST2 =
    "public abstract class Foo {}";

    private static final String TEST3 =
    "public class AbstractFoo {}";

    private static final String TEST4 =
    "public class Foo {}";
}
