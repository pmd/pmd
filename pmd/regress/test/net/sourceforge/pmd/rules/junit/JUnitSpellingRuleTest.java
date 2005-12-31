/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules.junit;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class JUnitSpellingRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("junit", "JUnitSpelling");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "setUp mispellings", 2, rule),
           new TestDescriptor(TEST2, "tearDown mispellings", 2, rule),
           new TestDescriptor(TEST3, "ok", 0, rule),
           new TestDescriptor(TEST4, "unrelated methods", 0, rule),
           new TestDescriptor(TEST5, "overloaded setUp", 0, rule),
       });
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " public void setup() {}" + PMD.EOL +
    " public void SetUp() {}" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " public void TearDown() {}" + PMD.EOL +
    " public void teardown() {}" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class Foo {" + PMD.EOL +
    " public void setUp() {}" + PMD.EOL +
    " public void tearDown() {}" + PMD.EOL +
    "}";

    private static final String TEST4 =
    "public class Foo {" + PMD.EOL +
    " public void utility() {}" + PMD.EOL +
    " public void foobr() {}" + PMD.EOL +
    "}";

    private static final String TEST5 =
    "public class Foo {" + PMD.EOL +
    " public void setup(String x) {}" + PMD.EOL +
    "}";
}
