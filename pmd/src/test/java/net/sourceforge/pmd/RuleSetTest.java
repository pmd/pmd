/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.rule.MockRule;
import net.sourceforge.pmd.lang.rule.RuleReference;
import net.sourceforge.pmd.testframework.RuleTst;

import org.junit.Test;


public class RuleSetTest extends RuleTst {

    private String javaCode = "public class Test { }";

    @Test
    public void testNoDFA() {
	RuleSet rs = new RuleSet();
	MockRule mock = new MockRule("name", "desc", "msg", "rulesetname");
	rs.addRule(mock);
	assertFalse(rs.usesDFA(Language.JAVA));
    }

    @Test
    public void testIncludesRuleWithDFA() {
	RuleSet rs = new RuleSet();
	MockRule mock = new MockRule("name", "desc", "msg", "rulesetname");
	mock.setUsesDFA();
	rs.addRule(mock);
	assertTrue(rs.usesDFA(Language.JAVA));
    }

    @Test
    public void testAccessors() {
	RuleSet rs = new RuleSet();
	rs.setFileName("baz");
	assertEquals("file name mismatch", "baz", rs.getFileName());
	rs.setName("foo");
	assertEquals("name mismatch", "foo", rs.getName());
	rs.setDescription("bar");
	assertEquals("description mismatch", "bar", rs.getDescription());
    }

    @Test
    public void testGetRuleByName() {
	RuleSet rs = new RuleSet();
	MockRule mock = new MockRule("name", "desc", "msg", "rulesetname");
	rs.addRule(mock);
	assertEquals("unable to fetch rule by name", mock, rs.getRuleByName("name"));
    }

    @Test
    public void testGetRuleByName2() {
	RuleSet rs = new RuleSet();
	MockRule mock = new MockRule("name", "desc", "msg", "rulesetname");
	rs.addRule(mock);
	assertNull("the rule FooRule must not be found!", rs.getRuleByName("FooRule"));
    }

    @Test
    public void testRuleList() {
	RuleSet IUT = new RuleSet();

	assertEquals("Size of RuleSet isn't zero.", 0, IUT.size());

	MockRule rule = new MockRule("name", "desc", "msg", "rulesetname");
	IUT.addRule(rule);

	assertEquals("Size of RuleSet isn't one.", 1, IUT.size());

	Collection rules = IUT.getRules();

	Iterator i = rules.iterator();
	assertTrue("Empty Set", i.hasNext());
	assertEquals("Returned set of wrong size.", 1, rules.size());
	assertEquals("Rule isn't in ruleset.", rule, i.next());
    }

    @Test
    public void testAddRuleSet() {
	RuleSet set1 = new RuleSet();
	set1.addRule(new MockRule("name", "desc", "msg", "rulesetname"));
	RuleSet set2 = new RuleSet();
	set2.addRule(new MockRule("name2", "desc", "msg", "rulesetname"));
	set1.addRuleSet(set2);
	assertEquals("ruleset size wrong", 2, set1.size());
    }

    @Test(expected = RuntimeException.class)
    public void testAddRuleSetByReferenceBad() {
	RuleSet set1 = new RuleSet();
	set1.addRule(new MockRule("name", "desc", "msg", "rulesetname"));
	RuleSet set2 = new RuleSet();
	set2.addRule(new MockRule("name2", "desc", "msg", "rulesetname"));
	set1.addRuleSetByReference(set2, false);
    }

    @Test
    public void testAddRuleSetByReferenceAllRule() {
	RuleSet set1 = new RuleSet();
	RuleSet set2 = new RuleSet();
	set2.setFileName("foo");
	set2.addRule(new MockRule("name", "desc", "msg", "rulesetname"));
	set2.addRule(new MockRule("name2", "desc", "msg", "rulesetname"));
	set1.addRuleSetByReference(set2, true);
	assertEquals("wrong rule size", 2, set1.getRules().size());
	for (Rule rule : set1.getRules()) {
	    assertTrue("not a rule reference", rule instanceof RuleReference);
	    RuleReference ruleReference = (RuleReference) rule;
	    assertEquals("wrong ruleset file name", "foo", ruleReference.getRuleSetReference().getRuleSetFileName());
	    assertTrue("not all rule reference", ruleReference.getRuleSetReference().isAllRules());
	}
    }

