/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class CallSuperInConstructorTest extends SimpleAggregatorTst {
	private Rule rule = null;
	
	public void setUp() {
		rule = findRule("controversial", "CallSuperInConstructor");
	}
	
    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "TEST1", 1, rule),
		   new TestDescriptor(TEST2, "TEST2", 0, rule),
		   new TestDescriptor(TEST3, "TEST3", 0, rule),
       });
    }
    
    private static final String TEST1 =
    	"public class Foo {" + PMD.EOL +
		" public Foo() {" + PMD.EOL +
		" }" + PMD.EOL +
		"}";
    
    private static final String TEST2 =
    	"public class Foo {" + PMD.EOL +
		" public Foo() {" + PMD.EOL +
		" super();" + PMD.EOL +
		"}" + PMD.EOL +
		"}";
    
    private static final String TEST3 =
    	"public class Foo {" + PMD.EOL +
		" public Foo(Object o) {" + PMD.EOL +
		" 	this();" + PMD.EOL +
		"}" + PMD.EOL +
		" public Foo() {" + PMD.EOL +
		" super();" + PMD.EOL +
		"}" + PMD.EOL +
		"}";

}
