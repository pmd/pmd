
 package test.net.sourceforge.pmd.rules.strings;
 
 import net.sourceforge.pmd.Rule;
 import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 public class UselessStringValueOfTest extends SimpleAggregatorTst {
 
 	private Rule rule;
 
 	public void setUp() {
 		rule = findRule("rulesets/strings.xml", "UselessStringValueOf");
 	}
 
 	public void testAll() {
 		runTests(rule);
 	}
 }
