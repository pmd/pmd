/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sourceforge.pmd.junit.JavaUtilLoggingRule;
import net.sourceforge.pmd.junit.LocaleRule;
import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.rule.MockRule;
import net.sourceforge.pmd.lang.rule.RuleReference;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.util.ResourceLoader;

public class RuleSetFactoryTest {

    @org.junit.Rule
    public ExpectedException ex = ExpectedException.none();

    @org.junit.Rule
    public LocaleRule localeRule = LocaleRule.en();

    @Test
    public void testRuleSetFileName() throws RuleSetNotFoundException {
        RuleSet rs = loadRuleSet(EMPTY_RULESET);
        assertNull("RuleSet file name not expected", rs.getFileName());

        RuleSetFactory rsf = RulesetsFactoryUtils.defaultFactory();
        rs = rsf.createRuleSet("net/sourceforge/pmd/TestRuleset1.xml");
        assertEquals("wrong RuleSet file name", rs.getFileName(), "net/sourceforge/pmd/TestRuleset1.xml");
    }

    @Test
    public void testNoRuleSetFileName() throws RuleSetNotFoundException {
        RuleSet rs = loadRuleSet(EMPTY_RULESET);
        assertNull("RuleSet file name not expected", rs.getFileName());
    }

    @Test
    public void testRefs() throws Exception {
        RuleSetFactory rsf = RulesetsFactoryUtils.defaultFactory();
        RuleSet rs = rsf.createRuleSet("net/sourceforge/pmd/TestRuleset1.xml");
        assertNotNull(rs.getRuleByName("TestRuleRef"));
    }

    @Test
    public void testExtendedReferences() throws Exception {
        InputStream in = new ResourceLoader().loadClassPathResourceAsStream("net/sourceforge/pmd/rulesets/reference-ruleset.xml");
        assertNotNull("Test ruleset not found - can't continue with test!", in);
        in.close();

        RuleSetFactory rsf = RulesetsFactoryUtils.defaultFactory();
        RuleSets rs = rsf.createRuleSets("net/sourceforge/pmd/rulesets/reference-ruleset.xml");
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
        assertEquals(1, countRule(rs, "MockRule2"));

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
        assertEquals(1, countRule(rs, "Ruleset4Rule1"));
        // priority overridden for whole TestRuleset4 group
        Rule ruleset4Rule2 = rs.getRuleByName("Ruleset4Rule2");
        assertNotNull(ruleset4Rule2);
        assertEquals(2, ruleset4Rule2.getPriority().getPriority());
    }

    private int countRule(RuleSets rs, String ruleName) {
        int count = 0;
        for (Rule r : rs.getAllRules()) {
            if (ruleName.equals(r.getName())) {
                count++;
            }
        }
        return count;
    }

    @Test(expected = RuleSetNotFoundException.class)
    public void testRuleSetNotFound() throws RuleSetNotFoundException {
        RuleSetFactory rsf = RulesetsFactoryUtils.defaultFactory();
        rsf.createRuleSet("fooooo");
    }

    @Test
    public void testCreateEmptyRuleSet() throws RuleSetNotFoundException {
        RuleSet rs = loadRuleSet(EMPTY_RULESET);
        assertEquals("test", rs.getName());
        assertEquals(0, rs.size());
    }

    @Test
    public void testSingleRule() throws RuleSetNotFoundException {
        RuleSet rs = loadRuleSet(SINGLE_RULE);
        assertEquals(1, rs.size());
        Rule r = rs.getRules().iterator().next();
        assertEquals("MockRuleName", r.getName());
        assertEquals("net.sourceforge.pmd.lang.rule.MockRule", r.getRuleClass());
        assertEquals("avoid the mock rule", r.getMessage());
    }

