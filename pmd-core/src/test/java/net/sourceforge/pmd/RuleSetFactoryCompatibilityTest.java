/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.util.ResourceLoader;

public class RuleSetFactoryCompatibilityTest {

    @Test
    public void testCorrectOldReference() throws Exception {
        final String ruleset = "<?xml version=\"1.0\"?>\n" + "\n" + "<ruleset name=\"Test\"\n"
                + "    xmlns=\"http://pmd.sourceforge.net/ruleset/2.0.0\"\n"
                + "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                + "    xsi:schemaLocation=\"http://pmd.sourceforge.net/ruleset/2.0.0 https://pmd.sourceforge.io/ruleset_2_0_0.xsd\">\n"
                + "  <description>Test</description>\n" + "\n"
                + " <rule ref=\"rulesets/dummy/notexisting.xml/DummyBasicMockRule\" />\n" + "</ruleset>\n";

        RuleSetFactory factory = RulesetsFactoryUtils.defaultFactory();
        factory.getCompatibilityFilter().addFilterRuleMoved("dummy", "notexisting", "basic", "DummyBasicMockRule");

        RuleSet createdRuleSet = createRulesetFromString(ruleset, factory);
        Assert.assertNotNull(createdRuleSet.getRuleByName("DummyBasicMockRule"));
    }

    @Test
    public void testCorrectMovedAndRename() {

        RuleSetFactoryCompatibility rsfc = new RuleSetFactoryCompatibility();
        rsfc.addFilterRuleMoved("dummy", "notexisting", "basic", "OldDummyBasicMockRule");
        rsfc.addFilterRuleRenamed("dummy", "basic", "OldDummyBasicMockRule", "NewNameForDummyBasicMockRule");

        String out = rsfc.applyRef("rulesets/dummy/notexisting.xml/OldDummyBasicMockRule");

        Assert.assertEquals("rulesets/dummy/basic.xml/NewNameForDummyBasicMockRule", out);
    }

    @Test
    public void testExclusion() throws Exception {
        final String ruleset = "<?xml version=\"1.0\"?>\n" + "\n" + "<ruleset name=\"Test\"\n"
                + "    xmlns=\"http://pmd.sourceforge.net/ruleset/2.0.0\"\n"
                + "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                + "    xsi:schemaLocation=\"http://pmd.sourceforge.net/ruleset/2.0.0 https://pmd.sourceforge.io/ruleset_2_0_0.xsd\">\n"
                + "  <description>Test</description>\n" + "\n" + " <rule ref=\"rulesets/dummy/basic.xml\">\n"
                + "   <exclude name=\"OldNameOfSampleXPathRule\"/>\n" + " </rule>\n" + "</ruleset>\n";

        RuleSetFactory factory = RulesetsFactoryUtils.defaultFactory();
        factory.getCompatibilityFilter().addFilterRuleRenamed("dummy", "basic", "OldNameOfSampleXPathRule",
                "SampleXPathRule");

        RuleSet createdRuleSet = createRulesetFromString(ruleset, factory);
        Assert.assertNotNull(createdRuleSet.getRuleByName("DummyBasicMockRule"));
        Assert.assertNull(createdRuleSet.getRuleByName("SampleXPathRule"));
    }

    @Test
    public void testExclusionRenamedAndMoved() {

        RuleSetFactoryCompatibility rsfc = new RuleSetFactoryCompatibility();
        rsfc.addFilterRuleMovedAndRenamed("dummy", "oldbasic", "OldDummyBasicMockRule", "basic", "NewNameForDummyBasicMockRule");

        String in = "rulesets/dummy/oldbasic.xml";
        String out = rsfc.applyRef(in);

        Assert.assertEquals(in, out);
    }

    @Test
    public void testFilter() {
        RuleSetFactoryCompatibility rsfc = new RuleSetFactoryCompatibility();
        rsfc.addFilterRuleMoved("dummy", "notexisting", "basic", "DummyBasicMockRule");
        rsfc.addFilterRuleRemoved("dummy", "basic", "DeletedRule");
        rsfc.addFilterRuleRenamed("dummy", "basic", "OldNameOfBasicMockRule", "NewNameOfBasicMockRule");

        Assert.assertEquals("rulesets/dummy/basic.xml/DummyBasicMockRule",
                            rsfc.applyRef("rulesets/dummy/notexisting.xml/DummyBasicMockRule"));

        Assert.assertEquals("rulesets/dummy/basic.xml/NewNameOfBasicMockRule",
                            rsfc.applyRef("rulesets/dummy/basic.xml/OldNameOfBasicMockRule"));

        Assert.assertNull(rsfc.applyRef("rulesets/dummy/basic.xml/DeletedRule"));
    }

    @Test
    public void testExclusionFilter() {
        RuleSetFactoryCompatibility rsfc = new RuleSetFactoryCompatibility();
        rsfc.addFilterRuleRenamed("dummy", "basic", "AnotherOldNameOfBasicMockRule", "NewNameOfBasicMockRule");

        String out = rsfc.applyExclude("rulesets/dummy/basic.xml", "AnotherOldNameOfBasicMockRule");

        Assert.assertEquals("NewNameOfBasicMockRule", out);
    }

    private RuleSet createRulesetFromString(final String ruleset, RuleSetFactory factory)
            throws RuleSetNotFoundException {
        return factory.createRuleSet(new RuleSetReferenceId(null) {
            @Override
            public InputStream getInputStream(ResourceLoader resourceLoader) throws RuleSetNotFoundException {
                return new ByteArrayInputStream(ruleset.getBytes(StandardCharsets.UTF_8));
            }
        });
    }
}
