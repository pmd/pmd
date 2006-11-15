/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class CloseResourceTest extends SimpleAggregatorTst {
    private Rule rule;
    private TestDescriptor[] tests;

    public void setUp() {
        rule = findRule("design", "CloseResource");
        tests = extractTestsFromXml(rule);
        rule.addProperty("types", "Connection,Statement,ResultSet");  //Default
    }

    public void testAll() {
        runTests(new TestDescriptor[] {tests[0], tests[1], tests[2], tests[3], tests[4]});
    }

    public void testMultipleProperties(){
        runTest(tests[5]);
    }
    
    public void testTypes(){
        runTest(tests[6]);
    }
    
    public void testPropertySetter(){
        runTest(tests[7]);
    }

}