    @Test
    public void testMultipleRules() throws RuleSetNotFoundException {
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
    public void testSingleRuleWithPriority() throws RuleSetNotFoundException {
        assertEquals(RulePriority.MEDIUM, loadFirstRule(PRIORITY).getPriority());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testProps() throws RuleSetNotFoundException {
        Rule r = loadFirstRule(PROPERTIES);
        assertEquals("bar", r.getProperty((PropertyDescriptor<String>) r.getPropertyDescriptor("fooString")));
        assertEquals(new Integer(3), r.getProperty((PropertyDescriptor<Integer>) r.getPropertyDescriptor("fooInt")));
        assertTrue(r.getProperty((PropertyDescriptor<Boolean>) r.getPropertyDescriptor("fooBoolean")));
        assertEquals(3.0d, r.getProperty((PropertyDescriptor<Double>) r.getPropertyDescriptor("fooDouble")), 0.05);
        assertNull(r.getPropertyDescriptor("BuggleFish"));
        assertNotSame(r.getDescription().indexOf("testdesc2"), -1);
    }

    @Test
    public void testStringMultiPropertyDefaultDelimiter() throws Exception {
        Rule r = loadFirstRule("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<ruleset name=\"the ruleset\">\n  <description>Desc</description>\n"
                                   + "     <rule name=\"myRule\" message=\"Do not place to this package. Move to \n{0} package/s instead.\" \n"
                                   + "class=\"net.sourceforge.pmd.lang.rule.XPathRule\" language=\"dummy\">\n"
                                   + "         <description>Please move your class to the right folder(rest \nfolder)</description>\n"
                                   + "         <priority>2</priority>\n         <properties>\n             <property name=\"packageRegEx\""
                                   + " value=\"com.aptsssss|com.abc\" \ntype=\"List[String]\" "
                                   + "description=\"valid packages\"/>\n         </properties></rule></ruleset>");
        PropertyDescriptor<List<String>> prop = (PropertyDescriptor<List<String>>) r.getPropertyDescriptor("packageRegEx");
        List<String> values = r.getProperty(prop);
        assertEquals(Arrays.asList("com.aptsssss", "com.abc"), values);
    }

    @Test
    public void testStringMultiPropertyDelimiter() throws Exception {
        Rule r = loadFirstRule("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<ruleset name=\"test\">\n "
                                   + " <description>ruleset desc</description>\n     "
                                   + "<rule name=\"myRule\" message=\"Do not place to this package. Move to \n{0} package/s"
                                   + " instead.\" \n" + "class=\"net.sourceforge.pmd.lang.rule.XPathRule\" language=\"dummy\">\n"
                                   + "         <description>Please move your class to the right folder(rest \nfolder)</description>\n"
                                   + "         <priority>2</priority>\n         <properties>\n             <property name=\"packageRegEx\""
                                   + " value=\"com.aptsssss,com.abc\" \ntype=\"List[String]\" delimiter=\",\" "
                                   + "description=\"valid packages\"/>\n"
                                   + "         </properties></rule>" + "</ruleset>");
        PropertyDescriptor<List<String>> prop = (PropertyDescriptor<List<String>>) r.getPropertyDescriptor("packageRegEx");
        List<String> values = r.getProperty(prop);
        assertEquals(Arrays.asList("com.aptsssss", "com.abc"), values);
    }

    @Test
    public void testRuleSetWithDeprecatedRule() throws Exception {
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
     * @throws Exception
     */
    @Test
    public void testRuleSetWithDeprecatedButRenamedRule() throws Exception {
        RuleSet rs = loadRuleSetWithDeprecationWarnings("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<ruleset name=\"test\">\n"
                + "  <description>ruleset desc</description>\n"
                + "     <rule deprecated=\"true\" ref=\"NewName\" name=\"OldName\"/>"
                + "     <rule name=\"NewName\" message=\"m\" class=\"net.sourceforge.pmd.lang.rule.XPathRule\" language=\"dummy\">"
                + "         <description>d</description>\n" + "         <priority>2</priority>\n" + "     </rule>"
                + "</ruleset>");
        assertEquals(1, rs.getRules().size());
        Rule rule = rs.getRuleByName("NewName");
        assertNotNull(rule);
        assertNull(rs.getRuleByName("OldName"));

        assertTrue(logging.getLog().isEmpty());
    }

    /**
     * This is an example of a custom user ruleset, that references a rule, that has been renamed.
     * The user should get a deprecation warning.
     *
     * @throws Exception
     */
    @Test
    public void testRuleSetReferencesADeprecatedRenamedRule() throws Exception {
        RuleSet rs = loadRuleSetWithDeprecationWarnings("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<ruleset name=\"test\">\n"
                + "  <description>ruleset desc</description>\n"
                + "     <rule ref=\"rulesets/dummy/basic.xml/OldNameOfDummyBasicMockRule\"/>" + "</ruleset>");
        assertEquals(1, rs.getRules().size());
        Rule rule = rs.getRuleByName("OldNameOfDummyBasicMockRule");
        assertNotNull(rule);

        assertEquals(1,
            StringUtils.countMatches(logging.getLog(),
                "WARNING: Use Rule name rulesets/dummy/basic.xml/DummyBasicMockRule instead of the deprecated Rule name rulesets/dummy/basic.xml/OldNameOfDummyBasicMockRule."));
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
     * @throws Exception
     */
    @Test
    public void testRuleSetReferencesRulesetWithADeprecatedRenamedRule() throws Exception {
        RuleSet rs = loadRuleSetWithDeprecationWarnings("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<ruleset name=\"test\">\n"
                + "  <description>ruleset desc</description>\n"
                + "     <rule ref=\"rulesets/dummy/basic.xml\"/>" + "</ruleset>");
        assertEquals(2, rs.getRules().size());
        assertNotNull(rs.getRuleByName("DummyBasicMockRule"));
        assertNotNull(rs.getRuleByName("SampleXPathRule"));

        assertTrue(logging.getLog().isEmpty());
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
     * @throws Exception
     */
    @Test
    public void testRuleSetReferencesRulesetWithAExcludedDeprecatedRule() throws Exception {
        RuleSet rs = loadRuleSetWithDeprecationWarnings("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<ruleset name=\"test\">\n"
                + "  <description>ruleset desc</description>\n"
                + "     <rule ref=\"rulesets/dummy/basic.xml\"><exclude name=\"DeprecatedRule\"/></rule>" + "</ruleset>");
        assertEquals(2, rs.getRules().size());
        assertNotNull(rs.getRuleByName("DummyBasicMockRule"));
        assertNotNull(rs.getRuleByName("SampleXPathRule"));

        assertTrue(logging.getLog().isEmpty());
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
     * @throws Exception
     */
    @Test
    public void testRuleSetReferencesRulesetWithAExcludedNonExistingRule() throws Exception {
        RuleSet rs = loadRuleSetWithDeprecationWarnings("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<ruleset name=\"test\">\n"
                + "  <description>ruleset desc</description>\n"
                + "     <rule ref=\"rulesets/dummy/basic.xml\"><exclude name=\"NonExistingRule\"/></rule>" + "</ruleset>");
        assertEquals(2, rs.getRules().size());
        assertNotNull(rs.getRuleByName("DummyBasicMockRule"));
        assertNotNull(rs.getRuleByName("SampleXPathRule"));

        assertEquals(0,
                StringUtils.countMatches(logging.getLog(),
                    "WARNING: Discontinue using Rule rulesets/dummy/basic.xml/DeprecatedRule as it is scheduled for removal from PMD."));
        assertEquals(1,
                StringUtils.countMatches(logging.getLog(),
                    "WARNING: Unable to exclude rules [NonExistingRule] from ruleset reference rulesets/dummy/basic.xml; perhaps the rule name is mispelled or the rule doesn't exist anymore?"));
    }

    /**
     * When a custom ruleset references a ruleset that only contains deprecated rules, then this ruleset itself is
     * considered deprecated and the user should get a deprecation warning for the ruleset.
     */
    @Test
    public void testRuleSetReferencesDeprecatedRuleset() throws Exception {
        RuleSet rs = loadRuleSetWithDeprecationWarnings("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<ruleset name=\"test\">\n"
                + "  <description>ruleset desc</description>\n"
                + "     <rule ref=\"rulesets/dummy/deprecated.xml\" />" + "</ruleset>");
        assertEquals(2, rs.getRules().size());
        assertNotNull(rs.getRuleByName("DummyBasicMockRule"));
        assertNotNull(rs.getRuleByName("SampleXPathRule"));

        assertEquals(1,
                StringUtils.countMatches(logging.getLog(),
                    "WARNING: The RuleSet rulesets/dummy/deprecated.xml has been deprecated and will be removed in PMD"));
    }

    /**
     * When a custom ruleset references a ruleset that contains both rules and rule references, that are left
     * for backwards compatibility, because the rules have been moved to a different ruleset, then there should be
     * no warning about deprecation - since the deprecated rules are not used.
     */
    @Test
    public void testRuleSetReferencesRulesetWithAMovedRule() throws Exception {
        RuleSet rs = loadRuleSetWithDeprecationWarnings("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<ruleset name=\"test\">\n"
                + "  <description>ruleset desc</description>\n"
                + "     <rule ref=\"rulesets/dummy/basic2.xml\" />" + "</ruleset>");
        assertEquals(1, rs.getRules().size());
        assertNotNull(rs.getRuleByName("DummyBasic2MockRule"));

        assertEquals(0,
                StringUtils.countMatches(logging.getLog(),
                    "WARNING: Use Rule name rulesets/dummy/basic.xml/DummyBasicMockRule instead of the deprecated Rule name rulesets/dummy/basic2.xml/DummyBasicMockRule. PMD"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testXPath() throws RuleSetNotFoundException {
        Rule r = loadFirstRule(XPATH);
        PropertyDescriptor<String> xpathProperty = (PropertyDescriptor<String>) r.getPropertyDescriptor("xpath");
        assertNotNull("xpath property descriptor", xpathProperty);
        assertNotSame(r.getProperty(xpathProperty).indexOf(" //Block "), -1);
    }

    @Test
    public void testFacadesOffByDefault() throws RuleSetNotFoundException {
        Rule r = loadFirstRule(XPATH);
        assertFalse(r.isDfa());
    }

    @Test
    public void testDFAFlag() throws RuleSetNotFoundException {
        assertTrue(loadFirstRule(DFA).isDfa());
    }

    @Test
    public void testExternalReferenceOverride() throws RuleSetNotFoundException {
        Rule r = loadFirstRule(REF_OVERRIDE);
        assertEquals("TestNameOverride", r.getName());
        assertEquals("Test message override", r.getMessage());
        assertEquals("Test description override", r.getDescription());
        assertEquals("Test that both example are stored", 2, r.getExamples().size());
        assertEquals("Test example override", r.getExamples().get(1));
        assertEquals(RulePriority.MEDIUM, r.getPriority());
        PropertyDescriptor<?> test2Descriptor = r.getPropertyDescriptor("test2");
        assertNotNull("test2 descriptor", test2Descriptor);
        assertEquals("override2", r.getProperty(test2Descriptor));
        PropertyDescriptor<?> test3Descriptor = r.getPropertyDescriptor("test3");
        assertNotNull("test3 descriptor", test3Descriptor);
        assertEquals("override3", r.getProperty(test3Descriptor));
    }

    @Test
    public void testExternalReferenceOverrideNonExistent() throws RuleSetNotFoundException {
        ex.expect(IllegalArgumentException.class);
        ex.expectMessage("Cannot set non-existent property 'test4' on Rule TestNameOverride");
        loadFirstRule(REF_OVERRIDE_NONEXISTENT);
    }

    @Test
    public void testReferenceInternalToInternal() throws RuleSetNotFoundException {
        RuleSet ruleSet = loadRuleSet(REF_INTERNAL_TO_INTERNAL);

        Rule rule = ruleSet.getRuleByName("MockRuleName");
        assertNotNull("Could not find Rule MockRuleName", rule);

        Rule ruleRef = ruleSet.getRuleByName("MockRuleNameRef");
        assertNotNull("Could not find Rule MockRuleNameRef", ruleRef);
    }

    @Test
    public void testReferenceInternalToInternalChain() throws RuleSetNotFoundException {
        RuleSet ruleSet = loadRuleSet(REF_INTERNAL_TO_INTERNAL_CHAIN);

        Rule rule = ruleSet.getRuleByName("MockRuleName");
        assertNotNull("Could not find Rule MockRuleName", rule);

        Rule ruleRef = ruleSet.getRuleByName("MockRuleNameRef");
        assertNotNull("Could not find Rule MockRuleNameRef", ruleRef);

        Rule ruleRefRef = ruleSet.getRuleByName("MockRuleNameRefRef");
        assertNotNull("Could not find Rule MockRuleNameRefRef", ruleRefRef);
    }

    @Test
    public void testReferenceInternalToExternal() throws RuleSetNotFoundException {
        RuleSet ruleSet = loadRuleSet(REF_INTERNAL_TO_EXTERNAL);

        Rule rule = ruleSet.getRuleByName("ExternalRefRuleName");
        assertNotNull("Could not find Rule ExternalRefRuleName", rule);

        Rule ruleRef = ruleSet.getRuleByName("ExternalRefRuleNameRef");
        assertNotNull("Could not find Rule ExternalRefRuleNameRef", ruleRef);
    }

    @Test
    public void testReferenceInternalToExternalChain() throws RuleSetNotFoundException {
        RuleSet ruleSet = loadRuleSet(REF_INTERNAL_TO_EXTERNAL_CHAIN);

        Rule rule = ruleSet.getRuleByName("ExternalRefRuleName");
        assertNotNull("Could not find Rule ExternalRefRuleName", rule);

        Rule ruleRef = ruleSet.getRuleByName("ExternalRefRuleNameRef");
        assertNotNull("Could not find Rule ExternalRefRuleNameRef", ruleRef);

        Rule ruleRefRef = ruleSet.getRuleByName("ExternalRefRuleNameRefRef");
        assertNotNull("Could not find Rule ExternalRefRuleNameRefRef", ruleRefRef);
    }

    @Test
    public void testReferencePriority() throws RuleSetNotFoundException {
        ResourceLoader rl = new ResourceLoader();
        RuleSetFactory rsf = new RuleSetFactory(rl, RulePriority.LOW, false, true);

        RuleSet ruleSet = rsf.createRuleSet(createRuleSetReferenceId(REF_INTERNAL_TO_INTERNAL_CHAIN));
        assertEquals("Number of Rules", 3, ruleSet.getRules().size());
        assertNotNull(ruleSet.getRuleByName("MockRuleName"));
        assertNotNull(ruleSet.getRuleByName("MockRuleNameRef"));
        assertNotNull(ruleSet.getRuleByName("MockRuleNameRefRef"));

        rsf = new RuleSetFactory(rl, RulePriority.MEDIUM_HIGH, false, true);
        ruleSet = rsf.createRuleSet(createRuleSetReferenceId(REF_INTERNAL_TO_INTERNAL_CHAIN));
        assertEquals("Number of Rules", 2, ruleSet.getRules().size());
        assertNotNull(ruleSet.getRuleByName("MockRuleNameRef"));
        assertNotNull(ruleSet.getRuleByName("MockRuleNameRefRef"));

        rsf = new RuleSetFactory(rl, RulePriority.HIGH, false, true);
        ruleSet = rsf.createRuleSet(createRuleSetReferenceId(REF_INTERNAL_TO_INTERNAL_CHAIN));
        assertEquals("Number of Rules", 1, ruleSet.getRules().size());
        assertNotNull(ruleSet.getRuleByName("MockRuleNameRefRef"));

        rsf = new RuleSetFactory(rl, RulePriority.LOW, false, true);
        ruleSet = rsf.createRuleSet(createRuleSetReferenceId(REF_INTERNAL_TO_EXTERNAL_CHAIN));
        assertEquals("Number of Rules", 3, ruleSet.getRules().size());
        assertNotNull(ruleSet.getRuleByName("ExternalRefRuleName"));
        assertNotNull(ruleSet.getRuleByName("ExternalRefRuleNameRef"));
        assertNotNull(ruleSet.getRuleByName("ExternalRefRuleNameRefRef"));

        rsf = new RuleSetFactory(rl, RulePriority.MEDIUM_HIGH, false, true);
        ruleSet = rsf.createRuleSet(createRuleSetReferenceId(REF_INTERNAL_TO_EXTERNAL_CHAIN));
        assertEquals("Number of Rules", 2, ruleSet.getRules().size());
        assertNotNull(ruleSet.getRuleByName("ExternalRefRuleNameRef"));
        assertNotNull(ruleSet.getRuleByName("ExternalRefRuleNameRefRef"));

        rsf = new RuleSetFactory(rl, RulePriority.HIGH, false, true);
        ruleSet = rsf.createRuleSet(createRuleSetReferenceId(REF_INTERNAL_TO_EXTERNAL_CHAIN));
        assertEquals("Number of Rules", 1, ruleSet.getRules().size());
        assertNotNull(ruleSet.getRuleByName("ExternalRefRuleNameRefRef"));
    }

    @Test
    public void testOverridePriorityLoadWithMinimum() throws RuleSetNotFoundException {
        RuleSetFactory rsf = new RuleSetFactory(new ResourceLoader(), RulePriority.MEDIUM_LOW, true, true);
        RuleSet ruleset = rsf.createRuleSet("net/sourceforge/pmd/rulesets/ruleset-minimum-priority.xml");
        // only one rule should remain, since we filter out the other rule by minimum priority
        assertEquals("Number of Rules", 1, ruleset.getRules().size());

        // Priority is overridden and applied, rule is missing
        assertNull(ruleset.getRuleByName("DummyBasicMockRule"));

        // this is the remaining rule
        assertNotNull(ruleset.getRuleByName("SampleXPathRule"));

        // now, load with default minimum priority
        rsf = RulesetsFactoryUtils.defaultFactory();
        ruleset = rsf.createRuleSet("net/sourceforge/pmd/rulesets/ruleset-minimum-priority.xml");
        assertEquals("Number of Rules", 2, ruleset.getRules().size());
        Rule dummyBasicMockRule = ruleset.getRuleByName("DummyBasicMockRule");
        assertEquals("Wrong Priority", RulePriority.LOW, dummyBasicMockRule.getPriority());
    }

    @Test
    public void testExcludeWithMinimumPriority() throws RuleSetNotFoundException {
        RuleSetFactory rsf = RulesetsFactoryUtils.createFactory(RulePriority.HIGH, true, true);
        RuleSet ruleset = rsf.createRuleSet("net/sourceforge/pmd/rulesets/ruleset-minimum-priority-exclusion.xml");
        // no rules should be loaded
        assertEquals("Number of Rules", 0, ruleset.getRules().size());

        // now, load with default minimum priority
        rsf = RulesetsFactoryUtils.defaultFactory();
        ruleset = rsf.createRuleSet("net/sourceforge/pmd/rulesets/ruleset-minimum-priority-exclusion.xml");
        // only one rule, we have excluded one...
        assertEquals("Number of Rules", 1, ruleset.getRules().size());
        // rule is excluded
        assertNull(ruleset.getRuleByName("DummyBasicMockRule"));
        // this is the remaining rule
        assertNotNull(ruleset.getRuleByName("SampleXPathRule"));
    }

    @Test
    public void testOverrideMessage() throws RuleSetNotFoundException {
        Rule r = loadFirstRule(REF_OVERRIDE_ORIGINAL_NAME);
        assertEquals("TestMessageOverride", r.getMessage());
    }

    @Test
    public void testOverrideMessageOneElem() throws RuleSetNotFoundException {
        Rule r = loadFirstRule(REF_OVERRIDE_ORIGINAL_NAME_ONE_ELEM);
        assertEquals("TestMessageOverride", r.getMessage());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIncorrectExternalRef() throws IllegalArgumentException, RuleSetNotFoundException {
        loadFirstRule(REF_MISPELLED_XREF);
    }

    @Test
    public void testSetPriority() throws RuleSetNotFoundException {
        ResourceLoader rl = new ResourceLoader();
        RuleSetFactory rsf = new RuleSetFactory(rl, RulePriority.MEDIUM_HIGH, false, true);
        assertEquals(0, rsf.createRuleSet(createRuleSetReferenceId(SINGLE_RULE)).size());
        rsf = new RuleSetFactory(rl, RulePriority.MEDIUM_LOW, false, true);
        assertEquals(1, rsf.createRuleSet(createRuleSetReferenceId(SINGLE_RULE)).size());
    }

    @Test
    public void testLanguage() throws RuleSetNotFoundException {
        Rule r = loadFirstRule(LANGUAGE);
        assertEquals(LanguageRegistry.getLanguage(DummyLanguageModule.NAME), r.getLanguage());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIncorrectLanguage() throws RuleSetNotFoundException {
        loadFirstRule(INCORRECT_LANGUAGE);
    }

    @Test
    public void testMinimumLanugageVersion() throws RuleSetNotFoundException {
        Rule r = loadFirstRule(MINIMUM_LANGUAGE_VERSION);
        assertEquals(LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getVersion("1.4"),
                r.getMinimumLanguageVersion());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIncorrectMinimumLanugageVersion() throws RuleSetNotFoundException {
        loadFirstRule(INCORRECT_MINIMUM_LANGUAGE_VERSION);
    }

    @Test
    public void testMaximumLanugageVersion() throws RuleSetNotFoundException {
        Rule r = loadFirstRule(MAXIMUM_LANGUAGE_VERSION);
        assertEquals(LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getVersion("1.7"),
                r.getMaximumLanguageVersion());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIncorrectMaximumLanugageVersion() throws RuleSetNotFoundException {
        loadFirstRule(INCORRECT_MAXIMUM_LANGUAGE_VERSION);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvertedMinimumMaximumLanugageVersions() throws RuleSetNotFoundException {
        loadFirstRule(INVERTED_MINIMUM_MAXIMUM_LANGUAGE_VERSIONS);
    }

    @Test
    public void testDirectDeprecatedRule() throws RuleSetNotFoundException {
        Rule r = loadFirstRule(DIRECT_DEPRECATED_RULE);
        assertNotNull("Direct Deprecated Rule", r);
        assertTrue(r.isDeprecated());
    }

    @Test
    public void testReferenceToDeprecatedRule() throws RuleSetNotFoundException {
        Rule r = loadFirstRule(REFERENCE_TO_DEPRECATED_RULE);
        assertNotNull("Reference to Deprecated Rule", r);
        assertTrue("Rule Reference", r instanceof RuleReference);
        assertFalse("Not deprecated", r.isDeprecated());
        assertTrue("Original Rule Deprecated", ((RuleReference) r).getRule().isDeprecated());
        assertEquals("Rule name", r.getName(), DEPRECATED_RULE_NAME);
    }

    @Test
    public void testRuleSetReferenceWithDeprecatedRule() throws RuleSetNotFoundException {
        RuleSet ruleSet = loadRuleSet(REFERENCE_TO_RULESET_WITH_DEPRECATED_RULE);
        assertNotNull("RuleSet", ruleSet);
        assertFalse("RuleSet empty", ruleSet.getRules().isEmpty());
        // No deprecated Rules should be loaded when loading an entire RuleSet
        // by reference - unless it contains only deprecated rules - then all rules would be added
        Rule r = ruleSet.getRuleByName(DEPRECATED_RULE_NAME);
        assertNull("Deprecated Rule Reference", r);
        for (Rule rule : ruleSet.getRules()) {
            assertFalse("Rule not deprecated", rule.isDeprecated());
        }
    }

    @Test
    public void testDeprecatedRuleSetReference() throws RuleSetNotFoundException {
        RuleSetFactory ruleSetFactory = RulesetsFactoryUtils.defaultFactory();
        RuleSet ruleSet = ruleSetFactory.createRuleSet("net/sourceforge/pmd/rulesets/ruleset-deprecated.xml");
        assertEquals(2, ruleSet.getRules().size());
    }

    @Test
    public void testExternalReferences() throws RuleSetNotFoundException {
        RuleSet rs = loadRuleSet(EXTERNAL_REFERENCE_RULE_SET);
        assertEquals(1, rs.size());
        assertEquals(MockRule.class.getName(), rs.getRuleByName("MockRule").getRuleClass());
    }

    @Test
    public void testIncludeExcludePatterns() throws RuleSetNotFoundException {
        RuleSet ruleSet = loadRuleSet(INCLUDE_EXCLUDE_RULESET);

        assertNotNull("Include patterns", ruleSet.getIncludePatterns());
        assertEquals("Include patterns size", 2, ruleSet.getIncludePatterns().size());
        assertEquals("Include pattern #1", "include1", ruleSet.getIncludePatterns().get(0));
        assertEquals("Include pattern #2", "include2", ruleSet.getIncludePatterns().get(1));

        assertNotNull("Exclude patterns", ruleSet.getExcludePatterns());
        assertEquals("Exclude patterns size", 3, ruleSet.getExcludePatterns().size());
        assertEquals("Exclude pattern #1", "exclude1", ruleSet.getExcludePatterns().get(0));
        assertEquals("Exclude pattern #2", "exclude2", ruleSet.getExcludePatterns().get(1));
        assertEquals("Exclude pattern #3", "exclude3", ruleSet.getExcludePatterns().get(2));
    }

    /**
     * Rule reference can't be resolved - ref is used instead of class and the
     * class is old (pmd 4.3 and not pmd 5).
     *
     * @throws Exception
     *             any error
     */
    @Test(expected = RuleSetNotFoundException.class)
    public void testBug1202() throws Exception {
        RuleSetReferenceId ref = createRuleSetReferenceId("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<ruleset>\n"
                + "  <rule ref=\"net.sourceforge.pmd.rules.XPathRule\">\n" + "    <priority>1</priority>\n"
                + "    <properties>\n" + "      <property name=\"xpath\" value=\"//TypeDeclaration\" />\n"
                + "      <property name=\"message\" value=\"Foo\" />\n" + "    </properties>\n" + "  </rule>\n"
                + "</ruleset>\n");
        RuleSetFactory ruleSetFactory = RulesetsFactoryUtils.defaultFactory();
        ruleSetFactory.createRuleSet(ref);
    }

    /**
     * See https://sourceforge.net/p/pmd/bugs/1225/
     *
     * @throws Exception
     *             any error
     */
    @Test
    public void testEmptyRuleSetFile() throws Exception {
        RuleSetReferenceId ref = createRuleSetReferenceId("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "\n"
                + "<ruleset name=\"Custom ruleset\" xmlns=\"http://pmd.sourceforge.net/ruleset/2.0.0\"\n"
                + "    xmlns:xsi=\"http:www.w3.org/2001/XMLSchema-instance\"\n"
                + "    xsi:schemaLocation=\"http://pmd.sourceforge.net/ruleset/2.0.0 https://pmd.sourceforge.io/ruleset_2_0_0.xsd\">\n"
                + "    <description>PMD Ruleset.</description>\n" + "\n"
                + "    <exclude-pattern>.*Test.*</exclude-pattern>\n" + "\n" + "</ruleset>\n");
        RuleSetFactory ruleSetFactory = RulesetsFactoryUtils.defaultFactory();
        RuleSet ruleset = ruleSetFactory.createRuleSet(ref);
        assertEquals(0, ruleset.getRules().size());
    }

    /**
     * See https://github.com/pmd/pmd/issues/782
     * Empty ruleset should be interpreted as deprecated.
     *
     * @throws Exception
     *             any error
     */
    @Test
    public void testEmptyRuleSetReferencedShouldNotBeDeprecated() throws Exception {
        RuleSetReferenceId ref = createRuleSetReferenceId("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "\n"
                + "<ruleset name=\"Custom ruleset\" xmlns=\"http://pmd.sourceforge.net/ruleset/2.0.0\"\n"
                + "    xmlns:xsi=\"http:www.w3.org/2001/XMLSchema-instance\"\n"
                + "    xsi:schemaLocation=\"http://pmd.sourceforge.net/ruleset/2.0.0 https://pmd.sourceforge.io/ruleset_2_0_0.xsd\">\n"
                + "    <description>Ruleset which references a empty ruleset</description>\n" + "\n"
                + "    <rule ref=\"rulesets/dummy/empty-ruleset.xml\" />\n"
                + "</ruleset>\n");
        RuleSetFactory ruleSetFactory = new RuleSetFactory(new ResourceLoader(), RulePriority.LOW, true, true);
        RuleSet ruleset = ruleSetFactory.createRuleSet(ref);
        assertEquals(0, ruleset.getRules().size());

        assertTrue(logging.getLog().isEmpty());
    }

    /**
     * See https://sourceforge.net/p/pmd/bugs/1231/
     *
     * @throws Exception
     *             any error
     */
    @Test(expected = IllegalArgumentException.class)
    public void testWrongRuleNameReferenced() throws Exception {
        RuleSetReferenceId ref = createRuleSetReferenceId("<?xml version=\"1.0\"?>\n"
                + "<ruleset name=\"Custom ruleset for tests\"\n"
                + "    xmlns=\"http://pmd.sourceforge.net/ruleset/2.0.0\"\n"
                + "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                + "    xsi:schemaLocation=\"http://pmd.sourceforge.net/ruleset/2.0.0 https://pmd.sourceforge.io/ruleset_2_0_0.xsd\">\n"
                + "  <description>Custom ruleset for tests</description>\n"
                + "  <rule ref=\"net/sourceforge/pmd/TestRuleset1.xml/ThisRuleDoesNotExist\"/>\n" + "</ruleset>\n");
        RuleSetFactory ruleSetFactory = RulesetsFactoryUtils.defaultFactory();
        ruleSetFactory.createRuleSet(ref);
    }

    /**
     * Unit test for #1312 see https://sourceforge.net/p/pmd/bugs/1312/
     *
     * @throws Exception
     *             any error
     */
    @Test
    public void testRuleReferenceWithNameOverridden() throws Exception {
        RuleSetReferenceId ref = createRuleSetReferenceId("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<ruleset xmlns=\"http://pmd.sourceforge.net/ruleset/2.0.0\"\n"
                + "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                + "         name=\"pmd-eclipse\"\n"
                + "         xsi:schemaLocation=\"http://pmd.sourceforge.net/ruleset/2.0.0 https://pmd.sourceforge.io/ruleset_2_0_0.xsd\">\n"
                + "   <description>PMD Plugin preferences rule set</description>\n" + "\n"
                + "<rule name=\"OverriddenDummyBasicMockRule\"\n"
                + "    ref=\"rulesets/dummy/basic.xml/DummyBasicMockRule\">\n" + "</rule>\n" + "\n" + "</ruleset>");
        RuleSetFactory ruleSetFactory = RulesetsFactoryUtils.defaultFactory();
        RuleSet rs = ruleSetFactory.createRuleSet(ref);

        Rule r = rs.getRules().toArray(new Rule[1])[0];
        assertEquals("OverriddenDummyBasicMockRule", r.getName());
        RuleReference ruleRef = (RuleReference) r;
        assertEquals("DummyBasicMockRule", ruleRef.getRule().getName());
    }

    /**
     * See https://sourceforge.net/p/pmd/bugs/1231/
     *
     * <p>See https://github.com/pmd/pmd/issues/1978 - with that, it should not be an error anymore.
     *
     * @throws Exception
     *             any error
     */
    @Test
    public void testWrongRuleNameExcluded() throws Exception {
        RuleSetReferenceId ref = createRuleSetReferenceId(
                "<?xml version=\"1.0\"?>\n" + "<ruleset name=\"Custom ruleset for tests\"\n"
                        + "    xmlns=\"http://pmd.sourceforge.net/ruleset/2.0.0\"\n"
                        + "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                        + "    xsi:schemaLocation=\"http://pmd.sourceforge.net/ruleset/2.0.0 https://pmd.sourceforge.io/ruleset_2_0_0.xsd\">\n"
                        + "  <description>Custom ruleset for tests</description>\n"
                        + "  <rule ref=\"net/sourceforge/pmd/TestRuleset1.xml\">\n"
                        + "    <exclude name=\"ThisRuleDoesNotExist\"/>\n" + "  </rule>\n" + "</ruleset>\n");
        RuleSetFactory ruleSetFactory = RulesetsFactoryUtils.defaultFactory();
        RuleSet ruleset = ruleSetFactory.createRuleSet(ref);
        assertEquals(4, ruleset.getRules().size());
    }

    /**
     * This unit test manifests the current behavior - which might change in the
     * future. See #1537.
     *
     * Currently, if a ruleset is imported twice, the excludes of the first
     * import are ignored. Duplicated rules are silently ignored.
     *
     * @throws Exception
     *             any error
     * @see <a href="https://sourceforge.net/p/pmd/bugs/1537/">#1537 Implement
     *      strict ruleset parsing</a>
     * @see <a href=
     *      "http://stackoverflow.com/questions/40299075/custom-pmd-ruleset-not-working">stackoverflow
     *      - custom ruleset not working</a>
     */
    @Test
    public void testExcludeAndImportTwice() throws Exception {
        RuleSetReferenceId ref1 = createRuleSetReferenceId(
                "<?xml version=\"1.0\"?>\n" + "<ruleset name=\"Custom ruleset for tests\"\n"
                        + "    xmlns=\"http://pmd.sourceforge.net/ruleset/2.0.0\"\n"
                        + "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                        + "    xsi:schemaLocation=\"http://pmd.sourceforge.net/ruleset/2.0.0 https://pmd.sourceforge.io/ruleset_2_0_0.xsd\">\n"
                        + "  <description>Custom ruleset for tests</description>\n"
                        + "  <rule ref=\"rulesets/dummy/basic.xml\">\n" + "    <exclude name=\"DummyBasicMockRule\"/>\n"
                        + "  </rule>\n" + "</ruleset>\n");
        RuleSetFactory ruleSetFactory = RulesetsFactoryUtils.defaultFactory();
        RuleSet ruleset = ruleSetFactory.createRuleSet(ref1);
        assertNull(ruleset.getRuleByName("DummyBasicMockRule"));

        RuleSetReferenceId ref2 = createRuleSetReferenceId(
                "<?xml version=\"1.0\"?>\n" + "<ruleset name=\"Custom ruleset for tests\"\n"
                        + "    xmlns=\"http://pmd.sourceforge.net/ruleset/2.0.0\"\n"
                        + "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                        + "    xsi:schemaLocation=\"http://pmd.sourceforge.net/ruleset/2.0.0 https://pmd.sourceforge.io/ruleset_2_0_0.xsd\">\n"
                        + "  <description>Custom ruleset for tests</description>\n"
                        + "  <rule ref=\"rulesets/dummy/basic.xml\">\n" + "    <exclude name=\"DummyBasicMockRule\"/>\n"
                        + "  </rule>\n" + "  <rule ref=\"rulesets/dummy/basic.xml\"/>\n" + "</ruleset>\n");
        RuleSetFactory ruleSetFactory2 = RulesetsFactoryUtils.defaultFactory();
        RuleSet ruleset2 = ruleSetFactory2.createRuleSet(ref2);
        assertNotNull(ruleset2.getRuleByName("DummyBasicMockRule"));

        RuleSetReferenceId ref3 = createRuleSetReferenceId(
                "<?xml version=\"1.0\"?>\n" + "<ruleset name=\"Custom ruleset for tests\"\n"
                        + "    xmlns=\"http://pmd.sourceforge.net/ruleset/2.0.0\"\n"
                        + "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                        + "    xsi:schemaLocation=\"http://pmd.sourceforge.net/ruleset/2.0.0 https://pmd.sourceforge.io/ruleset_2_0_0.xsd\">\n"
                        + "  <description>Custom ruleset for tests</description>\n"
                        + "  <rule ref=\"rulesets/dummy/basic.xml\"/>\n" + "  <rule ref=\"rulesets/dummy/basic.xml\">\n"
                        + "    <exclude name=\"DummyBasicMockRule\"/>\n" + "  </rule>\n" + "</ruleset>\n");
        RuleSetFactory ruleSetFactory3 = RulesetsFactoryUtils.defaultFactory();
        RuleSet ruleset3 = ruleSetFactory3.createRuleSet(ref3);
        assertNotNull(ruleset3.getRuleByName("DummyBasicMockRule"));
    }

    @org.junit.Rule
    public JavaUtilLoggingRule logging = new JavaUtilLoggingRule(RuleSetFactory.class.getName());

    @Test
    public void testMissingRuleSetNameIsWarning() throws Exception {
        RuleSetReferenceId ref = createRuleSetReferenceId(
                "<?xml version=\"1.0\"?>\n" + "<ruleset \n"
                        + "    xmlns=\"http://pmd.sourceforge.net/ruleset/2.0.0\"\n"
                        + "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                        + "    xsi:schemaLocation=\"http://pmd.sourceforge.net/ruleset/2.0.0 https://pmd.sourceforge.io/ruleset_2_0_0.xsd\">\n"
                        + "  <description>Custom ruleset for tests</description>\n"
                        + "  <rule ref=\"rulesets/dummy/basic.xml\"/>\n"
                        + "  </ruleset>\n");
        RuleSetFactory ruleSetFactory = RulesetsFactoryUtils.defaultFactory();
        ruleSetFactory.createRuleSet(ref);

        assertTrue(logging.getLog().contains("RuleSet name is missing."));
    }

    @Test
    public void testMissingRuleSetDescriptionIsWarning() throws Exception {
        RuleSetReferenceId ref = createRuleSetReferenceId(
                "<?xml version=\"1.0\"?>\n" + "<ruleset name=\"then name\"\n"
                        + "    xmlns=\"http://pmd.sourceforge.net/ruleset/2.0.0\"\n"
                        + "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                        + "    xsi:schemaLocation=\"http://pmd.sourceforge.net/ruleset/2.0.0 https://pmd.sourceforge.io/ruleset_2_0_0.xsd\">\n"
                        + "  <rule ref=\"rulesets/dummy/basic.xml\"/>\n"
                        + "  </ruleset>\n");
        RuleSetFactory ruleSetFactory = RulesetsFactoryUtils.defaultFactory();
        ruleSetFactory.createRuleSet(ref);
        assertTrue(logging.getLog().contains("RuleSet description is missing."));
    }

    private static final String REF_OVERRIDE_ORIGINAL_NAME = "<?xml version=\"1.0\"?>" + PMD.EOL
            + "<ruleset name=\"test\">" + PMD.EOL + " <description>testdesc</description>" + PMD.EOL + " <rule "
            + PMD.EOL + "  ref=\"net/sourceforge/pmd/TestRuleset1.xml/MockRule1\" message=\"TestMessageOverride\"> "
            + PMD.EOL + " </rule>" + PMD.EOL + "</ruleset>";

    private static final String REF_MISPELLED_XREF = "<?xml version=\"1.0\"?>" + PMD.EOL + "<ruleset name=\"test\">"
            + PMD.EOL + " <description>testdesc</description>" + PMD.EOL + " <rule " + PMD.EOL
            + "  ref=\"net/sourceforge/pmd/TestRuleset1.xml/FooMockRule1\"> " + PMD.EOL + " </rule>" + PMD.EOL
            + "</ruleset>";

    private static final String REF_OVERRIDE_ORIGINAL_NAME_ONE_ELEM = "<?xml version=\"1.0\"?>" + PMD.EOL
            + "<ruleset name=\"test\">" + PMD.EOL + " <description>testdesc</description>" + PMD.EOL
            + " <rule ref=\"net/sourceforge/pmd/TestRuleset1.xml/MockRule1\" message=\"TestMessageOverride\"/> "
            + PMD.EOL + "</ruleset>";

    private static final String REF_OVERRIDE = "<?xml version=\"1.0\"?>" + PMD.EOL + "<ruleset name=\"test\">" + PMD.EOL
            + " <description>testdesc</description>" + PMD.EOL + " <rule " + PMD.EOL
            + "  ref=\"net/sourceforge/pmd/TestRuleset1.xml/MockRule4\" " + PMD.EOL + "  name=\"TestNameOverride\" "
            + PMD.EOL + "  message=\"Test message override\"> " + PMD.EOL
            + "  <description>Test description override</description>" + PMD.EOL
            + "  <example>Test example override</example>" + PMD.EOL + "  <priority>3</priority>" + PMD.EOL
            + "  <properties>" + PMD.EOL
            + "   <property name=\"test2\" description=\"test2\" type=\"String\" value=\"override2\"/>" + PMD.EOL
            + "   <property name=\"test3\" type=\"String\" description=\"test3\"><value>override3</value></property>"
            // + PMD.EOL + "   <property name=\"test4\" description=\"test4\" type=\"String\" value=\"new property\"/>" // Nonsense
            + PMD.EOL + "  </properties>" + PMD.EOL + " </rule>" + PMD.EOL + "</ruleset>";

    private static final String REF_OVERRIDE_NONEXISTENT = "<?xml version=\"1.0\"?>" + PMD.EOL + "<ruleset name=\"test\">" + PMD.EOL
                                                           + " <description>testdesc</description>" + PMD.EOL + " <rule " + PMD.EOL
                                                           + "  ref=\"net/sourceforge/pmd/TestRuleset1.xml/MockRule4\" " + PMD.EOL + "  name=\"TestNameOverride\" "
                                                           + PMD.EOL + "  message=\"Test message override\"> " + PMD.EOL
                                                           + "  <description>Test description override</description>" + PMD.EOL
                                                           + "  <example>Test example override</example>" + PMD.EOL + "  <priority>3</priority>" + PMD.EOL
                                                           + "  <properties>" + PMD.EOL
                                                           + "   <property name=\"test4\" description=\"test4\" type=\"String\" value=\"new property\"/>" + PMD.EOL // inexistent property
                                                           + "  </properties>" + PMD.EOL + " </rule>" + PMD.EOL + "</ruleset>";

    private static final String REF_INTERNAL_TO_INTERNAL = "<?xml version=\"1.0\"?>" + PMD.EOL
            + "<ruleset name=\"test\">" + PMD.EOL + " <description>testdesc</description>" + PMD.EOL + "<rule "
            + PMD.EOL + "name=\"MockRuleName\" " + PMD.EOL + "message=\"avoid the mock rule\" " + PMD.EOL
            + "class=\"net.sourceforge.pmd.lang.rule.MockRule\">" + PMD.EOL + "</rule>"
            + " <rule ref=\"MockRuleName\" name=\"MockRuleNameRef\"/> " + PMD.EOL + "</ruleset>";

    private static final String REF_INTERNAL_TO_INTERNAL_CHAIN = "<?xml version=\"1.0\"?>" + PMD.EOL
            + "<ruleset name=\"test\">" + PMD.EOL + " <description>testdesc</description>" + PMD.EOL + "<rule "
            + PMD.EOL + "name=\"MockRuleName\" " + PMD.EOL + "message=\"avoid the mock rule\" " + PMD.EOL
            + "class=\"net.sourceforge.pmd.lang.rule.MockRule\">" + PMD.EOL + "</rule>"
            + " <rule ref=\"MockRuleName\" name=\"MockRuleNameRef\"><priority>2</priority></rule> " + PMD.EOL
            + " <rule ref=\"MockRuleNameRef\" name=\"MockRuleNameRefRef\"><priority>1</priority></rule> " + PMD.EOL
            + "</ruleset>";

    private static final String REF_INTERNAL_TO_EXTERNAL = "<?xml version=\"1.0\"?>" + PMD.EOL
            + "<ruleset name=\"test\">" + PMD.EOL + " <description>testdesc</description>" + PMD.EOL + "<rule "
            + PMD.EOL + "name=\"ExternalRefRuleName\" " + PMD.EOL
            + "ref=\"net/sourceforge/pmd/TestRuleset1.xml/MockRule1\"/>" + PMD.EOL
            + " <rule ref=\"ExternalRefRuleName\" name=\"ExternalRefRuleNameRef\"/> " + PMD.EOL + "</ruleset>";

    private static final String REF_INTERNAL_TO_EXTERNAL_CHAIN = "<?xml version=\"1.0\"?>" + PMD.EOL
            + "<ruleset name=\"test\">" + PMD.EOL + " <description>testdesc</description>" + PMD.EOL + "<rule "
            + PMD.EOL + "name=\"ExternalRefRuleName\" " + PMD.EOL
            + "ref=\"net/sourceforge/pmd/TestRuleset2.xml/TestRule\"/>" + PMD.EOL
            + " <rule ref=\"ExternalRefRuleName\" name=\"ExternalRefRuleNameRef\"><priority>2</priority></rule> "
            + PMD.EOL
            + " <rule ref=\"ExternalRefRuleNameRef\" name=\"ExternalRefRuleNameRefRef\"><priority>1</priority></rule> "
            + PMD.EOL + "</ruleset>";

    private static final String EMPTY_RULESET = "<?xml version=\"1.0\"?>" + PMD.EOL + "<ruleset name=\"test\">"
            + PMD.EOL + "<description>testdesc</description>" + PMD.EOL + "</ruleset>";

    private static final String SINGLE_RULE = "<?xml version=\"1.0\"?>" + PMD.EOL + "<ruleset name=\"test\">" + PMD.EOL
            + "<description>testdesc</description>" + PMD.EOL + "<rule " + PMD.EOL + "name=\"MockRuleName\" " + PMD.EOL
            + "message=\"avoid the mock rule\" " + PMD.EOL + "class=\"net.sourceforge.pmd.lang.rule.MockRule\">"
            + "<priority>3</priority>" + PMD.EOL + "</rule></ruleset>";

    private static final String MULTIPLE_RULES = "<?xml version=\"1.0\"?>" + PMD.EOL + "<ruleset name=\"test\">"
            + PMD.EOL + "<description>testdesc</description>" + PMD.EOL + "<rule name=\"MockRuleName1\" " + PMD.EOL
            + "message=\"avoid the mock rule\" " + PMD.EOL + "class=\"net.sourceforge.pmd.lang.rule.MockRule\">"
            + PMD.EOL + "</rule>" + PMD.EOL + "<rule name=\"MockRuleName2\" " + PMD.EOL
            + "message=\"avoid the mock rule\" " + PMD.EOL + "class=\"net.sourceforge.pmd.lang.rule.MockRule\">"
            + PMD.EOL + "</rule></ruleset>";

    private static final String PROPERTIES = "<?xml version=\"1.0\"?>" + PMD.EOL + "<ruleset name=\"test\">" + PMD.EOL
            + "<description>testdesc</description>" + PMD.EOL + "<rule name=\"MockRuleName\" " + PMD.EOL
            + "message=\"avoid the mock rule\" " + PMD.EOL + "class=\"net.sourceforge.pmd.lang.rule.MockRule\">"
            + PMD.EOL + "<description>testdesc2</description>" + PMD.EOL + "<properties>" + PMD.EOL
            + "<property name=\"fooBoolean\" description=\"test\" type=\"Boolean\" value=\"true\" />" + PMD.EOL
            + "<property name=\"fooChar\" description=\"test\" type=\"Character\" value=\"B\" />" + PMD.EOL
            + "<property name=\"fooInt\" description=\"test\" type=\"Integer\" min=\"1\" max=\"10\" value=\"3\" />"
            + PMD.EOL
            + "<property name=\"fooFloat\" description=\"test\" type=\"Float\" min=\"1.0\" max=\"1.0\" value=\"1.0\"  />"
            + PMD.EOL
            + "<property name=\"fooDouble\" description=\"test\" type=\"Double\" min=\"1.0\" max=\"9.0\" value=\"3.0\"  />"
            + PMD.EOL + "<property name=\"fooString\" description=\"test\" type=\"String\" value=\"bar\" />" + PMD.EOL
            + "</properties>" + PMD.EOL + "</rule></ruleset>";

    private static final String XPATH = "<?xml version=\"1.0\"?>" + PMD.EOL + "<ruleset name=\"test\">" + PMD.EOL
            + "<description>testdesc</description>" + PMD.EOL + "<rule name=\"MockRuleName\" " + PMD.EOL
            + "message=\"avoid the mock rule\" " + PMD.EOL + "class=\"net.sourceforge.pmd.lang.rule.MockRule\">"
            + "<priority>3</priority>" + PMD.EOL + PMD.EOL + "<description>testdesc2</description>" + PMD.EOL
            + "<properties>" + PMD.EOL + "<property name=\"xpath\" description=\"test\" type=\"String\">" + PMD.EOL
            + "<value>" + PMD.EOL + "<![CDATA[ //Block ]]>" + PMD.EOL + "</value>" + PMD.EOL + "</property>" + PMD.EOL
            + "</properties>" + PMD.EOL + "</rule></ruleset>";

    private static final String PRIORITY = "<?xml version=\"1.0\"?>" + PMD.EOL + "<ruleset name=\"test\">" + PMD.EOL
            + "<description>testdesc</description>" + PMD.EOL + "<rule " + PMD.EOL + "name=\"MockRuleName\" " + PMD.EOL
            + "message=\"avoid the mock rule\" " + PMD.EOL + "class=\"net.sourceforge.pmd.lang.rule.MockRule\">"
            + "<priority>3</priority>" + PMD.EOL + "</rule></ruleset>";

    private static final String LANGUAGE = "<?xml version=\"1.0\"?>" + PMD.EOL + "<ruleset name=\"test\">" + PMD.EOL
            + "<description>testdesc</description>" + PMD.EOL + "<rule " + PMD.EOL + "name=\"MockRuleName\" " + PMD.EOL
            + "message=\"avoid the mock rule\" " + PMD.EOL
            + "class=\"net.sourceforge.pmd.lang.rule.MockRule\" language=\"dummy\">" + PMD.EOL + "</rule></ruleset>";

    private static final String INCORRECT_LANGUAGE = "<?xml version=\"1.0\"?>" + PMD.EOL + "<ruleset name=\"test\">"
            + PMD.EOL + "<description>testdesc</description>" + PMD.EOL + "<rule " + PMD.EOL + "name=\"MockRuleName\" "
            + PMD.EOL + "message=\"avoid the mock rule\" " + PMD.EOL
            + "class=\"net.sourceforge.pmd.lang.rule.MockRule\"" + PMD.EOL + " language=\"bogus\">" + PMD.EOL
            + "</rule></ruleset>";

    private static final String MINIMUM_LANGUAGE_VERSION = "<?xml version=\"1.0\"?>" + PMD.EOL
            + "<ruleset name=\"test\">" + PMD.EOL + "<description>testdesc</description>" + PMD.EOL + "<rule " + PMD.EOL
            + "name=\"MockRuleName\" " + PMD.EOL + "message=\"avoid the mock rule\" " + PMD.EOL
            + "class=\"net.sourceforge.pmd.lang.rule.MockRule\"" + PMD.EOL + " language=\"dummy\"" + PMD.EOL
            + " minimumLanguageVersion=\"1.4\">" + PMD.EOL + "</rule></ruleset>";

    private static final String INCORRECT_MINIMUM_LANGUAGE_VERSION = "<?xml version=\"1.0\"?>" + PMD.EOL
            + "<ruleset name=\"test\">" + PMD.EOL + "<description>testdesc</description>" + PMD.EOL + "<rule " + PMD.EOL
            + "name=\"MockRuleName\" " + PMD.EOL + "message=\"avoid the mock rule\" " + PMD.EOL
            + "class=\"net.sourceforge.pmd.lang.rule.MockRule\"" + PMD.EOL + " language=\"dummy\"" + PMD.EOL
            + " minimumLanguageVersion=\"bogus\">" + PMD.EOL + "</rule></ruleset>";

    private static final String MAXIMUM_LANGUAGE_VERSION = "<?xml version=\"1.0\"?>" + PMD.EOL
            + "<ruleset name=\"test\">" + PMD.EOL + "<description>testdesc</description>" + PMD.EOL + "<rule " + PMD.EOL
            + "name=\"MockRuleName\" " + PMD.EOL + "message=\"avoid the mock rule\" " + PMD.EOL
            + "class=\"net.sourceforge.pmd.lang.rule.MockRule\"" + PMD.EOL + " language=\"dummy\"" + PMD.EOL
            + " maximumLanguageVersion=\"1.7\">" + PMD.EOL + "</rule></ruleset>";

    private static final String INCORRECT_MAXIMUM_LANGUAGE_VERSION = "<?xml version=\"1.0\"?>" + PMD.EOL
            + "<ruleset name=\"test\">" + PMD.EOL + "<description>testdesc</description>" + PMD.EOL + "<rule " + PMD.EOL
            + "name=\"MockRuleName\" " + PMD.EOL + "message=\"avoid the mock rule\" " + PMD.EOL
            + "class=\"net.sourceforge.pmd.lang.rule.MockRule\"" + PMD.EOL + " language=\"dummy\"" + PMD.EOL
            + " maximumLanguageVersion=\"bogus\">" + PMD.EOL + "</rule></ruleset>";

    private static final String INVERTED_MINIMUM_MAXIMUM_LANGUAGE_VERSIONS = "<?xml version=\"1.0\"?>" + PMD.EOL
            + "<ruleset name=\"test\">" + PMD.EOL + "<description>testdesc</description>" + PMD.EOL + "<rule " + PMD.EOL
            + "name=\"MockRuleName\" " + PMD.EOL + "message=\"avoid the mock rule\" " + PMD.EOL
            + "class=\"net.sourceforge.pmd.lang.rule.MockRule\" " + PMD.EOL + "language=\"dummy\"" + PMD.EOL
            + " minimumLanguageVersion=\"1.7\"" + PMD.EOL + "maximumLanguageVersion=\"1.4\">" + PMD.EOL
            + "</rule></ruleset>";

    private static final String DIRECT_DEPRECATED_RULE = "<?xml version=\"1.0\"?>" + PMD.EOL + "<ruleset name=\"test\">"
            + PMD.EOL + "<description>testdesc</description>" + PMD.EOL + "<rule " + PMD.EOL + "name=\"MockRuleName\" "
            + PMD.EOL + "message=\"avoid the mock rule\" " + PMD.EOL
            + "class=\"net.sourceforge.pmd.lang.rule.MockRule\" deprecated=\"true\">" + PMD.EOL + "</rule></ruleset>";

    // Note: Update this RuleSet name to a different RuleSet with deprecated
    // Rules when the Rules are finally removed.
    private static final String DEPRECATED_RULE_RULESET_NAME = "net/sourceforge/pmd/TestRuleset1.xml";

    // Note: Update this Rule name to a different deprecated Rule when the one
    // listed here is finally removed.
    private static final String DEPRECATED_RULE_NAME = "MockRule3";

    private static final String REFERENCE_TO_DEPRECATED_RULE = "<?xml version=\"1.0\"?>" + PMD.EOL
            + "<ruleset name=\"test\">" + PMD.EOL + "<description>testdesc</description>" + PMD.EOL + "<rule " + PMD.EOL
            + "ref=\"" + DEPRECATED_RULE_RULESET_NAME + "/" + DEPRECATED_RULE_NAME + "\">" + PMD.EOL
            + "</rule></ruleset>";

    private static final String REFERENCE_TO_RULESET_WITH_DEPRECATED_RULE = "<?xml version=\"1.0\"?>" + PMD.EOL
            + "<ruleset name=\"test\">" + PMD.EOL + "<description>testdesc</description>" + PMD.EOL + "<rule " + PMD.EOL
            + "ref=\"" + DEPRECATED_RULE_RULESET_NAME + "\">" + PMD.EOL + "</rule></ruleset>";

    private static final String DFA = "<?xml version=\"1.0\"?>" + PMD.EOL + "<ruleset name=\"test\">" + PMD.EOL
            + "<description>testdesc</description>" + PMD.EOL + "<rule " + PMD.EOL + "name=\"MockRuleName\" " + PMD.EOL
            + "message=\"avoid the mock rule\" " + PMD.EOL + "dfa=\"true\" " + PMD.EOL
            + "class=\"net.sourceforge.pmd.lang.rule.MockRule\">" + "<priority>3</priority>" + PMD.EOL
            + "</rule></ruleset>";

    private static final String INCLUDE_EXCLUDE_RULESET = "<?xml version=\"1.0\"?>" + PMD.EOL
            + "<ruleset name=\"test\">" + PMD.EOL + "<description>testdesc</description>" + PMD.EOL
            + "<include-pattern>include1</include-pattern>" + PMD.EOL + "<include-pattern>include2</include-pattern>"
            + PMD.EOL + "<exclude-pattern>exclude1</exclude-pattern>" + PMD.EOL
            + "<exclude-pattern>exclude2</exclude-pattern>" + PMD.EOL + "<exclude-pattern>exclude3</exclude-pattern>"
            + PMD.EOL + "</ruleset>";

    private static final String EXTERNAL_REFERENCE_RULE_SET = "<?xml version=\"1.0\"?>" + PMD.EOL
            + "<ruleset name=\"test\">" + PMD.EOL + "<description>testdesc</description>" + PMD.EOL
            + "<rule ref=\"net/sourceforge/pmd/external-reference-ruleset.xml/MockRule\"/>" + PMD.EOL + "</ruleset>";

    private Rule loadFirstRule(String ruleSetXml) throws RuleSetNotFoundException {
        RuleSet rs = loadRuleSet(ruleSetXml);
        return rs.getRules().iterator().next();
    }

    private RuleSet loadRuleSet(String ruleSetXml) throws RuleSetNotFoundException {
        RuleSetFactory rsf = RulesetsFactoryUtils.defaultFactory();
        return rsf.createRuleSet(createRuleSetReferenceId(ruleSetXml));
    }

    private RuleSet loadRuleSetWithDeprecationWarnings(String ruleSetXml) throws RuleSetNotFoundException {
        RuleSetFactory rsf = RulesetsFactoryUtils.createFactory(RulePriority.LOW, true, false);
        return rsf.createRuleSet(createRuleSetReferenceId(ruleSetXml));
    }

    private static RuleSetReferenceId createRuleSetReferenceId(final String ruleSetXml) {
        return new RuleSetReferenceId(null) {
            @Override
            public InputStream getInputStream(ResourceLoader resourceLoader) throws RuleSetNotFoundException {
                try {
                    return new ByteArrayInputStream(ruleSetXml.getBytes("UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    return null;
                }
            }
        };
    }
}
