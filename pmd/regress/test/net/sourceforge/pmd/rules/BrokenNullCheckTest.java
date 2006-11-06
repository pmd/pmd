package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class BrokenNullCheckTest extends SimpleAggregatorTst {
    private Rule rule;

    public void setUp() {
        rule = findRule("basic", "BrokenNullCheck");
    }
   
    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "should be ||", 1, rule),
            new TestDescriptor(TEST2, "should be &&", 1, rule),
        });
    }
    
    private static final String TEST1 =
    	"public class Foo {" + PMD.EOL +
    	"	List bar(List list) {" + PMD.EOL +
    	"		if (list != null || !list.equals(buz)) {" + PMD.EOL +
    	"			return list;" + PMD.EOL +
    	"		}" + PMD.EOL +
    	"	}" + PMD.EOL +
    	"}";
    
    private static final String TEST2 =
    	"public class Foo {" + PMD.EOL +
    	"	List bar(List list) {" + PMD.EOL +
    	"		if (list == null && list.equals(buz)) {" + PMD.EOL +
    	"			return list;" + PMD.EOL +
    	"		}" + PMD.EOL +
    	"	}" + PMD.EOL +
    	"}";
}
