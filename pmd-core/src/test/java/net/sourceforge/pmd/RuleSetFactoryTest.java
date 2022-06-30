/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.rule.MockRule;
import net.sourceforge.pmd.lang.rule.RuleReference;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.util.ResourceLoader;

import com.github.stefanbirkner.systemlambda.SystemLambda;

class RuleSetFactoryTest {

    @Test
    void testRuleSetFileName() {
        RuleSet rs = new RuleSetLoader().loadFromString("dummyRuleset.xml", EMPTY_RULESET);
        assertEquals("dummyRuleset.xml", rs.getFileName());

        rs = new RuleSetLoader().loadFromResource("net/sourceforge/pmd/TestRuleset1.xml");
        assertEquals(rs.getFileName(), "net/sourceforge/pmd/TestRuleset1.xml", "wrong RuleSet file name");
    }

    @Test
    void testRefs() {
        RuleSet rs = new RuleSetLoader().loadFromResource("net/sourceforge/pmd/TestRuleset1.xml");
        assertNotNull(rs.getRuleByName("TestRuleRef"));
    }

    @Test
    void testExtendedReferences() throws Exception {
        InputStream in = new ResourceLoader().loadClassPathResourceAsStream("net/sourceforge/pmd/rulesets/reference-ruleset.xml");
        assertNotNull(in, "Test ruleset not found - can't continue with test!");
        in.close();

        RuleSet rs = new RuleSetLoader().loadFromResource("net/sourceforge/pmd/rulesets/reference-ruleset.xml");
        // added by referencing a complete ruleset (TestRuleset1.xml)
        assertNotNull(rs.getRuleByName("MockRule1"));
        assertNotNull(rs.getRuleByName("MockRule2"));
        assertNotNull(rs.getRuleByName("MockRule3"));
        assertNotNull(rs.getRuleByName("TestRuleRef"));

        // added by specific reference
        assertNotNull(rs.getRuleByName("TestRule"));
        // this is from TestRuleset2.xml, but not referenced
        assertNull(rs.getRuleByName("TestRule2Ruleset2"));

        Rule mockRule3 = rs.getRuleByName("MockRule3");
        assertEquals("Overridden message", mockRule3.getMessage());
        assertEquals(2, mockRule3.getPriority().getPriority());

        Rule mockRule2 = rs.getRuleByName("MockRule2");
        assertEquals("Just combine them!", mockRule2.getMessage());
        // assert that MockRule2 is only once added to the ruleset, so that it
        // really
        // overwrites the configuration inherited from TestRuleset1.xml
        assertNotNull(rs.getRuleByName("MockRule2"));

        Rule mockRule1 = rs.getRuleByName("MockRule1");
        assertNotNull(mockRule1);
        PropertyDescriptor<?> prop = mockRule1.getPropertyDescriptor("testIntProperty");
        Object property = mockRule1.getProperty(prop);
        assertEquals("5", String.valueOf(property));

        // included from TestRuleset3.xml
        assertNotNull(rs.getRuleByName("Ruleset3Rule2"));
        // excluded from TestRuleset3.xml
        assertNull(rs.getRuleByName("Ruleset3Rule1"));

        // overridden to 5
        Rule ruleset4Rule1 = rs.getRuleByName("Ruleset4Rule1");
        assertNotNull(ruleset4Rule1);
        assertEquals(5, ruleset4Rule1.getPriority().getPriority());
        assertNotNull(rs.getRuleByName("Ruleset4Rule1"));
        // priority overridden for whole TestRuleset4 group
        Rule ruleset4Rule2 = rs.getRuleByName("Ruleset4Rule2");
        assertNotNull(ruleset4Rule2);
        assertEquals(2, ruleset4Rule2.getPriority().getPriority());
    }

    @Test
    void testRuleSetNotFound() {
        assertThrows(RuleSetLoadException.class, () -> new RuleSetLoader().loadFromResource("fooooo"));
    }

    @Test
    void testCreateEmptyRuleSet() {
        RuleSet rs = loadRuleSet(EMPTY_RULESET);
        assertEquals("test", rs.getName());
        assertEquals(0, rs.size());
    }

    @Test
    void testSingleRule() {
        RuleSet rs = loadRuleSet(SINGLE_RULE);
        assertEquals(1, rs.size());
        Rule r = rs.getRules().iterator().next();
        assertEquals("MockRuleName", r.getName());
        assertEquals("net.sourceforge.pmd.lang.rule.MockRule", r.getRuleClass());
        assertEquals("avoid the mock rule", r.getMessage());
    }

    @Test
    void testSingleRuleEmptyRef() throws Exception {
        String log = SystemLambda.tapSystemErr(() -> {
            RuleSet rs = loadRuleSet(SINGLE_RULE_EMPTY_REF);
            assertEquals(1, rs.size());

            Rule r = rs.getRules().iterator().next();
            assertEquals("MockRuleName", r.getName());
            assertEquals("net.sourceforge.pmd.lang.rule.MockRule", r.getRuleClass());
            assertEquals("avoid the mock rule", r.getMessage());
        });
        assertThat(log, containsString("Empty ref attribute in ruleset 'test'"));
    }

    @Test
    void testMultipleRules() {
        RuleSet rs = loadRuleSet(MULTIPLE_RULES);
        assertEquals(2, rs.size());
        Set<String> expected = new HashSet<>();
        expected.add("MockRuleName1");
        expected.add("MockRuleName2");
        for (Rule rule : rs.getRules()) {
            assertTrue(expected.contains(rule.getName()));
        }
    }

    @Test
    void testSingleRuleWithPriority() {
        assertEquals(RulePriority.MEDIUM, loadFirstRule(PRIORITY).getPriority());
    }

    @Test
    void testProps() {
        Rule r = loadFirstRule(PROPERTIES);
        assertEquals("bar", r.getProperty(r.getPropertyDescriptor("fooString")));
        assertEquals(3, r.getProperty(r.getPropertyDescriptor("fooInt")));
        assertEquals(true, r.getProperty(r.getPropertyDescriptor("fooBoolean")));
        assertEquals(3.0d, (Double) r.getProperty(r.getPropertyDescriptor("fooDouble")), 0.05);
        assertNull(r.getPropertyDescriptor("BuggleFish"));
        assertNotSame(r.getDescription().indexOf("testdesc2"), -1);
    }

    @Test
    void testStringMultiPropertyDefaultDelimiter() {
        Rule r = loadFirstRule(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<ruleset name=\"the ruleset\">\n  <description>Desc</description>\n"
                + "     <rule name=\"myRule\" message=\"Do not place to this package. Move to \n{0} package/s instead.\" \n"
                + "class=\"net.sourceforge.pmd.lang.rule.XPathRule\" language=\"dummy\">\n"
                + "         <description>Please move your class to the right folder(rest \nfolder)</description>\n"
                + "         <priority>2</priority>\n         <properties>\n             <property name=\"packageRegEx\""
                + " value=\"com.aptsssss|com.abc\" \ntype=\"List[String]\" "
                + "description=\"valid packages\"/>\n         </properties></rule></ruleset>");
        Object propValue = r.getProperty(r.getPropertyDescriptor("packageRegEx"));

        assertEquals(Arrays.asList("com.aptsssss", "com.abc"), propValue);
    }

    @Test
    void testStringMultiPropertyDelimiter() {
        Rule r = loadFirstRule("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<ruleset name=\"test\">\n "
                                   + " <description>ruleset desc</description>\n     "
                                   + "<rule name=\"myRule\" message=\"Do not place to this package. Move to \n{0} package/s"
                                   + " instead.\" \n"
                                   + "class=\"net.sourceforge.pmd.lang.rule.XPathRule\" language=\"dummy\">\n"
                                   + "         <description>Please move your class to the right folder(rest \nfolder)</description>\n"
                                   + "         <priority>2</priority>\n         <properties>\n             <property name=\"packageRegEx\""
                                   + " value=\"com.aptsssss,com.abc\" \ntype=\"List[String]\" delimiter=\",\" "
                                   + "description=\"valid packages\"/>\n"
                                   + "         </properties></rule>" + "</ruleset>");

        Object propValue = r.getProperty(r.getPropertyDescriptor("packageRegEx"));
        assertEquals(Arrays.asList("com.aptsssss", "com.abc"), propValue);
    }

