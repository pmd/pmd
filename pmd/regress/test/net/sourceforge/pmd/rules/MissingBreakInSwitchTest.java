/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class MissingBreakInSwitchTest extends SimpleAggregatorTst {
    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("rulesets/design.xml", "MissingBreakInSwitch");
    }
    
    public void testAll() {
       runTests(new TestDescriptor[] {
               new TestDescriptor(TEST1, "TEST1", 1, rule),
               new TestDescriptor(TEST2, "TEST2", 1, rule),
	       new TestDescriptor(TEST3, "TEST3", 0, rule),
       });
    }
    
    private static final String TEST1 =
        "public class Foo {" + PMD.EOL +
	"	void main() {" + PMD.EOL +
	"		switch(i) {" + PMD.EOL +
	"		case 1:" + PMD.EOL +
	"		default:" + PMD.EOL +
	"		}" + PMD.EOL +
	"	}" + PMD.EOL +
        "}";

    private static final String TEST2 =
        "public class Foo {" + PMD.EOL +
	"	void main() {" + PMD.EOL +
	"		switch(i) {" + PMD.EOL +
	"		}" + PMD.EOL +
	"	}" + PMD.EOL +
        "}";
	
    private static final String TEST3 =
        "public class Foo {" + PMD.EOL +
	"	void main() {" + PMD.EOL +
	"		switch(i) {" + PMD.EOL +
	"		case 1:" + PMD.EOL +
	"		case 2:" + PMD.EOL +
	"			break;" + PMD.EOL +
	"		default:" + PMD.EOL +
	"		}" + PMD.EOL +
	"	}" + PMD.EOL +
        "}";

}

