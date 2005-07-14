/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class LongParameterListRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("codesize", "ExcessiveParameterList");
        rule.addProperty("minimum", "9");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "short", 0, rule),
           new TestDescriptor(TEST2, "long", 1, rule),
       });
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    "    public void foo() {}" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    "    public void foo(int p01, int p02, int p03, int p04, int p05, int p06, int p07, int p08, int p09, int p10 ) { }" + PMD.EOL +
    "    public void bar(int p01, int p02, int p03, int p04, int p05 ) { }" + PMD.EOL +
    "}";
}