    @Test
    public void testAddRuleSetByReferenceSingleRule() {
	RuleSet set1 = new RuleSet();
	RuleSet set2 = new RuleSet();
	set2.setFileName("foo");
	set2.addRule(new MockRule("name", "desc", "msg", "rulesetname"));
	set2.addRule(new MockRule("name2", "desc", "msg", "rulesetname"));
	set1.addRuleSetByReference(set2, false);
	assertEquals("wrong rule size", 2, set1.getRules().size());
	for (Rule rule : set1.getRules()) {
	    assertTrue("not a rule reference", rule instanceof RuleReference);
	    RuleReference ruleReference = (RuleReference) rule;
	    assertEquals("wrong ruleset file name", "foo", ruleReference.getRuleSetReference().getRuleSetFileName());
	    assertFalse("should not be all rule reference", ruleReference.getRuleSetReference().isAllRules());
	}
    }

    @Test
    public void testApply0Rules() throws Throwable {
	RuleSet IUT = new RuleSet();
	verifyRuleSet(IUT, 0, new HashSet());
    }

    @Test
    public void testEquals1() {
	RuleSet s = new RuleSet();
	assertFalse("A ruleset cannot be equals to null", s.equals(null));
    }

    @Test
    @SuppressWarnings("PMD.UseAssertEqualsInsteadOfAssertTrue")
    public void testEquals2() {
	RuleSet s = new RuleSet();
	assertTrue("A rulset must be equals to itself", s.equals(s));
    }

    @Test
    public void testEquals3() {
	RuleSet s = new RuleSet();
	s.setName("basic rules");
	assertFalse("A ruleset cannot be equals to another kind of object", s.equals("basic rules"));
    }

    @Test
    public void testEquals4() {
	RuleSet s1 = new RuleSet();
	s1.setName("my ruleset");
	s1.addRule(new MockRule("name", "desc", "msg", "rulesetname"));

	RuleSet s2 = new RuleSet();
	s2.setName("my ruleset");
	s2.addRule(new MockRule("name", "desc", "msg", "rulesetname"));

	assertEquals("2 rulesets with same name and rules must be equals", s1, s2);
	assertEquals("Equals rulesets must have the same hashcode", s1.hashCode(), s2.hashCode());
    }

    @Test
    public void testEquals5() {
	RuleSet s1 = new RuleSet();
	s1.setName("my ruleset");
	s1.addRule(new MockRule("name", "desc", "msg", "rulesetname"));

	RuleSet s2 = new RuleSet();
	s2.setName("my other ruleset");
	s2.addRule(new MockRule("name", "desc", "msg", "rulesetname"));

	assertFalse("2 rulesets with different name but same rules must not be equals", s1.equals(s2));
    }

    @Test
    public void testEquals6() {
	RuleSet s1 = new RuleSet();
	s1.setName("my ruleset");
	s1.addRule(new MockRule("name", "desc", "msg", "rulesetname"));

	RuleSet s2 = new RuleSet();
	s2.setName("my ruleset");
	s2.addRule(new MockRule("other rule", "desc", "msg", "rulesetname"));

	assertFalse("2 rulesets with same name but different rules must not be equals", s1.equals(s2));
    }
    
    @Test
    public void testLanguageApplies() {

	Rule rule = new MockRule();

	rule.setLanguage(Language.ECMASCRIPT);
	assertFalse("Different languages should not apply", RuleSet.applies(rule, LanguageVersion.JAVA_15));

	rule.setLanguage(Language.JAVA);
	assertTrue("Same language with no min/max should apply", RuleSet.applies(rule, LanguageVersion.JAVA_15));

	rule.setMinimumLanguageVersion(LanguageVersion.JAVA_15);
	assertTrue("Same language with valid min only should apply", RuleSet.applies(rule, LanguageVersion.JAVA_15));

	rule.setMaximumLanguageVersion(LanguageVersion.JAVA_16);
	assertTrue("Same language with valid min and max should apply", RuleSet.applies(rule, LanguageVersion.JAVA_15));
	assertFalse("Same language with outside range of min/max should not apply", RuleSet.applies(rule, LanguageVersion.JAVA_14));
	assertFalse("Same language with outside range of min/max should not apply", RuleSet.applies(rule, LanguageVersion.JAVA_17));
    }

    @Test
    public void testAddExcludePattern() {
	RuleSet ruleSet = new RuleSet();
	ruleSet.addExcludePattern("*");
	assertNotNull("Exclude patterns", ruleSet.getExcludePatterns());
	assertEquals("Invalid number of patterns", 1, ruleSet.getExcludePatterns().size());
	ruleSet.addExcludePattern("*");		// try to create a duplicate
	assertEquals("Invalid number of patterns", 1, ruleSet.getExcludePatterns().size());	
	assertEquals("Exclude pattern", "*", ruleSet.getExcludePatterns().get(0));
	assertNotNull("Include patterns", ruleSet.getIncludePatterns());
	assertEquals("Invalid number of include patterns", 0, ruleSet.getIncludePatterns().size());
    }

