package net.sourceforge.pmd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import junit.framework.JUnit4TestAdapter;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.RuleViolationComparator;
import net.sourceforge.pmd.lang.java.ast.DummyJavaNode;
import net.sourceforge.pmd.lang.java.rule.JavaRuleViolation;
import net.sourceforge.pmd.lang.java.symboltable.SourceFileScope;
import net.sourceforge.pmd.lang.rule.MockRule;

import org.junit.Test;

public class RuleViolationComparatorTest {

    @Test
    public void testComparator() {
	Rule rule1 = new MockRule("name1", "desc", "msg", "rulesetname1");
	Rule rule2 = new MockRule("name2", "desc", "msg", "rulesetname2");

	// RuleViolations created in pre-sorted order
	RuleViolation[] expectedOrder = new RuleViolation[12];

	int index = 0;
	// Different begin line
	expectedOrder[index++] = createJavaRuleViolation(rule1, "file1", 10, "desc1", 0, 20, 80);
	expectedOrder[index++] = createJavaRuleViolation(rule1, "file1", 20, "desc1", 0, 20, 80);
	// Different description
	expectedOrder[index++] = createJavaRuleViolation(rule1, "file2", 10, "desc1", 0, 20, 80);
	expectedOrder[index++] = createJavaRuleViolation(rule1, "file2", 10, "desc2", 0, 20, 80);
	// Different begin column
	expectedOrder[index++] = createJavaRuleViolation(rule1, "file3", 10, "desc1", 0, 20, 80);
	expectedOrder[index++] = createJavaRuleViolation(rule1, "file3", 10, "desc1", 10, 20, 80);
	// Different end line
	expectedOrder[index++] = createJavaRuleViolation(rule1, "file4", 10, "desc1", 0, 20, 80);
	expectedOrder[index++] = createJavaRuleViolation(rule1, "file4", 10, "desc1", 0, 30, 80);
	// Different end column
	expectedOrder[index++] = createJavaRuleViolation(rule1, "file5", 10, "desc1", 0, 20, 80);
	expectedOrder[index++] = createJavaRuleViolation(rule1, "file5", 10, "desc1", 0, 20, 90);
	// Different rule name
	expectedOrder[index++] = createJavaRuleViolation(rule1, "file6", 10, "desc1", 0, 20, 80);
	expectedOrder[index++] = createJavaRuleViolation(rule2, "file6", 10, "desc1", 0, 20, 80);

	// Randomize
	List<RuleViolation> ruleViolations = new ArrayList<RuleViolation>(Arrays.asList(expectedOrder));
	long seed = System.nanoTime();
	Random random = new Random(seed);
	Collections.shuffle(ruleViolations, random);

	// Sort
	Collections.sort(ruleViolations, RuleViolationComparator.INSTANCE);

	// Check
	int count = 0;
	for (int i = 0; i < expectedOrder.length; i++) {
	    count++;
	    assertSame("Wrong RuleViolation " + i + ", usind seed: " + seed, expectedOrder[i], ruleViolations.get(i));
	}
	assertEquals("Missing assertion for every RuleViolation", expectedOrder.length, count);
    }

    private RuleViolation createJavaRuleViolation(Rule rule, String fileName, int beginLine, String description,
	    int beginColumn, int endLine, int endColumn) {
	RuleContext ruleContext = new RuleContext();
	ruleContext.setSourceCodeFilename(fileName);
	DummyJavaNode simpleNode = new DummyJavaNode(1);
	simpleNode.setScope(new SourceFileScope(fileName));
	simpleNode.testingOnly__setBeginLine(beginLine);
	simpleNode.testingOnly__setBeginColumn(beginColumn);
	simpleNode.testingOnly__setEndLine(endLine);
	simpleNode.testingOnly__setEndColumn(endColumn);
	RuleViolation ruleViolation = new JavaRuleViolation(rule, ruleContext, simpleNode, description);
	return ruleViolation;
    }

    public static junit.framework.Test suite() {
	return new JUnit4TestAdapter(RuleViolationComparatorTest.class);
    }
}
