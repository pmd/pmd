/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.codesize;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.testframework.RuleTst;
import net.sourceforge.pmd.testframework.TestDescriptor;

import org.junit.Before;
import org.junit.Test;


public class NPathComplexityTest extends RuleTst {
    private Rule rule;
    private TestDescriptor[] tests;

    @Before
    public void setUp() {
	rule = findRule("java-codesize", "NPathComplexity");
	tests = extractTestsFromXml(rule);
    }

    @Test
    public void testViolationMessage() throws Throwable {
	rule.setProperty(NPathComplexityRule.MINIMUM_DESCRIPTOR, 1.0);
	Report report = new Report();
	runTestFromString(tests[0].getCode(), rule, report);
	Iterator<RuleViolation> i = report.iterator();
	RuleViolation rv = (RuleViolation) i.next();
	assertEquals("correct violation message", "The method bar() has an NPath complexity of 2", rv.getDescription());
    }
    
    /**
     * Runs the 3rd test case with the proper threshold property.
     * @throws Exception any error
     */
    @Test
    public void testReturnValueComplexity() throws Exception {
    	rule.setProperty(NPathComplexityRule.MINIMUM_DESCRIPTOR, 25.0);
    	Report report = new Report();
    	runTestFromString(tests[2].getCode(), rule, report);
    	Iterator<RuleViolation> i = report.iterator();
    	String descriptions = "";
    	while (i.hasNext()) {
    		RuleViolation violation = i.next();
    		descriptions += violation.getDescription() + "\n";
    	}
    	assertEquals("expected violations", 2, report.size());
    	assertEquals("The method x() has an NPath complexity of 25\nThe method y() has an NPath complexity of 25\n",
    			descriptions);
    }

    public static junit.framework.Test suite() {
	return new junit.framework.JUnit4TestAdapter(NPathComplexityTest.class);
    }
}