    @Test
    public void testAddExcludePatterns() {
	RuleSet ruleSet = new RuleSet();
	ruleSet.addExcludePattern("*");
	ruleSet.addExcludePattern(".*");
	RuleSet ruleSet2 = new RuleSet();
	ruleSet2.addExcludePatterns(ruleSet.getExcludePatterns());
	assertNotNull("Exclude patterns", ruleSet2.getExcludePatterns());
	assertEquals("Invalid number of patterns", 2, ruleSet2.getExcludePatterns().size());
	ruleSet.addExcludePattern(".*");	// try to create a duplicate
	assertEquals("Invalid number of patterns", 2, ruleSet2.getExcludePatterns().size());
	assertEquals("Exclude pattern", "*", ruleSet2.getExcludePatterns().get(0));
	assertEquals("Exclude pattern", ".*", ruleSet2.getExcludePatterns().get(1));
	assertNotNull("Include patterns", ruleSet2.getIncludePatterns());
	assertEquals("Invalid number of include patterns", 0, ruleSet2.getIncludePatterns().size());
    }

    @Test
    public void testSetExcludePatterns() {
	List<String> excludePatterns = new ArrayList<String>();
	excludePatterns.add("*");
	excludePatterns.add(".*");
	RuleSet ruleSet = new RuleSet();
	ruleSet.setExcludePatterns(excludePatterns);
	assertNotNull("Exclude patterns", ruleSet.getExcludePatterns());
	assertEquals("Invalid number of exclude patterns", 2, ruleSet.getExcludePatterns().size());
	assertEquals("Exclude pattern", "*", ruleSet.getExcludePatterns().get(0));
	assertEquals("Exclude pattern", ".*", ruleSet.getExcludePatterns().get(1));
	assertNotNull("Include patterns", ruleSet.getIncludePatterns());
	assertEquals("Invalid number of include patterns", 0, ruleSet.getIncludePatterns().size());
    }

    @Test
    public void testAddIncludePattern() {
	RuleSet ruleSet = new RuleSet();
	ruleSet.addIncludePattern("*");
	assertNotNull("Include patterns", ruleSet.getIncludePatterns());
	assertEquals("Invalid number of patterns", 1, ruleSet.getIncludePatterns().size());
	assertEquals("Include pattern", "*", ruleSet.getIncludePatterns().get(0));
	assertNotNull("Exclude patterns", ruleSet.getExcludePatterns());
	assertEquals("Invalid number of exclude patterns", 0, ruleSet.getExcludePatterns().size());
    }

    @Test
    public void testAddIncludePatterns() {
	RuleSet ruleSet = new RuleSet();
	ruleSet.addIncludePattern("*");
	ruleSet.addIncludePattern(".*");
	RuleSet ruleSet2 = new RuleSet();
	ruleSet2.addIncludePatterns(ruleSet.getIncludePatterns());
	assertNotNull("Include patterns", ruleSet2.getIncludePatterns());
	assertEquals("Invalid number of patterns", 2, ruleSet2.getIncludePatterns().size());
	assertEquals("Include pattern", "*", ruleSet2.getIncludePatterns().get(0));
	assertEquals("Include pattern", ".*", ruleSet2.getIncludePatterns().get(1));
	assertNotNull("Exclude patterns", ruleSet.getExcludePatterns());
	assertEquals("Invalid number of exclude patterns", 0, ruleSet.getExcludePatterns().size());
    }

    @Test
    public void testSetIncludePatterns() {
	List<String> includePatterns = new ArrayList<String>();
	includePatterns.add("*");
	includePatterns.add(".*");
	RuleSet ruleSet = new RuleSet();
	ruleSet.setIncludePatterns(includePatterns);
	assertNotNull("Include patterns", ruleSet.getIncludePatterns());
	assertEquals("Invalid number of include patterns", 2, ruleSet.getIncludePatterns().size());
	assertEquals("Include pattern", "*", ruleSet.getIncludePatterns().get(0));
	assertEquals("Include pattern", ".*", ruleSet.getIncludePatterns().get(1));
	assertNotNull("Exclude patterns", ruleSet.getExcludePatterns());
	assertEquals("Invalid number of exclude patterns", 0, ruleSet.getExcludePatterns().size());
    }