    @Test
    void testRuleSetWithDeprecatedRule() {
        RuleSet rs = loadRuleSet("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<ruleset name=\"ruleset\">\n"
                                     + "  <description>ruleset desc</description>\n"
                                     + "     <rule deprecated=\"true\" ref=\"rulesets/dummy/basic.xml/DummyBasicMockRule\"/>"
                                     + "</ruleset>");
        assertEquals(1, rs.getRules().size());
        Rule rule = rs.getRuleByName("DummyBasicMockRule");
        assertNotNull(rule);
    }

    /**
     * This is an example of a category (built-in) ruleset, which contains a rule, that has been renamed.
     * This means: a rule definition for "NewName" and a rule reference "OldName", that is deprecated
     * and exists for backwards compatibility.
     *
     * <p>When loading this ruleset at a whole, we shouldn't get a deprecation warning. The deprecated
     * rule reference should be ignored, so at the end, we only have the new rule name in the ruleset.
     * This is because the deprecated reference points to a rule in the same ruleset.
     *
     */
    @Test
    void testRuleSetWithDeprecatedButRenamedRule() throws Exception {
        String log = SystemLambda.tapSystemErr(() -> {
            RuleSet rs = loadRuleSetWithDeprecationWarnings(
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<ruleset name=\"test\">\n"
                            + "  <description>ruleset desc</description>\n"
                            + "     <rule deprecated=\"true\" ref=\"NewName\" name=\"OldName\"/>"
                            + "     <rule name=\"NewName\" message=\"m\" class=\"net.sourceforge.pmd.lang.rule.XPathRule\" language=\"dummy\">"
                            + "         <description>d</description>\n" + "         <priority>2</priority>\n" + "     </rule>"
                            + "</ruleset>");
            assertEquals(1, rs.getRules().size());
            Rule rule = rs.getRuleByName("NewName");
            assertNotNull(rule);
            assertNull(rs.getRuleByName("OldName"));
        });
        assertTrue(log.isEmpty());
    }

    /**
     * This is an example of a category (built-in) ruleset, which contains a rule, that has been renamed.
     * This means: a rule definition for "NewName" and a rule reference "OldName", that is deprecated
     * and exists for backwards compatibility.
     *
     * <p>When loading this ruleset at a whole for generating the documentation, we should still
     * include the deprecated rule reference, so that we can create a nice documentation.
     *
     */
    @Test
    void testRuleSetWithDeprecatedRenamedRuleForDoc() {
        RuleSetLoader loader = new RuleSetLoader().includeDeprecatedRuleReferences(true);
        RuleSet rs = loader.loadFromString("",
                                           "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<ruleset name=\"test\">\n"
                                               + "  <description>ruleset desc</description>\n"
                                               + "     <rule deprecated=\"true\" ref=\"NewName\" name=\"OldName\"/>"
                                               + "     <rule name=\"NewName\" message=\"m\" class=\"net.sourceforge.pmd.lang.rule.XPathRule\" language=\"dummy\">"
                                               + "         <description>d</description>\n"
                                               + "         <priority>2</priority>\n"
                                               + "     </rule>"
                                               + "</ruleset>");
        assertEquals(2, rs.getRules().size());
        assertNotNull(rs.getRuleByName("NewName"));
        assertNotNull(rs.getRuleByName("OldName"));
    }

    /**
     * This is an example of a custom user ruleset, that references a rule, that has been renamed.
     * The user should get a deprecation warning.
     */
    @Test
    void testRuleSetReferencesADeprecatedRenamedRule() throws Exception {
        String log = SystemLambda.tapSystemErr(() -> {
            RuleSet rs = loadRuleSetWithDeprecationWarnings(
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<ruleset name=\"test\">\n"
                            + "  <description>ruleset desc</description>\n"
                            + "     <rule ref=\"rulesets/dummy/basic.xml/OldNameOfDummyBasicMockRule\"/>" + "</ruleset>");
            assertEquals(1, rs.getRules().size());
            Rule rule = rs.getRuleByName("OldNameOfDummyBasicMockRule");
            assertNotNull(rule);
        });

        assertEquals(1,
                     StringUtils.countMatches(log,
                                              "WARN net.sourceforge.pmd.RuleSetFactory - Use Rule name rulesets/dummy/basic.xml/DummyBasicMockRule instead of the deprecated Rule name rulesets/dummy/basic.xml/OldNameOfDummyBasicMockRule."));
    }

    /**
     * This is an example of a custom user ruleset, that references a complete (e.g. category) ruleset,
     * that contains a renamed (deprecated) rule and two normal rules and one deprecated rule.
     *
     * <p>
     * The user should not get a deprecation warning for the whole ruleset,
     * since not all rules are deprecated in the referenced ruleset. Although the referenced ruleset contains
     * a deprecated rule, there should be no warning about it, because all deprecated rules are ignored,
     * if a whole ruleset is referenced.
     *
     * <p>
     * In the end, we should get all non-deprecated rules of the referenced ruleset.
     *
     */
    @Test
    void testRuleSetReferencesRulesetWithADeprecatedRenamedRule() throws Exception {
        String log = SystemLambda.tapSystemErr(() -> {
            RuleSet rs = loadRuleSetWithDeprecationWarnings(
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<ruleset name=\"test\">\n"
                            + "  <description>ruleset desc</description>\n"
                            + "     <rule ref=\"rulesets/dummy/basic.xml\"/>" + "</ruleset>");
            assertEquals(2, rs.getRules().size());
            assertNotNull(rs.getRuleByName("DummyBasicMockRule"));
            assertNotNull(rs.getRuleByName("SampleXPathRule"));
        });

        assertTrue(log.isEmpty());
    }

    /**
     * This is an example of a custom user ruleset, that references a complete (e.g. category) ruleset,
     * that contains a renamed (deprecated) rule and two normal rules and one deprecated rule. The deprecated
     * rule is excluded.
     *
     * <p>
     * The user should not get a deprecation warning for the whole ruleset,
     * since not all rules are deprecated in the referenced ruleset. Since the deprecated rule is excluded,
     * there should be no deprecation warning at all, although the deprecated ruleset would have been
     * excluded by default (without explictly excluding it).
     *
     * <p>
     * In the end, we should get all non-deprecated rules of the referenced ruleset.
     *
     */
    @Test
    void testRuleSetReferencesRulesetWithAExcludedDeprecatedRule() throws Exception {
        String log = SystemLambda.tapSystemErr(() -> {
            RuleSet rs = loadRuleSetWithDeprecationWarnings(
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<ruleset name=\"test\">\n"
                            + "  <description>ruleset desc</description>\n"
                            + "     <rule ref=\"rulesets/dummy/basic.xml\"><exclude name=\"DeprecatedRule\"/></rule>"
                            + "</ruleset>");
            assertEquals(2, rs.getRules().size());
            assertNotNull(rs.getRuleByName("DummyBasicMockRule"));
            assertNotNull(rs.getRuleByName("SampleXPathRule"));
        });

        assertTrue(log.isEmpty());
    }

    /**
     * This is an example of a custom user ruleset, that references a complete (e.g. category) ruleset,
     * that contains a renamed (deprecated) rule and two normal rules and one deprecated rule.
     * There is a exclusion of a rule, that no longer exists.
     *
     * <p>
     * The user should not get a deprecation warning for the whole ruleset,
     * since not all rules are deprecated in the referenced ruleset.
     * Since the rule to be excluded doesn't exist, there should be a warning about that.
     *
     */
    @Test
    void testRuleSetReferencesRulesetWithAExcludedNonExistingRule() throws Exception {
        String log = SystemLambda.tapSystemErr(() -> {
            RuleSet rs = loadRuleSetWithDeprecationWarnings(
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<ruleset name=\"test\">\n"
                            + "  <description>ruleset desc</description>\n"
                            + "     <rule ref=\"rulesets/dummy/basic.xml\"><exclude name=\"NonExistingRule\"/></rule>"
                            + "</ruleset>");
            assertEquals(2, rs.getRules().size());
            assertNotNull(rs.getRuleByName("DummyBasicMockRule"));
            assertNotNull(rs.getRuleByName("SampleXPathRule"));
        });

        assertEquals(0,
                     StringUtils.countMatches(log,
                                              "WARN net.sourceforge.pmd.RuleSetFactory - Discontinue using Rule rulesets/dummy/basic.xml/DeprecatedRule as it is scheduled for removal from PMD."));
        assertEquals(1,
                StringUtils.countMatches(log,
                    "WARN net.sourceforge.pmd.RuleSetFactory - Unable to exclude rules [NonExistingRule] from ruleset reference rulesets/dummy/basic.xml; perhaps the rule name is misspelled or the rule doesn't exist anymore?"));
    }

