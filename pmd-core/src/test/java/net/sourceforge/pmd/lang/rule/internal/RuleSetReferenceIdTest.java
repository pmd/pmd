/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.Test;

class RuleSetReferenceIdTest {

    private static void assertRuleSetReferenceId(final boolean expectedAbsolute, final String expectedRuleSetFileName,
            final boolean expectedAllRules, final String expectedRuleName, final String expectedNormalizedReference,
            final RuleSetReferenceId reference) {

        assertEquals(expectedAbsolute, reference.isAbsolute(), "Wrong absolute flag");
        assertEquals(expectedRuleSetFileName, reference.getRuleSetFileName(), "Wrong RuleSet file name");
        assertEquals(expectedAllRules, reference.isAllRules(), "Wrong all Rule reference");
        assertEquals(expectedRuleName, reference.getRuleName(), "Wrong Rule name");
        assertEquals(expectedNormalizedReference, reference.toNormalizedReference(), "Wrong normalized reference");
    }

    @Test
    void testCommaInSingleId() {
        assertThrows(IllegalArgumentException.class, () -> new RuleSetReferenceId("bad,id"));
    }

    @Test
    void testAbsoluteWithAbsolute() {
        assertThrows(IllegalArgumentException.class, () ->
                new RuleSetReferenceId("ruleset.xml/SomeRule", new RuleSetReferenceId("ruleset2.xml")));
    }

    @Test
    void testAbsoluteWithAbsolute2() {
        assertThrows(IllegalArgumentException.class, () ->
                new RuleSetReferenceId("someruleset.xml/SomeRule", new RuleSetReferenceId("someruleset.xml/SomeOtherRule")));
    }

    @Test
    void testRelativeWithRelative() {
        assertThrows(IllegalArgumentException.class, () ->
            new RuleSetReferenceId("SomeRule", new RuleSetReferenceId("SomeOtherRule")));
    }


    @Test
    void testAbsoluteWithRelative() {
        assertThrows(IllegalArgumentException.class, () ->
            new RuleSetReferenceId("someruleset.xml/SomeRule", new RuleSetReferenceId("SomeOtherRule")));
    }

    @Test
    void testRelativeWithAbsolute() {
        assertRuleSetReferenceId(true, "someruleset.xml", false, "SomeRule", "someruleset.xml/SomeRule",
                // This is okay
                new RuleSetReferenceId("SomeRule", new RuleSetReferenceId("someruleset.xml/SomeOtherRule")));
    }

    @Test
    void testNullReference() {
        assertThrows(IllegalArgumentException.class, () -> new RuleSetReferenceId(null));
    }

    @Test
    void testRelativeWithAbsoluteRuleSet() {
        // This is representative of how the RuleSetFactory temporarily pairs a
        // relative reference (rule only) with an absolute reference.
        RuleSetReferenceId relativeRuleSetReferenceId = new RuleSetReferenceId("MockRuleName");
        assertRuleSetReferenceId(false, null, false, "MockRuleName", "MockRuleName", relativeRuleSetReferenceId);
        RuleSetReferenceId absoluteRuleSetReferenceId = new RuleSetReferenceId("rulesets/java/basic.xml");
        assertRuleSetReferenceId(true, "rulesets/java/basic.xml", true, null, "rulesets/java/basic.xml",
                absoluteRuleSetReferenceId);

        RuleSetReferenceId pairRuleSetReferenceId = new RuleSetReferenceId("MockRuleName", absoluteRuleSetReferenceId);
        assertRuleSetReferenceId(true, "rulesets/java/basic.xml", false, "MockRuleName",
                "rulesets/java/basic.xml/MockRuleName", pairRuleSetReferenceId);
    }

