/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class ProperCloneImplementationRuleTest extends SimpleAggregatorTst{

  private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("rulesets/design.xml", "ProperCloneImplementationRule");
    }

  public void testAll() {
     runTests(new TestDescriptor[] {
         new TestDescriptor(TEST1, "ok, calls super.clone", 0, rule),
         new TestDescriptor(TEST2, "bad, Foo.clone() calls new Foo();", 1, rule),
         new TestDescriptor(TEST3, "clone([whatever]) is fine", 0, rule),
     });
  }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " void clone() {" + PMD.EOL +
    "  super.clone();" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " void clone() {" + PMD.EOL +
    "  Foo f = new Foo();" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class Foo {" + PMD.EOL +
    " void clone(String fiddle) {" + PMD.EOL +
    "  Foo f = new Foo();" + PMD.EOL +
    " }" + PMD.EOL +
    "}";
}