    /**
     * When a custom ruleset references a ruleset that only contains deprecated rules, then this ruleset itself is
     * considered deprecated and the user should get a deprecation warning for the ruleset.
     */
    @Test
    void testRuleSetReferencesDeprecatedRuleset() throws Exception {
        String log = SystemLambda.tapSystemErr(() -> {
            RuleSet rs = loadRuleSetWithDeprecationWarnings(
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<ruleset name=\"test\">\n"
                            + "  <description>ruleset desc</description>\n"
                            + "     <rule ref=\"rulesets/dummy/deprecated.xml\" />" + "</ruleset>");
            assertEquals(2, rs.getRules().size());
            assertNotNull(rs.getRuleByName("DummyBasicMockRule"));
            assertNotNull(rs.getRuleByName("SampleXPathRule"));
        });

        assertEquals(1,
                     StringUtils.countMatches(log,
                                              "WARN net.sourceforge.pmd.RuleSetFactory - The RuleSet rulesets/dummy/deprecated.xml has been deprecated and will be removed in PMD"));
    }

    /**
     * When a custom ruleset references a ruleset that contains both rules and rule references, that are left
     * for backwards compatibility, because the rules have been moved to a different ruleset, then there should be
     * no warning about deprecation - since the deprecated rules are not used.
     */
    @Test
    void testRuleSetReferencesRulesetWithAMovedRule() throws Exception {
        String log = SystemLambda.tapSystemErr(() -> {
            RuleSet rs = loadRuleSetWithDeprecationWarnings(
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<ruleset name=\"test\">\n"
                            + "  <description>ruleset desc</description>\n"
                            + "     <rule ref=\"rulesets/dummy/basic2.xml\" />" + "</ruleset>");
            assertEquals(1, rs.getRules().size());
            assertNotNull(rs.getRuleByName("DummyBasic2MockRule"));
        });

        assertEquals(0,
                     StringUtils.countMatches(log,
                                              "WARN net.sourceforge.pmd.RuleSetFactory - Use Rule name rulesets/dummy/basic.xml/DummyBasicMockRule instead of the deprecated Rule name rulesets/dummy/basic2.xml/DummyBasicMockRule. PMD"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testXPath() {
        Rule r = loadFirstRule(XPATH);
        PropertyDescriptor<String> xpathProperty = (PropertyDescriptor<String>) r.getPropertyDescriptor("xpath");
        assertNotNull(xpathProperty, "xpath property descriptor");
        assertNotSame(r.getProperty(xpathProperty).indexOf(" //Block "), -1);
    }

    @Test
    void testExternalReferenceOverride() {
        Rule r = loadFirstRule(REF_OVERRIDE);
        assertEquals("TestNameOverride", r.getName());
        assertEquals("Test message override", r.getMessage());
        assertEquals("Test description override", r.getDescription());
        assertEquals(2, r.getExamples().size(), "Test that both example are stored");
        assertEquals("Test example override", r.getExamples().get(1));
        assertEquals(RulePriority.MEDIUM, r.getPriority());
        PropertyDescriptor<?> test2Descriptor = r.getPropertyDescriptor("test2");
        assertNotNull(test2Descriptor, "test2 descriptor");
        assertEquals("override2", r.getProperty(test2Descriptor));
        PropertyDescriptor<?> test3Descriptor = r.getPropertyDescriptor("test3");
        assertNotNull(test3Descriptor, "test3 descriptor");
        assertEquals("override3", r.getProperty(test3Descriptor));
    }

    @Test
    void testExternalReferenceOverrideNonExistent() {
        RuleSetLoadException ex = assertCannotParse(REF_OVERRIDE_NONEXISTENT);

        assertThat(ex.getCause().getMessage(), containsString("Cannot set non-existent property 'test4' on Rule TestNameOverride"));
    }

    private RuleSetLoadException assertCannotParse(String xmlContent) {
        return assertThrows(RuleSetLoadException.class, () -> loadFirstRule(xmlContent));
    }

    @Test
    void testReferenceInternalToInternal() {
        RuleSet ruleSet = loadRuleSet(REF_INTERNAL_TO_INTERNAL);

        Rule rule = ruleSet.getRuleByName("MockRuleName");
        assertNotNull(rule, "Could not find Rule MockRuleName");

        Rule ruleRef = ruleSet.getRuleByName("MockRuleNameRef");
        assertNotNull(ruleRef, "Could not find Rule MockRuleNameRef");
    }

    @Test
    void testReferenceInternalToInternalChain() {
        RuleSet ruleSet = loadRuleSet(REF_INTERNAL_TO_INTERNAL_CHAIN);

        Rule rule = ruleSet.getRuleByName("MockRuleName");
        assertNotNull(rule, "Could not find Rule MockRuleName");

        Rule ruleRef = ruleSet.getRuleByName("MockRuleNameRef");
        assertNotNull(ruleRef, "Could not find Rule MockRuleNameRef");

        Rule ruleRefRef = ruleSet.getRuleByName("MockRuleNameRefRef");
        assertNotNull(ruleRefRef, "Could not find Rule MockRuleNameRefRef");
    }

    @Test
    void testReferenceInternalToExternal() {
        RuleSet ruleSet = loadRuleSet(REF_INTERNAL_TO_EXTERNAL);

        Rule rule = ruleSet.getRuleByName("ExternalRefRuleName");
        assertNotNull(rule, "Could not find Rule ExternalRefRuleName");

        Rule ruleRef = ruleSet.getRuleByName("ExternalRefRuleNameRef");
        assertNotNull(ruleRef, "Could not find Rule ExternalRefRuleNameRef");
    }

    @Test
    void testReferenceInternalToExternalChain() {
        RuleSet ruleSet = loadRuleSet(REF_INTERNAL_TO_EXTERNAL_CHAIN);

        Rule rule = ruleSet.getRuleByName("ExternalRefRuleName");
        assertNotNull(rule, "Could not find Rule ExternalRefRuleName");

        Rule ruleRef = ruleSet.getRuleByName("ExternalRefRuleNameRef");
        assertNotNull(ruleRef, "Could not find Rule ExternalRefRuleNameRef");

        Rule ruleRefRef = ruleSet.getRuleByName("ExternalRefRuleNameRefRef");
        assertNotNull(ruleRefRef, "Could not find Rule ExternalRefRuleNameRefRef");
    }

    @Test
    void testReferencePriority() {
        RuleSetLoader config = new RuleSetLoader().warnDeprecated(false).enableCompatibility(true);

        RuleSetLoader rulesetLoader = config.filterAbovePriority(RulePriority.LOW);
        RuleSet ruleSet = rulesetLoader.loadFromString("", REF_INTERNAL_TO_INTERNAL_CHAIN);
        assertEquals(3, ruleSet.getRules().size(), "Number of Rules");
        assertNotNull(ruleSet.getRuleByName("MockRuleName"));
        assertNotNull(ruleSet.getRuleByName("MockRuleNameRef"));
        assertNotNull(ruleSet.getRuleByName("MockRuleNameRefRef"));

        rulesetLoader = config.filterAbovePriority(RulePriority.MEDIUM_HIGH);
        ruleSet = rulesetLoader.loadFromString("", REF_INTERNAL_TO_INTERNAL_CHAIN);
        assertEquals(2, ruleSet.getRules().size(), "Number of Rules");
        assertNotNull(ruleSet.getRuleByName("MockRuleNameRef"));
        assertNotNull(ruleSet.getRuleByName("MockRuleNameRefRef"));

        rulesetLoader = config.filterAbovePriority(RulePriority.HIGH);
        ruleSet = rulesetLoader.loadFromString("", REF_INTERNAL_TO_INTERNAL_CHAIN);
        assertEquals(1, ruleSet.getRules().size(), "Number of Rules");
        assertNotNull(ruleSet.getRuleByName("MockRuleNameRefRef"));

        rulesetLoader = config.filterAbovePriority(RulePriority.LOW);
        ruleSet = rulesetLoader.loadFromString("", REF_INTERNAL_TO_EXTERNAL_CHAIN);
        assertEquals(3, ruleSet.getRules().size(), "Number of Rules");
        assertNotNull(ruleSet.getRuleByName("ExternalRefRuleName"));
        assertNotNull(ruleSet.getRuleByName("ExternalRefRuleNameRef"));
        assertNotNull(ruleSet.getRuleByName("ExternalRefRuleNameRefRef"));

        rulesetLoader = config.filterAbovePriority(RulePriority.MEDIUM_HIGH);
        ruleSet = rulesetLoader.loadFromString("", REF_INTERNAL_TO_EXTERNAL_CHAIN);
        assertEquals(2, ruleSet.getRules().size(), "Number of Rules");
        assertNotNull(ruleSet.getRuleByName("ExternalRefRuleNameRef"));
        assertNotNull(ruleSet.getRuleByName("ExternalRefRuleNameRefRef"));

        rulesetLoader = config.filterAbovePriority(RulePriority.HIGH);
        ruleSet = rulesetLoader.loadFromString("", REF_INTERNAL_TO_EXTERNAL_CHAIN);
        assertEquals(1, ruleSet.getRules().size(), "Number of Rules");
        assertNotNull(ruleSet.getRuleByName("ExternalRefRuleNameRefRef"));
    }

    @Test
    void testOverridePriorityLoadWithMinimum() {
        RuleSetLoader rulesetLoader = new RuleSetLoader().filterAbovePriority(RulePriority.MEDIUM_LOW)
                .warnDeprecated(true).enableCompatibility(true);
        RuleSet ruleset = rulesetLoader.loadFromResource("net/sourceforge/pmd/rulesets/ruleset-minimum-priority.xml");
        // only one rule should remain, since we filter out the other rule by minimum priority
        assertEquals(1, ruleset.getRules().size(), "Number of Rules");

        // Priority is overridden and applied, rule is missing
        assertNull(ruleset.getRuleByName("DummyBasicMockRule"));

        // this is the remaining rule
        assertNotNull(ruleset.getRuleByName("SampleXPathRule"));

        // now, load with default minimum priority
        rulesetLoader = new RuleSetLoader();
        ruleset = rulesetLoader.loadFromResource("net/sourceforge/pmd/rulesets/ruleset-minimum-priority.xml");
        assertEquals(2, ruleset.getRules().size(), "Number of Rules");
        Rule dummyBasicMockRule = ruleset.getRuleByName("DummyBasicMockRule");
        assertEquals(RulePriority.LOW, dummyBasicMockRule.getPriority(), "Wrong Priority");
    }

    @Test
    void testExcludeWithMinimumPriority() {
        RuleSetLoader rulesetLoader = new RuleSetLoader().filterAbovePriority(RulePriority.HIGH);
        RuleSet ruleset = rulesetLoader
                .loadFromResource("net/sourceforge/pmd/rulesets/ruleset-minimum-priority-exclusion.xml");
        // no rules should be loaded
        assertEquals(0, ruleset.getRules().size(), "Number of Rules");

        // now, load with default minimum priority
        rulesetLoader = new RuleSetLoader().filterAbovePriority(RulePriority.LOW);
        ruleset = rulesetLoader.loadFromResource("net/sourceforge/pmd/rulesets/ruleset-minimum-priority-exclusion.xml");
        // only one rule, we have excluded one...
        assertEquals(1, ruleset.getRules().size(), "Number of Rules");
        // rule is excluded
        assertNull(ruleset.getRuleByName("DummyBasicMockRule"));
        // this is the remaining rule
        assertNotNull(ruleset.getRuleByName("SampleXPathRule"));
    }

    @Test
    void testOverrideMessage() {
        Rule r = loadFirstRule(REF_OVERRIDE_ORIGINAL_NAME);
        assertEquals("TestMessageOverride", r.getMessage());
    }

    @Test
    void testOverrideMessageOneElem() {
        Rule r = loadFirstRule(REF_OVERRIDE_ORIGINAL_NAME_ONE_ELEM);
        assertEquals("TestMessageOverride", r.getMessage());
    }

    @Test
    void testIncorrectExternalRef() {
        assertCannotParse(REF_MISSPELLED_XREF);
    }

    @Test
    void testSetPriority() {
        RuleSetLoader rulesetLoader = new RuleSetLoader().filterAbovePriority(RulePriority.MEDIUM_HIGH).warnDeprecated(false);
        assertEquals(0, rulesetLoader.loadFromString("", SINGLE_RULE).size());
        rulesetLoader = new RuleSetLoader().filterAbovePriority(RulePriority.MEDIUM_LOW).warnDeprecated(false);
        assertEquals(1, rulesetLoader.loadFromString("", SINGLE_RULE).size());
    }

    @Test
    void testLanguage() {
        Rule r = loadFirstRule(LANGUAGE);
        assertEquals(LanguageRegistry.getLanguage(DummyLanguageModule.NAME), r.getLanguage());
    }

    @Test
    void testIncorrectLanguage() {
        assertCannotParse(INCORRECT_LANGUAGE);
    }

    @Test
    void testMinimumLanguageVersion() {
        Rule r = loadFirstRule(MINIMUM_LANGUAGE_VERSION);
        assertEquals(LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getVersion("1.4"),
                     r.getMinimumLanguageVersion());
    }

    @Test
    void testIncorrectMinimumLanguageVersion() {
        RuleSetLoadException ex = assertCannotParse(INCORRECT_MINIMUM_LANGUAGE_VERSION);

        assertThat(ex.getCause().getMessage(), containsString("1.0, 1.1, 1.2")); // and not "dummy 1.0, dummy 1.1, ..."

    }

    @Test
    void testIncorrectMinimumLanguageVersionWithLanguageSetInJava() {
        assertCannotParse("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                              + "<ruleset name=\"TODO\">\n"
                              + "    <description>TODO</description>\n"
                              + "\n"
                              + "    <rule name=\"TODO\"\n"
                              + "          message=\"TODO\"\n"
                              + "          class=\"net.sourceforge.pmd.util.FooRuleWithLanguageSetInJava\"\n"
                              + "          minimumLanguageVersion=\"12\">\n"
                              + "        <description>TODO</description>\n"
                              + "        <priority>2</priority>\n"
                              + "    </rule>\n"
                        + "\n"
                        + "</ruleset>");
    }

    @Test
    void testMaximumLanguageVersion() {
        Rule r = loadFirstRule(MAXIMUM_LANGUAGE_VERSION);
        assertEquals(LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getVersion("1.7"),
                     r.getMaximumLanguageVersion());
    }

    @Test
    void testIncorrectMaximumLanguageVersion() {
        RuleSetLoadException ex = assertCannotParse(INCORRECT_MAXIMUM_LANGUAGE_VERSION);

        assertThat(ex.getCause().getMessage(), containsString("1.0, 1.1, 1.2")); // and not "dummy 1.0, dummy 1.1, ..."
    }

    @Test
    void testInvertedMinimumMaximumLanguageVersions() {
        assertCannotParse(INCORRECT_MAXIMUM_LANGUAGE_VERSION);
    }

    @Test
    void testDirectDeprecatedRule() {
        Rule r = loadFirstRule(DIRECT_DEPRECATED_RULE);
        assertNotNull(r, "Direct Deprecated Rule");
        assertTrue(r.isDeprecated());
    }

    @Test
    void testReferenceToDeprecatedRule() {
        Rule r = loadFirstRule(REFERENCE_TO_DEPRECATED_RULE);
        assertNotNull(r, "Reference to Deprecated Rule");
        assertTrue(r instanceof RuleReference, "Rule Reference");
        assertFalse(r.isDeprecated(), "Not deprecated");
        assertTrue(((RuleReference) r).getRule().isDeprecated(), "Original Rule Deprecated");
        assertEquals(r.getName(), DEPRECATED_RULE_NAME, "Rule name");
    }

    @Test
    void testRuleSetReferenceWithDeprecatedRule() {
        RuleSet ruleSet = loadRuleSet(REFERENCE_TO_RULESET_WITH_DEPRECATED_RULE);
        assertNotNull(ruleSet, "RuleSet");
        assertFalse(ruleSet.getRules().isEmpty(), "RuleSet empty");
        // No deprecated Rules should be loaded when loading an entire RuleSet
        // by reference - unless it contains only deprecated rules - then all rules would be added
        Rule r = ruleSet.getRuleByName(DEPRECATED_RULE_NAME);
        assertNull(r, "Deprecated Rule Reference");
        for (Rule rule : ruleSet.getRules()) {
            assertFalse(rule.isDeprecated(), "Rule not deprecated");
        }
    }

    @Test
    void testDeprecatedRuleSetReference() {
        RuleSet ruleSet = new RuleSetLoader().loadFromResource("net/sourceforge/pmd/rulesets/ruleset-deprecated.xml");
        assertEquals(2, ruleSet.getRules().size());
    }

    @Test
    void testExternalReferences() {
        RuleSet rs = loadRuleSet(EXTERNAL_REFERENCE_RULE_SET);
        assertEquals(1, rs.size());
        assertEquals(MockRule.class.getName(), rs.getRuleByName("MockRule").getRuleClass());
    }

    @Test
    void testIncludeExcludePatterns() {
        RuleSet ruleSet = loadRuleSet(INCLUDE_EXCLUDE_RULESET);

        assertNotNull(ruleSet.getFileInclusions(), "Include patterns");
        assertEquals(2, ruleSet.getFileInclusions().size(), "Include patterns size");
        assertEquals("include1", ruleSet.getFileInclusions().get(0).pattern(), "Include pattern #1");
        assertEquals("include2", ruleSet.getFileInclusions().get(1).pattern(), "Include pattern #2");

        assertNotNull(ruleSet.getFileExclusions(), "Exclude patterns");
        assertEquals(3, ruleSet.getFileExclusions().size(), "Exclude patterns size");
        assertEquals("exclude1", ruleSet.getFileExclusions().get(0).pattern(), "Exclude pattern #1");
        assertEquals("exclude2", ruleSet.getFileExclusions().get(1).pattern(), "Exclude pattern #2");
        assertEquals("exclude3", ruleSet.getFileExclusions().get(2).pattern(), "Exclude pattern #3");
    }

    /**
     * Rule reference can't be resolved - ref is used instead of class and the
     * class is old (pmd 4.3 and not pmd 5).
     */
    @Test
    void testBug1202() {
        assertThrows(
            RuleSetLoadException.class,
            () -> new RuleSetLoader().loadFromString("", "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<ruleset>\n"
                + "  <rule ref=\"net.sourceforge.pmd.rules.XPathRule\">\n" + "    <priority>1</priority>\n"
                + "    <properties>\n" + "      <property name=\"xpath\" value=\"//TypeDeclaration\" />\n"
                + "      <property name=\"message\" value=\"Foo\" />\n" + "    </properties>\n" + "  </rule>\n"
                + "</ruleset>\n")
        );
    }

    /**
     * See https://sourceforge.net/p/pmd/bugs/1225/
     */
    @Test
    void testEmptyRuleSetFile() {
        RuleSet ruleset = new RuleSetLoader().loadFromString("", "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "\n"
            + "<ruleset name=\"Custom ruleset\" xmlns=\"http://pmd.sourceforge.net/ruleset/2.0.0\"\n"
            + "    xmlns:xsi=\"http:www.w3.org/2001/XMLSchema-instance\"\n"
            + "    xsi:schemaLocation=\"http://pmd.sourceforge.net/ruleset/2.0.0 https://pmd.sourceforge.io/ruleset_2_0_0.xsd\">\n"
            + "    <description>PMD Ruleset.</description>\n" + "\n"
            + "    <exclude-pattern>.*Test.*</exclude-pattern>\n" + "\n" + "</ruleset>\n");
        assertEquals(0, ruleset.getRules().size());
    }

    /**
     * See https://github.com/pmd/pmd/issues/782
     * Empty ruleset should be interpreted as deprecated.
     */
    @Test
    void testEmptyRuleSetReferencedShouldNotBeDeprecated() throws Exception {
        String log = SystemLambda.tapSystemErr(() -> {
            RuleSet ruleset = new RuleSetLoader().loadFromString("", "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "\n"
                    + "<ruleset name=\"Custom ruleset\" xmlns=\"http://pmd.sourceforge.net/ruleset/2.0.0\"\n"
                    + "    xmlns:xsi=\"http:www.w3.org/2001/XMLSchema-instance\"\n"
                    + "    xsi:schemaLocation=\"http://pmd.sourceforge.net/ruleset/2.0.0 https://pmd.sourceforge.io/ruleset_2_0_0.xsd\">\n"
                    + "    <description>Ruleset which references a empty ruleset</description>\n" + "\n"
                    + "    <rule ref=\"rulesets/dummy/empty-ruleset.xml\" />\n"
                    + "</ruleset>\n");
            assertEquals(0, ruleset.getRules().size());
        });

        assertTrue(log.isEmpty());
    }

    /**
     * See https://sourceforge.net/p/pmd/bugs/1231/
     */
    @Test
    void testWrongRuleNameReferenced() {
        assertCannotParse("<?xml version=\"1.0\"?>\n"
                              + "<ruleset name=\"Custom ruleset for tests\"\n"
                              + "    xmlns=\"http://pmd.sourceforge.net/ruleset/2.0.0\"\n"
                              + "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                              + "    xsi:schemaLocation=\"http://pmd.sourceforge.net/ruleset/2.0.0 https://pmd.sourceforge.io/ruleset_2_0_0.xsd\">\n"
                              + "  <description>Custom ruleset for tests</description>\n"
                              + "  <rule ref=\"net/sourceforge/pmd/TestRuleset1.xml/ThisRuleDoesNotExist\"/>\n"
                              + "</ruleset>\n");
    }

    /**
     * Unit test for #1312 see https://sourceforge.net/p/pmd/bugs/1312/
     *
     */
    @Test
    void testRuleReferenceWithNameOverridden() {
        RuleSet rs = loadRuleSet("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                                     + "<ruleset xmlns=\"http://pmd.sourceforge.net/ruleset/2.0.0\"\n"
                                     + "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                                     + "         name=\"pmd-eclipse\"\n"
                                     + "         xsi:schemaLocation=\"http://pmd.sourceforge.net/ruleset/2.0.0 https://pmd.sourceforge.io/ruleset_2_0_0.xsd\">\n"
                                     + "   <description>PMD Plugin preferences rule set</description>\n"
                                     + "<rule name=\"OverriddenDummyBasicMockRule\"\n"
                                     + "    ref=\"rulesets/dummy/basic.xml/DummyBasicMockRule\">\n" + "</rule>\n" + "\n"
                                     + "</ruleset>");

        Rule r = rs.getRules().iterator().next();
        assertEquals("OverriddenDummyBasicMockRule", r.getName());
        RuleReference ruleRef = (RuleReference) r;
        assertEquals("DummyBasicMockRule", ruleRef.getRule().getName());
    }

    /**
     * See https://sourceforge.net/p/pmd/bugs/1231/
     *
     * <p>See https://github.com/pmd/pmd/issues/1978 - with that, it should not be an error anymore.
     *
     */
    @Test
    void testWrongRuleNameExcluded() {
        RuleSet ruleset = loadRuleSet("<?xml version=\"1.0\"?>\n" + "<ruleset name=\"Custom ruleset for tests\"\n"
                                          + "    xmlns=\"http://pmd.sourceforge.net/ruleset/2.0.0\"\n"
                                          + "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                                          + "    xsi:schemaLocation=\"http://pmd.sourceforge.net/ruleset/2.0.0 https://pmd.sourceforge.io/ruleset_2_0_0.xsd\">\n"
                                          + "  <description>Custom ruleset for tests</description>\n"
                                          + "  <rule ref=\"net/sourceforge/pmd/TestRuleset1.xml\">\n"
                                          + "    <exclude name=\"ThisRuleDoesNotExist\"/>\n" + "  </rule>\n"
                                          + "</ruleset>\n");
        assertEquals(4, ruleset.getRules().size());
    }

    /**
     * This unit test manifests the current behavior - which might change in the
     * future. See #1537.
     *
     * Currently, if a ruleset is imported twice, the excludes of the first
     * import are ignored. Duplicated rules are silently ignored.
     *
     * @see <a href="https://sourceforge.net/p/pmd/bugs/1537/">#1537 Implement
     *      strict ruleset parsing</a>
     * @see <a href=
     *      "http://stackoverflow.com/questions/40299075/custom-pmd-ruleset-not-working">stackoverflow
     *      - custom ruleset not working</a>
     */
    @Test
    void testExcludeAndImportTwice() {
        RuleSet ruleset = loadRuleSet("<?xml version=\"1.0\"?>\n" + "<ruleset name=\"Custom ruleset for tests\"\n"
                                          + "    xmlns=\"http://pmd.sourceforge.net/ruleset/2.0.0\"\n"
                                          + "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                                          + "    xsi:schemaLocation=\"http://pmd.sourceforge.net/ruleset/2.0.0 https://pmd.sourceforge.io/ruleset_2_0_0.xsd\">\n"
                                          + "  <description>Custom ruleset for tests</description>\n"
                                          + "  <rule ref=\"rulesets/dummy/basic.xml\">\n"
                                          + "    <exclude name=\"DummyBasicMockRule\"/>\n"
                                          + "  </rule>\n" + "</ruleset>\n");
        assertNull(ruleset.getRuleByName("DummyBasicMockRule"));

        RuleSet ruleset2 = loadRuleSet("<?xml version=\"1.0\"?>\n" + "<ruleset name=\"Custom ruleset for tests\"\n"
                                           + "    xmlns=\"http://pmd.sourceforge.net/ruleset/2.0.0\"\n"
                                           + "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                                           + "    xsi:schemaLocation=\"http://pmd.sourceforge.net/ruleset/2.0.0 https://pmd.sourceforge.io/ruleset_2_0_0.xsd\">\n"
                                           + "  <description>Custom ruleset for tests</description>\n"
                                           + "  <rule ref=\"rulesets/dummy/basic.xml\">\n"
                                           + "    <exclude name=\"DummyBasicMockRule\"/>\n"
                                           + "  </rule>\n" + "  <rule ref=\"rulesets/dummy/basic.xml\"/>\n"
                                           + "</ruleset>\n");
        assertNotNull(ruleset2.getRuleByName("DummyBasicMockRule"));

        RuleSet ruleset3 = loadRuleSet("<?xml version=\"1.0\"?>\n" + "<ruleset name=\"Custom ruleset for tests\"\n"
                                           + "    xmlns=\"http://pmd.sourceforge.net/ruleset/2.0.0\"\n"
                                           + "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                                           + "    xsi:schemaLocation=\"http://pmd.sourceforge.net/ruleset/2.0.0 https://pmd.sourceforge.io/ruleset_2_0_0.xsd\">\n"
                                           + "  <description>Custom ruleset for tests</description>\n"
                                           + "  <rule ref=\"rulesets/dummy/basic.xml\"/>\n"
                                           + "  <rule ref=\"rulesets/dummy/basic.xml\">\n"
                                           + "    <exclude name=\"DummyBasicMockRule\"/>\n" + "  </rule>\n"
                                           + "</ruleset>\n");
        assertNotNull(ruleset3.getRuleByName("DummyBasicMockRule"));
    }

    @Test
    void testMissingRuleSetNameIsWarning() throws Exception {
        String log = SystemLambda.tapSystemErr(() -> {
            loadRuleSetWithDeprecationWarnings(
                    "<?xml version=\"1.0\"?>\n" + "<ruleset \n"
                            + "    xmlns=\"http://pmd.sourceforge.net/ruleset/2.0.0\"\n"
                            + "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                            + "    xsi:schemaLocation=\"http://pmd.sourceforge.net/ruleset/2.0.0 https://pmd.sourceforge.io/ruleset_2_0_0.xsd\">\n"
                            + "  <description>Custom ruleset for tests</description>\n"
                            + "  <rule ref=\"rulesets/dummy/basic.xml\"/>\n"
                            + "  </ruleset>\n"
            );
        });

        assertTrue(log.contains("RuleSet name is missing."));
    }

    @Test
    void testMissingRuleSetDescriptionIsWarning() throws Exception {
        String log = SystemLambda.tapSystemErr(() -> {
            loadRuleSetWithDeprecationWarnings(
                    "<?xml version=\"1.0\"?>\n" + "<ruleset name=\"then name\"\n"
                            + "    xmlns=\"http://pmd.sourceforge.net/ruleset/2.0.0\"\n"
                            + "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                            + "    xsi:schemaLocation=\"http://pmd.sourceforge.net/ruleset/2.0.0 https://pmd.sourceforge.io/ruleset_2_0_0.xsd\">\n"
                            + "  <rule ref=\"rulesets/dummy/basic.xml\"/>\n"
                            + "  </ruleset>\n"
            );
        });
        assertTrue(log.contains("RuleSet description is missing."));
    }

    @Test
    void testDeprecatedRulesetReferenceProducesWarning() throws Exception {
        String log = SystemLambda.tapSystemErr(() -> {
            loadRuleSetWithDeprecationWarnings(
                    "<?xml version=\"1.0\"?>\n" + "<ruleset \n"
                            + "    name='foo'"
                            + "    xmlns=\"http://pmd.sourceforge.net/ruleset/2.0.0\"\n"
                            + "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                            + "    xsi:schemaLocation=\"http://pmd.sourceforge.net/ruleset/2.0.0 https://pmd.sourceforge.io/ruleset_2_0_0.xsd\">\n"
                            + "  <description>Custom ruleset for tests</description>\n"
                            + "  <rule ref=\"dummy-basic\"/>\n"
                            + "  </ruleset>\n");
        });

        assertThat(log, containsString("Ruleset reference 'dummy-basic' uses a deprecated form, use 'rulesets/dummy/basic.xml' instead"));
    }

    private static final String REF_OVERRIDE_ORIGINAL_NAME = "<?xml version=\"1.0\"?>\n"
        + "<ruleset name=\"test\">\n"
        + " <description>testdesc</description>\n"
        + " <rule \n"
        + "\n"
        + "  ref=\"net/sourceforge/pmd/TestRuleset1.xml/MockRule1\" message=\"TestMessageOverride\"> \n"
        + "\n"
        + " </rule>\n"
        + "</ruleset>";

    private static final String REF_MISSPELLED_XREF = "<?xml version=\"1.0\"?>\n"
        + "<ruleset name=\"test\">\n"
        + "\n"
        + " <description>testdesc</description>\n"
        + " <rule \n"
        + "  ref=\"net/sourceforge/pmd/TestRuleset1.xml/FooMockRule1\"> \n"
        + " </rule>\n"
        + "</ruleset>";

    private static final String REF_OVERRIDE_ORIGINAL_NAME_ONE_ELEM = "<?xml version=\"1.0\"?>\n"
        + "<ruleset name=\"test\">\n"
        + " <description>testdesc</description>\n"
        + " <rule ref=\"net/sourceforge/pmd/TestRuleset1.xml/MockRule1\" message=\"TestMessageOverride\"/> \n"
        + "\n"
        + "</ruleset>";

    private static final String REF_OVERRIDE = "<?xml version=\"1.0\"?>\n"
        + "<ruleset name=\"test\">\n"
        + " <description>testdesc</description>\n"
        + " <rule \n"
        + "  ref=\"net/sourceforge/pmd/TestRuleset1.xml/MockRule4\" \n"
        + "  name=\"TestNameOverride\" \n"
        + "\n"
        + "  message=\"Test message override\"> \n"
        + "  <description>Test description override</description>\n"
        + "  <example>Test example override</example>\n"
        + "  <priority>3</priority>\n"
        + "  <properties>\n"
        + "   <property name=\"test2\" description=\"test2\" type=\"String\" value=\"override2\"/>\n"
        + "   <property name=\"test3\" type=\"String\" description=\"test3\"><value>override3</value></property>\n"
        + "\n"
        + "  </properties>\n"
        + " </rule>\n"
        + "</ruleset>";

    private static final String REF_OVERRIDE_NONEXISTENT = "<?xml version=\"1.0\"?>\n"
        + "<ruleset name=\"test\">\n"
        + "\n"
        + " <description>testdesc</description>\n"
        + " <rule \n"
        + "  ref=\"net/sourceforge/pmd/TestRuleset1.xml/MockRule4\" \n"
        + "  name=\"TestNameOverride\" \n"
        + "\n"
        + "  message=\"Test message override\"> \n"
        + "  <description>Test description override</description>\n"
        + "  <example>Test example override</example>\n"
        + "  <priority>3</priority>\n"
        + "  <properties>\n"
        + "   <property name=\"test4\" description=\"test4\" type=\"String\" value=\"new property\"/>\n"
        + "  </properties>\n"
        + " </rule>\n"
        + "</ruleset>";

    private static final String REF_INTERNAL_TO_INTERNAL = "<?xml version=\"1.0\"?>\n"
        + "<ruleset name=\"test\">\n"
        + " <description>testdesc</description>\n"
        + "<rule \n"
        + "\n"
        + "language=\"dummy\" \n"
        + "name=\"MockRuleName\" \n"
        + "message=\"avoid the mock rule\" \n"
        + "class=\"net.sourceforge.pmd.lang.rule.MockRule\">\n"
        + "</rule>\n"
        + " <rule ref=\"MockRuleName\" name=\"MockRuleNameRef\"/> \n"
        + "</ruleset>";

    private static final String REF_INTERNAL_TO_INTERNAL_CHAIN = "<?xml version=\"1.0\"?>\n"
        + "<ruleset name=\"test\">\n"
        + " <description>testdesc</description>\n"
        + "<rule \n"
        + "\n"
        + "language=\"dummy\" \n"
        + "name=\"MockRuleName\" \n"
        + "message=\"avoid the mock rule\" \n"
        + "class=\"net.sourceforge.pmd.lang.rule.MockRule\">\n"
        + "</rule>\n"
        + " <rule ref=\"MockRuleName\" name=\"MockRuleNameRef\"><priority>2</priority></rule> \n"
        + " <rule ref=\"MockRuleNameRef\" name=\"MockRuleNameRefRef\"><priority>1</priority></rule> \n"
        + "</ruleset>";

    private static final String REF_INTERNAL_TO_EXTERNAL = "<?xml version=\"1.0\"?>\n"
        + "<ruleset name=\"test\">\n"
        + " <description>testdesc</description>\n"
        + "<rule \n"
        + "\n"
        + "name=\"ExternalRefRuleName\" \n"
        + "ref=\"net/sourceforge/pmd/TestRuleset1.xml/MockRule1\"/>\n"
        + " <rule ref=\"ExternalRefRuleName\" name=\"ExternalRefRuleNameRef\"/> \n"
        + "</ruleset>";

    private static final String REF_INTERNAL_TO_EXTERNAL_CHAIN = "<?xml version=\"1.0\"?>\n"
        + "<ruleset name=\"test\">\n"
        + " <description>testdesc</description>\n"
        + "<rule \n"
        + "\n"
        + "name=\"ExternalRefRuleName\" \n"
        + "ref=\"net/sourceforge/pmd/TestRuleset2.xml/TestRule\"/>\n"
        + " <rule ref=\"ExternalRefRuleName\" name=\"ExternalRefRuleNameRef\"><priority>2</priority></rule> \n"
        + "\n"
        + " <rule ref=\"ExternalRefRuleNameRef\" name=\"ExternalRefRuleNameRefRef\"><priority>1</priority></rule> \n"
        + "\n"
        + "</ruleset>";

    private static final String EMPTY_RULESET = "<?xml version=\"1.0\"?>\n<ruleset name=\"test\">\n<description>testdesc</description>\n</ruleset>";

    private static final String SINGLE_RULE = "<?xml version=\"1.0\"?>\n"
        + "<ruleset name=\"test\">\n"
        + "<description>testdesc</description>\n"
        + "<rule \n"
        + "language=\"dummy\" \n"
        + "name=\"MockRuleName\" \n"
        + "message=\"avoid the mock rule\" \n"
        + "class=\"net.sourceforge.pmd.lang.rule.MockRule\">\n"
        + "<priority>3</priority>\n"
        + "</rule></ruleset>";

    private static final String SINGLE_RULE_EMPTY_REF = "<?xml version=\"1.0\"?>\n"
        + "<ruleset name=\"test\">\n"
        + "<description>testdesc</description>\n"
        + "<rule \n"
        + "language=\"dummy\" \n"
        + "ref=\"\" \n"
        + "name=\"MockRuleName\" \n"
        + "message=\"avoid the mock rule\" \n"
        + "class=\"net.sourceforge.pmd.lang.rule.MockRule\">\n"
        + "<priority>3</priority>\n"
        + "</rule></ruleset>";

    private static final String MULTIPLE_RULES = "<?xml version=\"1.0\"?>\n"
        + "<ruleset name=\"test\">\n"
        + "\n"
        + "<description>testdesc</description>\n"
        + "<rule name=\"MockRuleName1\" \n"
        + "language=\"dummy\" \n"
        + "message=\"avoid the mock rule\" \n"
        + "class=\"net.sourceforge.pmd.lang.rule.MockRule\">\n"
        + "\n"
        + "</rule>\n"
        + "<rule name=\"MockRuleName2\" \n"
        + "language=\"dummy\" \n"
        + "message=\"avoid the mock rule\" \n"
        + "class=\"net.sourceforge.pmd.lang.rule.MockRule\">\n"
        + "\n"
        + "</rule></ruleset>";

    private static final String PROPERTIES = "<?xml version=\"1.0\"?>\n"
        + "<ruleset name=\"test\">\n"
        + "<description>testdesc</description>\n"
        + "<rule name=\"MockRuleName\" \n"
        + "language=\"dummy\" \n"
        + "message=\"avoid the mock rule\" \n"
        + "class=\"net.sourceforge.pmd.lang.rule.MockRule\">\n"
        + "\n"
        + "<description>testdesc2</description>\n"
        + "<properties>\n"
        + "<property name=\"fooBoolean\" description=\"test\" type=\"Boolean\" value=\"true\" />\n"
        + "<property name=\"fooChar\" description=\"test\" type=\"Character\" value=\"B\" />\n"
        + "<property name=\"fooInt\" description=\"test\" type=\"Integer\" min=\"1\" max=\"10\" value=\"3\" />\n"
        + "\n"
        + "<property name=\"fooFloat\" description=\"test\" type=\"Float\" min=\"1.0\" max=\"1.0\" value=\"1.0\"  />\n"
        + "\n"
        + "<property name=\"fooDouble\" description=\"test\" type=\"Double\" min=\"1.0\" max=\"9.0\" value=\"3.0\"  />\n"
        + "\n"
        + "<property name=\"fooString\" description=\"test\" type=\"String\" value=\"bar\" />\n"
        + "</properties>\n"
        + "</rule></ruleset>";

    private static final String XPATH = "<?xml version=\"1.0\"?>\n"
        + "<ruleset name=\"test\">\n"
        + "<description>testdesc</description>\n"
        + "<rule name=\"MockRuleName\" \n"
        + "language=\"dummy\" \n"
        + "message=\"avoid the mock rule\" \n"
        + "class=\"net.sourceforge.pmd.lang.rule.MockRule\">\n"
        + "<priority>3</priority>\n"
        + "\n"
        + "<description>testdesc2</description>\n"
        + "<properties>\n"
        + "<property name=\"xpath\" description=\"test\" type=\"String\">\n"
        + "<value>\n"
        + "<![CDATA[ //Block ]]>\n"
        + "</value>\n"
        + "</property>\n"
        + "</properties>\n"
        + "</rule></ruleset>";

    private static final String PRIORITY = "<?xml version=\"1.0\"?>\n"
        + "<ruleset name=\"test\">\n"
        + "<description>testdesc</description>\n"
        + "<rule \n"
        + "language=\"dummy\" \n"
        + "name=\"MockRuleName\" \n"
        + "message=\"avoid the mock rule\" \n"
        + "class=\"net.sourceforge.pmd.lang.rule.MockRule\">\n"
        + "<priority>3</priority>\n"
        + "</rule></ruleset>";

    private static final String LANGUAGE = "<?xml version=\"1.0\"?>\n"
        + "<ruleset name=\"test\">\n"
        + "<description>testdesc</description>\n"
        + "<rule \n"
        + "name=\"MockRuleName\" \n"
        + "message=\"avoid the mock rule\" \n"
        + "class=\"net.sourceforge.pmd.lang.rule.MockRule\" "
        + "language=\"dummy\">\n"
        + "</rule></ruleset>";

    private static final String INCORRECT_LANGUAGE = "<?xml version=\"1.0\"?>\n"
        + "<ruleset name=\"test\">\n"
        + "\n"
        + "<description>testdesc</description>\n"
        + "<rule \n"
        + "name=\"MockRuleName\" \n"
        + "\n"
        + "message=\"avoid the mock rule\" \n"
        + "class=\"net.sourceforge.pmd.lang.rule.MockRule\"\n"
        + " language=\"bogus\">\n"
        + "</rule></ruleset>";

    private static final String MINIMUM_LANGUAGE_VERSION = "<?xml version=\"1.0\"?>\n"
        + "<ruleset name=\"test\">\n"
        + "<description>testdesc</description>\n"
        + "<rule \n"
        + "name=\"MockRuleName\" \n"
        + "message=\"avoid the mock rule\" \n"
        + "class=\"net.sourceforge.pmd.lang.rule.MockRule\"\n"
        + " language=\"dummy\"\n"
        + " minimumLanguageVersion=\"1.4\">\n"
        + "</rule></ruleset>";

    private static final String INCORRECT_MINIMUM_LANGUAGE_VERSION = "<?xml version=\"1.0\"?>\n"
        + "<ruleset name=\"test\">\n"
        + "<description>testdesc</description>\n"
        + "<rule \n"
        + "name=\"MockRuleName\" \n"
        + "message=\"avoid the mock rule\" \n"
        + "class=\"net.sourceforge.pmd.lang.rule.MockRule\"\n"
        + " language=\"dummy\"\n"
        + " minimumLanguageVersion=\"bogus\">\n"
        + "</rule></ruleset>";

    private static final String MAXIMUM_LANGUAGE_VERSION = "<?xml version=\"1.0\"?>\n"
        + "<ruleset name=\"test\">\n"
        + "<description>testdesc</description>\n"
        + "<rule \n"
        + "name=\"MockRuleName\" \n"
        + "message=\"avoid the mock rule\" \n"
        + "class=\"net.sourceforge.pmd.lang.rule.MockRule\"\n"
        + " language=\"dummy\"\n"
        + " maximumLanguageVersion=\"1.7\">\n"
        + "</rule></ruleset>";

    private static final String INCORRECT_MAXIMUM_LANGUAGE_VERSION = "<?xml version=\"1.0\"?>\n"
        + "<ruleset name=\"test\">\n"
        + "<description>testdesc</description>\n"
        + "<rule \n"
        + "name=\"MockRuleName\" \n"
        + "message=\"avoid the mock rule\" \n"
        + "class=\"net.sourceforge.pmd.lang.rule.MockRule\"\n"
        + " language=\"dummy\"\n"
        + " maximumLanguageVersion=\"bogus\">\n"
        + "</rule></ruleset>";

    private static final String INVERTED_MINIMUM_MAXIMUM_LANGUAGE_VERSIONS = "<?xml version=\"1.0\"?>\n"
        + "<ruleset name=\"test\">\n"
        + "<description>testdesc</description>\n"
        + "<rule \n"
        + "name=\"MockRuleName\" \n"
        + "message=\"avoid the mock rule\" \n"
        + "class=\"net.sourceforge.pmd.lang.rule.MockRule\" \n"
        + "language=\"dummy\"\n"
        + " minimumLanguageVersion=\"1.7\"\n"
        + "maximumLanguageVersion=\"1.4\">\n"
        + "</rule></ruleset>";

    private static final String DIRECT_DEPRECATED_RULE = "<?xml version=\"1.0\"?>\n"
        + "<ruleset name=\"test\">\n"
        + "\n"
        + "<description>testdesc</description>\n"
        + "<rule \n"
        + "language=\"dummy\" \n"
        + "name=\"MockRuleName\" \n"
        + "message=\"avoid the mock rule\" \n"
        + "class=\"net.sourceforge.pmd.lang.rule.MockRule\" deprecated=\"true\">\n"
        + "</rule></ruleset>";

    // Note: Update this RuleSet name to a different RuleSet with deprecated
    // Rules when the Rules are finally removed.
    private static final String DEPRECATED_RULE_RULESET_NAME = "net/sourceforge/pmd/TestRuleset1.xml";

    // Note: Update this Rule name to a different deprecated Rule when the one
    // listed here is finally removed.
    private static final String DEPRECATED_RULE_NAME = "MockRule3";

    private static final String REFERENCE_TO_DEPRECATED_RULE = "<?xml version=\"1.0\"?>\n"
        + "<ruleset name=\"test\">\n"
        + "<description>testdesc</description>\n"
        + "<rule " + "ref=\"" + DEPRECATED_RULE_RULESET_NAME + "/" + DEPRECATED_RULE_NAME + "\" />\n"
        + "</ruleset>";

    private static final String REFERENCE_TO_RULESET_WITH_DEPRECATED_RULE = "<?xml version=\"1.0\"?>\n"
        + "<ruleset name=\"test\">\n"
        + "<description>testdesc</description>\n"
        + "<rule " + "ref=\"" + DEPRECATED_RULE_RULESET_NAME + "\" />\n"
        + "</ruleset>";

    private static final String DFA = "<?xml version=\"1.0\"?>\n"
        + "<ruleset name=\"test\">\n"
        + "<description>testdesc</description>\n"
        + "<rule \n"
        + "language=\"dummy\" \n"
        + "name=\"MockRuleName\" \n"
        + "message=\"avoid the mock rule\" \n"
        + "dfa=\"true\" \n"
        + "class=\"net.sourceforge.pmd.lang.rule.MockRule\"><priority>3</priority>\n"
        + "</rule></ruleset>";

    private static final String INCLUDE_EXCLUDE_RULESET = "<?xml version=\"1.0\"?>\n"
        + "<ruleset name=\"test\">\n"
        + "<description>testdesc</description>\n"
        + "<include-pattern>include1</include-pattern>\n"
        + "<include-pattern>include2</include-pattern>\n"
        + "\n"
        + "<exclude-pattern>exclude1</exclude-pattern>\n"
        + "<exclude-pattern>exclude2</exclude-pattern>\n"
        + "<exclude-pattern>exclude3</exclude-pattern>\n"
        + "\n"
        + "</ruleset>";

    private static final String EXTERNAL_REFERENCE_RULE_SET = "<?xml version=\"1.0\"?>\n"
        + "<ruleset name=\"test\">\n"
        + "<description>testdesc</description>\n"
        + "<rule ref=\"net/sourceforge/pmd/external-reference-ruleset.xml/MockRule\"/>\n"
        + "</ruleset>";

    private Rule loadFirstRule(String ruleSetXml) {
        RuleSet rs = loadRuleSet(ruleSetXml);
        return rs.getRules().iterator().next();
    }

    private RuleSet loadRuleSet(String ruleSetXml) {
        return new RuleSetLoader().loadFromString("dummyRuleset.xml", ruleSetXml);
    }

    private RuleSet loadRuleSetWithDeprecationWarnings(String ruleSetXml) {
        return new RuleSetLoader().warnDeprecated(true).enableCompatibility(false).loadFromString("testRuleset.xml", ruleSetXml);
    }

}