    @Test
    void testExamplesFromJavaDoc() {
        assertRuleSetReferenceId(true, "rulesets/java/basic.xml", true, null, "rulesets/java/basic.xml",
                new RuleSetReferenceId("rulesets/java/basic.xml"));
        assertRuleSetReferenceId(true, "rulesets/java/basic.xml", false, "EmptyCatchBlock", "rulesets/java/basic.xml/EmptyCatchBlock",
                new RuleSetReferenceId("rulesets/java/basic.xml/EmptyCatchBlock"));
        assertRuleSetReferenceId(false, null, false, "EmptyCatchBlock", "EmptyCatchBlock",
                new RuleSetReferenceId("EmptyCatchBlock"));
        assertRuleSetReferenceId(true, "https://raw.githubusercontent.com/pmd/pmd/master/pmd-java/src/main/resources/rulesets/java/quickstart.xml", false, "ConstantsInInterface", "https://raw.githubusercontent.com/pmd/pmd/master/pmd-java/src/main/resources/rulesets/java/quickstart.xml/ConstantsInInterface",
                new RuleSetReferenceId("https://raw.githubusercontent.com/pmd/pmd/master/pmd-java/src/main/resources/rulesets/java/quickstart.xml/ConstantsInInterface"));
        assertRuleSetReferenceId(true, "https://example.org/ruleset/MyRule", true, null, "https://example.org/ruleset/MyRule",
                new RuleSetReferenceId("https://example.org/ruleset/MyRule"));
        assertRuleSetReferenceId(true, "https://example.org/ruleset.xml", false, "MyRule", "https://example.org/ruleset.xml/MyRule",
                new RuleSetReferenceId("https://example.org/ruleset.xml/MyRule"));
    }

    @Test
    void testConstructorGivenHttpUrlIdSucceedsAndProcessesIdCorrectly() {
        final String sonarRulesetUrlId = "http://localhost:54321/profiles/export?format=pmd&language=java&name=Sonar%2520way";
        RuleSetReferenceId ruleSetReferenceId = new RuleSetReferenceId("  " + sonarRulesetUrlId + "  ");
        assertRuleSetReferenceId(true, sonarRulesetUrlId, true, null, sonarRulesetUrlId, ruleSetReferenceId);
    }


    @Test
    void testOldSimpleRuleSetReferences() {
        assertRuleSetReferenceId(false, null, false, "dummy-basic", "dummy-basic",
                new RuleSetReferenceId("dummy-basic"));
    }

    @Test
    void testRuleSetReferenceWithSpaces() {
        assertRuleSetReferenceId(false, null, false, "MyRule", "MyRule",
                new RuleSetReferenceId(" MyRule "));
        assertRuleSetReferenceId(true, "ruleset.xml", true, null, "ruleset.xml",
                new RuleSetReferenceId(" ruleset.xml "));
        assertRuleSetReferenceId(true, "ruleset.xml", false, "MyRule", "ruleset.xml/MyRule",
                new RuleSetReferenceId(" ruleset.xml/MyRule "));
    }

    /**
     * See https://sourceforge.net/p/pmd/bugs/1201/
     */
    @Test
    void testMultipleRulesWithSpaces() {
        List<RuleSetReferenceId> references = RuleSetReferenceId.parse("rulesets/dummy/basic.xml, rulesets/dummy/unusedcode.xml, rulesets/dummy2/basic.xml");
        assertEquals(3, references.size());
        assertRuleSetReferenceId(true, "rulesets/dummy/basic.xml", true, null, "rulesets/dummy/basic.xml",
                references.get(0));
        assertRuleSetReferenceId(true, "rulesets/dummy/unusedcode.xml", true, null, "rulesets/dummy/unusedcode.xml",
                references.get(1));
        assertRuleSetReferenceId(true, "rulesets/dummy2/basic.xml", true, null, "rulesets/dummy2/basic.xml",
                references.get(2));
    }

    @Test
    void testOneFullRuleSet() {
        List<RuleSetReferenceId> references = RuleSetReferenceId.parse("rulesets/java/unusedcode.xml");
        assertEquals(1, references.size());
        assertRuleSetReferenceId(true, "rulesets/java/unusedcode.xml", true, null, "rulesets/java/unusedcode.xml",
                references.get(0));
    }

