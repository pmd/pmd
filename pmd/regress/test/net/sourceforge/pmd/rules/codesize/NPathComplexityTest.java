/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.rules.codesize;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.rule.stat.StatisticalRuleHelper;

import org.junit.Before;
import org.junit.Test;

import test.net.sourceforge.pmd.testframework.RuleTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class NPathComplexityTest extends RuleTst {
	private Rule rule;
	private TestDescriptor[] tests;

	@Before
	public void setUp() {
		rule = findRule("codesize", "NPathComplexity");
		tests = extractTestsFromXml(rule);
	}

	@Test
	public void testViolationMessage() throws Throwable {
		//	rule.addProperty("minimum", "1");
		rule.setProperty(StatisticalRuleHelper.MINIMUM_DESCRIPTOR, Integer.valueOf(1));
		Report report = new Report();
		runTestFromString(tests[0].getCode(), rule, report);
		Iterator i = report.iterator();
		RuleViolation rv = (RuleViolation) i.next();
		assertEquals("correct violation message", "The method bar() has an NPath complexity of 2", rv.getDescription());
	}

	public static junit.framework.Test suite() {
		return new junit.framework.JUnit4TestAdapter(NPathComplexityTest.class);
	}
}
