package test.net.sourceforge.pmd.rules.clone;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.PMD;

public class CloneMethodMustImplementCloneableRuleTest extends SimpleAggregatorTst{

  private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("rulesets/clone.xml", "CloneMethodMustImplementCloneable");
    }

  public void testAll() {
     runTests(new TestDescriptor[] {
         new TestDescriptor(TEST1, "ok, implements Cloneable", 0, rule),
         new TestDescriptor(TEST2, "bad, doesn't implement Cloneable", 1, rule),
     });
  }

    private static final String TEST1 =
    "public class Foo implements Cloneable {" + PMD.EOL +
    " void clone() {}" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " void clone() {}" + PMD.EOL +
    "}";
}
