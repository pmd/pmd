/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.junit.Test;

public class RuleSetReferenceIdTest {

    private static void assertRuleSetReferenceId(final boolean expectedExternal, final String expectedRuleSetFileName,
	    final boolean expectedAllRules, final String expectedRuleName, final String expectedToString,
	    final RuleSetReferenceId reference) {
	assertEquals("Wrong external", expectedExternal, reference.isExternal());
	assertEquals("Wrong RuleSet file name", expectedRuleSetFileName, reference.getRuleSetFileName());
	assertEquals("Wrong all Rule reference", expectedAllRules, reference.isAllRules());
	assertEquals("Wrong Rule name", expectedRuleName, reference.getRuleName());
	assertEquals("Wrong toString()", expectedToString, reference.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCommaInSingleId() {
	new RuleSetReferenceId("bad,id");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInternalWithInternal() {
	new RuleSetReferenceId("SomeRule", new RuleSetReferenceId("SomeOtherRule"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExternalWithExternal() {
	new RuleSetReferenceId("someruleset.xml/SomeRule", new RuleSetReferenceId("someruleset.xml/SomeOtherRule"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExternalWithInternal() {
	new RuleSetReferenceId("someruleset.xml/SomeRule", new RuleSetReferenceId("SomeOtherRule"));
    }

    @Test
    public void testInteralWithExternal() {
	// This is okay
	new RuleSetReferenceId("SomeRule", new RuleSetReferenceId("someruleset.xml/SomeOtherRule"));
    }

    @Test
    public void testEmptyRuleSet() {
	// This is representative of how the Test framework creates RuleSetReferenceId from static RuleSet XMLs
	RuleSetReferenceId reference = new RuleSetReferenceId(null);
	assertRuleSetReferenceId(true, null, true, null, "anonymous all Rule", reference);
    }

    @Test
    public void testInternalWithExternalRuleSet() {
	// This is representative of how the RuleSetFactory temporarily pairs an internal reference
	// with an external reference.
	RuleSetReferenceId internalRuleSetReferenceId = new RuleSetReferenceId("MockRuleName");
	assertRuleSetReferenceId(false, null, false, "MockRuleName", "MockRuleName", internalRuleSetReferenceId);
	RuleSetReferenceId externalRuleSetReferenceId = new RuleSetReferenceId("rulesets/java/basic.xml");
	assertRuleSetReferenceId(true, "rulesets/java/basic.xml", true, null, "rulesets/java/basic.xml",
		externalRuleSetReferenceId);

	RuleSetReferenceId pairRuleSetReferenceId = new RuleSetReferenceId("MockRuleName", externalRuleSetReferenceId);
	assertRuleSetReferenceId(true, "rulesets/java/basic.xml", false, "MockRuleName",
		"rulesets/java/basic.xml/MockRuleName", pairRuleSetReferenceId);
    }

    @Test
    public void testOneSimpleRuleSet() {
	List<RuleSetReferenceId> references = RuleSetReferenceId.parse("java-basic");
	assertEquals(1, references.size());
	assertRuleSetReferenceId(true, "rulesets/java/basic.xml", true, null, "rulesets/java/basic.xml", references
		.get(0));
    }

    @Test
    public void testMultipleSimpleRuleSet() {
	List<RuleSetReferenceId> references = RuleSetReferenceId.parse("java-unusedcode,java-basic");
	assertEquals(2, references.size());
	assertRuleSetReferenceId(true, "rulesets/java/unusedcode.xml", true, null, "rulesets/java/unusedcode.xml",
		references.get(0));
	assertRuleSetReferenceId(true, "rulesets/java/basic.xml", true, null, "rulesets/java/basic.xml", references
		.get(1));
    }

    @Test
    public void testOneReleaseRuleSet() {
	List<RuleSetReferenceId> references = RuleSetReferenceId.parse("50");
	assertEquals(1, references.size());
	assertRuleSetReferenceId(true, "rulesets/releases/50.xml", true, null, "rulesets/releases/50.xml", references
		.get(0));
    }

    @Test
    public void testOneFullRuleSet() {
	List<RuleSetReferenceId> references = RuleSetReferenceId.parse("rulesets/java/unusedcode.xml");
	assertEquals(1, references.size());
	assertRuleSetReferenceId(true, "rulesets/java/unusedcode.xml", true, null, "rulesets/java/unusedcode.xml",
		references.get(0));
    }

    @Test
    public void testOneFullRuleSetURL() {
	List<RuleSetReferenceId> references = RuleSetReferenceId.parse("file://somepath/rulesets/java/unusedcode.xml");
	assertEquals(1, references.size());
	assertRuleSetReferenceId(true, "file://somepath/rulesets/java/unusedcode.xml", true, null,
		"file://somepath/rulesets/java/unusedcode.xml", references.get(0));
    }

    @Test
    public void testMultipleFullRuleSet() {
	List<RuleSetReferenceId> references = RuleSetReferenceId
		.parse("rulesets/java/unusedcode.xml,rulesets/java/basic.xml");
	assertEquals(2, references.size());
	assertRuleSetReferenceId(true, "rulesets/java/unusedcode.xml", true, null, "rulesets/java/unusedcode.xml",
		references.get(0));
	assertRuleSetReferenceId(true, "rulesets/java/basic.xml", true, null, "rulesets/java/basic.xml", references
		.get(1));
    }

    @Test
    public void testMixRuleSet() {
	List<RuleSetReferenceId> references = RuleSetReferenceId.parse("rulesets/java/unusedcode.xml,xml-basic");
	assertEquals(2, references.size());
	assertRuleSetReferenceId(true, "rulesets/java/unusedcode.xml", true, null, "rulesets/java/unusedcode.xml",
		references.get(0));
	assertRuleSetReferenceId(true, "rulesets/xml/basic.xml", true, null, "rulesets/xml/basic.xml", references
		.get(1));
    }

    @Test
    public void testUnknownRuleSet() {
	List<RuleSetReferenceId> references = RuleSetReferenceId.parse("nonexistant.xml");
	assertEquals(1, references.size());
	assertRuleSetReferenceId(true, "nonexistant.xml", true, null, "nonexistant.xml", references.get(0));
    }

    @Test
    public void testUnknownAndSimpleRuleSet() {
	List<RuleSetReferenceId> references = RuleSetReferenceId.parse("jsp-basic,nonexistant.xml");
	assertEquals(2, references.size());
	assertRuleSetReferenceId(true, "rulesets/jsp/basic.xml", true, null, "rulesets/jsp/basic.xml", references
		.get(0));
	assertRuleSetReferenceId(true, "nonexistant.xml", true, null, "nonexistant.xml", references.get(1));
    }

    @Test
    public void testSimpleRuleSetAndRule() {
	List<RuleSetReferenceId> references = RuleSetReferenceId.parse("java-basic/EmptyCatchBlock");
	assertEquals(1, references.size());
	assertRuleSetReferenceId(true, "rulesets/java/basic.xml", false, "EmptyCatchBlock",
		"rulesets/java/basic.xml/EmptyCatchBlock", references.get(0));
    }

    @Test
    public void testFullRuleSetAndRule() {
	List<RuleSetReferenceId> references = RuleSetReferenceId.parse("rulesets/java/basic.xml/EmptyCatchBlock");
	assertEquals(1, references.size());
	assertRuleSetReferenceId(true, "rulesets/java/basic.xml", false, "EmptyCatchBlock",
		"rulesets/java/basic.xml/EmptyCatchBlock", references.get(0));
    }

    @Test
    public void testFullRuleSetURLAndRule() {
	List<RuleSetReferenceId> references = RuleSetReferenceId
		.parse("file://somepath/rulesets/java/unusedcode.xml/EmptyCatchBlock");
	assertEquals(1, references.size());
	assertRuleSetReferenceId(true, "file://somepath/rulesets/java/unusedcode.xml", false, "EmptyCatchBlock",
		"file://somepath/rulesets/java/unusedcode.xml/EmptyCatchBlock", references.get(0));
    }

    @Test
    public void testInternalRuleSetAndRule() {
	List<RuleSetReferenceId> references = RuleSetReferenceId.parse("EmptyCatchBlock");
	assertEquals(1, references.size());
	assertRuleSetReferenceId(false, null, false, "EmptyCatchBlock", "EmptyCatchBlock", references.get(0));
    }

    @Test
    public void testRelativePathRuleSet() {
        List<RuleSetReferenceId> references = RuleSetReferenceId.parse("pmd/pmd-ruleset.xml");
        assertEquals(1, references.size());
        assertRuleSetReferenceId(true, "pmd/pmd-ruleset.xml", true, null, "pmd/pmd-ruleset.xml", references.get(0));
    }

    @Test
    public void testAbsolutePathRuleSet() {
        List<RuleSetReferenceId> references = RuleSetReferenceId.parse("/home/foo/pmd/pmd-ruleset.xml");
        assertEquals(1, references.size());
        assertRuleSetReferenceId(true, "/home/foo/pmd/pmd-ruleset.xml", true, null,
                "/home/foo/pmd/pmd-ruleset.xml", references.get(0));
    }

    @Test
    public void testFooRules() throws Exception {
        String fooRulesFile = new File("./src/test/resources/net/sourceforge/pmd/rulesets/foo-project/foo-rules").getCanonicalPath();
        List<RuleSetReferenceId> references = RuleSetReferenceId.parse(fooRulesFile);
        assertEquals(1, references.size());
        assertRuleSetReferenceId(true, fooRulesFile, true, null, fooRulesFile, references.get(0));
    }

    public static junit.framework.Test suite() {
	return new junit.framework.JUnit4TestAdapter(RuleSetReferenceIdTest.class);
    }
}