    @Test
    public void testIncludeExcludeApplies() {
	File file = new File("C:\\myworkspace\\project\\some\\random\\package\\RandomClass.java");

	RuleSet ruleSet = new RuleSet();
	assertTrue("No patterns", ruleSet.applies(file));

	ruleSet = new RuleSet();
	ruleSet.addExcludePattern("nomatch");
	assertTrue("Non-matching exclude", ruleSet.applies(file));

	ruleSet = new RuleSet();
	ruleSet.addExcludePattern("nomatch");
	ruleSet.addExcludePattern(".*/package/.*");
	assertFalse("Matching exclude", ruleSet.applies(file));

	ruleSet = new RuleSet();
	ruleSet.addExcludePattern("nomatch");
	ruleSet.addExcludePattern(".*/package/.*");
	ruleSet.addIncludePattern(".*/randomX/.*");
	assertFalse("Non-matching include", ruleSet.applies(file));

	ruleSet = new RuleSet();
	ruleSet.addExcludePattern("nomatch");
	ruleSet.addExcludePattern(".*/package/.*");
	ruleSet.addIncludePattern(".*/randomX/.*");
	ruleSet.addIncludePattern(".*/random/.*");
	assertTrue("Matching include", ruleSet.applies(file));
    }

    @Test
    public void testIncludeExcludeMultipleRuleSetWithRuleChainApplies() throws PMDException {
	File file = new File("C:\\myworkspace\\project\\some\\random\\package\\RandomClass.java");

	RuleSet ruleSet1 = new RuleSet();
	ruleSet1.setName("RuleSet1");
	Rule rule = findRule("java-empty", "EmptyIfStmt");
	assertTrue("RuleChain rule", rule.usesRuleChain());
	ruleSet1.addRule(rule);

	RuleSet ruleSet2 = new RuleSet();
	ruleSet2.setName("RuleSet2");
	ruleSet2.addRule(rule);

	RuleSets ruleSets = new RuleSets();
	ruleSets.addRuleSet(ruleSet1);
	ruleSets.addRuleSet(ruleSet2);

	// Two violations
        PMD p = new PMD();
        RuleContext ctx = new RuleContext();
        Report r = new Report();
        ctx.setReport(r);
        ctx.setSourceCodeFilename(file.getName());
        ctx.setSourceCodeFile(file);
        p.getSourceCodeProcessor().processSourceCode(new StringReader(TEST1), ruleSets, ctx);
        assertEquals("Violations", 2, r.size());

        // One violation
	ruleSet1 = new RuleSet();
	ruleSet1.setName("RuleSet1");
        ruleSet1.addExcludePattern(".*/package/.*");
	ruleSet1.addRule(rule);

	ruleSets = new RuleSets();
	ruleSets.addRuleSet(ruleSet1);
	ruleSets.addRuleSet(ruleSet2);

        r = new Report();
        ctx.setReport(r);
        p.getSourceCodeProcessor().processSourceCode(new StringReader(TEST1), ruleSets, ctx);
        assertEquals("Violations", 1, r.size());
    }

    protected void verifyRuleSet(RuleSet IUT, int size, Set values) throws Throwable {

	RuleContext context = new RuleContext();
	Set<RuleViolation> reportedValues = new HashSet<RuleViolation>();
	context.setReport(new Report());
	IUT.apply(makeCompilationUnits(), context);

	assertEquals("Invalid number of Violations Reported", size, context.getReport().size());

	Iterator violations = context.getReport().iterator();
	while (violations.hasNext()) {
	    RuleViolation violation = (RuleViolation) violations.next();

	    reportedValues.add(violation);
	    assertTrue("Unexpected Violation Returned: " + violation, values.contains(violation));
	}

	Iterator expected = values.iterator();
	while (expected.hasNext()) {
	    RuleViolation violation = (RuleViolation) expected.next();
	    assertTrue("Expected Violation not Returned: " + violation, reportedValues.contains(violation));
	}
    }

    protected List<ASTCompilationUnit> makeCompilationUnits() throws Throwable {
	List<ASTCompilationUnit> RC = new ArrayList<ASTCompilationUnit>();
	LanguageVersionHandler languageVersionHandler = Language.JAVA.getDefaultVersion().getLanguageVersionHandler();
	ASTCompilationUnit cu = (ASTCompilationUnit) languageVersionHandler.getParser(
		languageVersionHandler.getDefaultParserOptions()).parse(null, new StringReader(javaCode));
	RC.add(cu);
	return RC;
    }

    private static final String TEST1 = "public class Foo {" + PMD.EOL +
    	"   public void foo() {" + PMD.EOL +
    	"      if (true) { }" + PMD.EOL +
    	"   }" + PMD.EOL +
	"}" + PMD.EOL;

    public static junit.framework.Test suite() {
	return new junit.framework.JUnit4TestAdapter(RuleSetTest.class);
    }
}
