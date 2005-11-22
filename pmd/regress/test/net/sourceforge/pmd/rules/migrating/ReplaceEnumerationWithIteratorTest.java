package test.net.sourceforge.pmd.rules.migrating;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.PMD;

public class ReplaceEnumerationWithIteratorTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("migrating", "ReplaceEnumerationWithIterator");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "bad, implementing Enumeration", 1, rule),
       });
    }

    private static final String TEST1 =
    "public class Foo implements Enumeration {" + PMD.EOL +
    " public boolean hasMoreElements() {" + PMD.EOL +
    "  true;" + PMD.EOL +
    " }" + PMD.EOL +
    " public Object nextElement() {" + PMD.EOL +
    "  return \"hello\";" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

}