    @Test
    void testOneFullRuleSetURL() {
        List<RuleSetReferenceId> references = RuleSetReferenceId.parse("file://somepath/rulesets/java/unusedcode.xml");
        assertEquals(1, references.size());
        assertRuleSetReferenceId(true, "file://somepath/rulesets/java/unusedcode.xml", true, null,
                "file://somepath/rulesets/java/unusedcode.xml", references.get(0));
    }

    @Test
    void testMultipleFullRuleSet() {
        List<RuleSetReferenceId> references = RuleSetReferenceId
                .parse("rulesets/java/unusedcode.xml,rulesets/java/basic.xml");
        assertEquals(2, references.size());
        assertRuleSetReferenceId(true, "rulesets/java/unusedcode.xml", true, null, "rulesets/java/unusedcode.xml",
                references.get(0));
        assertRuleSetReferenceId(true, "rulesets/java/basic.xml", true, null, "rulesets/java/basic.xml",
                references.get(1));
    }

    @Test
    void testUnknownRuleSet() {
        List<RuleSetReferenceId> references = RuleSetReferenceId.parse("nonexistant.xml");
        assertEquals(1, references.size());
        assertRuleSetReferenceId(true, "nonexistant.xml", true, null, "nonexistant.xml", references.get(0));
    }

    @Test
    void testFullRuleSetAndRule() {
        List<RuleSetReferenceId> references = RuleSetReferenceId.parse("rulesets/java/basic.xml/EmptyCatchBlock");
        assertEquals(1, references.size());
        assertRuleSetReferenceId(true, "rulesets/java/basic.xml", false, "EmptyCatchBlock",
                "rulesets/java/basic.xml/EmptyCatchBlock", references.get(0));
    }

    @Test
    void testFullRuleSetURLAndRule() {
        List<RuleSetReferenceId> references = RuleSetReferenceId
                .parse("file://somepath/rulesets/java/unusedcode.xml/EmptyCatchBlock");
        assertEquals(1, references.size());
        assertRuleSetReferenceId(true, "file://somepath/rulesets/java/unusedcode.xml", false, "EmptyCatchBlock",
                "file://somepath/rulesets/java/unusedcode.xml/EmptyCatchBlock", references.get(0));
    }

    @Test
    void testRelativeRule() {
        List<RuleSetReferenceId> references = RuleSetReferenceId.parse("EmptyCatchBlock");
        assertEquals(1, references.size());
        assertRuleSetReferenceId(false, null, false, "EmptyCatchBlock", "EmptyCatchBlock", references.get(0));
    }

    @Test
    void testRelativePathRuleSet() {
        List<RuleSetReferenceId> references = RuleSetReferenceId.parse("pmd/pmd-ruleset.xml");
        assertEquals(1, references.size());
        assertRuleSetReferenceId(true, "pmd/pmd-ruleset.xml", true, null, "pmd/pmd-ruleset.xml", references.get(0));
    }

    @Test
    void testAbsolutePathRuleSet() {
        List<RuleSetReferenceId> references = RuleSetReferenceId.parse("/home/foo/pmd/pmd-ruleset.xml");
        assertEquals(1, references.size());
        assertRuleSetReferenceId(true, "/home/foo/pmd/pmd-ruleset.xml", true, null, "/home/foo/pmd/pmd-ruleset.xml",
                references.get(0));
    }

    @Test
    void testFooRules() throws Exception {
        String fooRulesFile = new File("./src/test/resources/net/sourceforge/pmd/rulesets/foo-project/foo-rules")
                .getCanonicalPath();
        List<RuleSetReferenceId> references = RuleSetReferenceId.parse(fooRulesFile);
        assertEquals(1, references.size());
        assertRuleSetReferenceId(true, fooRulesFile, true, null, fooRulesFile, references.get(0));
    }

    @Test
    void testNullRulesetString() throws Exception {
        List<RuleSetReferenceId> references = RuleSetReferenceId.parse(null);
        assertTrue(references.isEmpty());
    }
}
