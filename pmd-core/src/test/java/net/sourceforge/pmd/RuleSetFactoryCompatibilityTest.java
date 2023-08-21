/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class RuleSetFactoryCompatibilityTest {

    @Test
    void testCorrectOldReference() throws Exception {
        final String ruleset = "<?xml version=\"1.0\"?>\n" + "\n" + "<ruleset name=\"Test\"\n"
                + "    xmlns=\"http://pmd.sourceforge.net/ruleset/2.0.0\"\n"
                + "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                + "    xsi:schemaLocation=\"http://pmd.sourceforge.net/ruleset/2.0.0 https://pmd.sourceforge.io/ruleset_2_0_0.xsd\">\n"
                + "  <description>Test</description>\n" + "\n"
                + " <rule ref=\"rulesets/dummy/notexisting.xml/DummyBasicMockRule\" />\n" + "</ruleset>\n";

        RuleSetFactoryCompatibility compat = new RuleSetFactoryCompatibility();
        compat.addFilterRuleMoved("dummy", "notexisting", "basic", "DummyBasicMockRule");


        RuleSetLoader rulesetLoader = new RuleSetLoader().setCompatibility(compat);
        RuleSet createdRuleSet = rulesetLoader.loadFromString("dummy.xml", ruleset);

        assertNotNull(createdRuleSet.getRuleByName("DummyBasicMockRule"));
    }

    @Test
    void testCorrectMovedAndRename() {

        RuleSetFactoryCompatibility rsfc = new RuleSetFactoryCompatibility();
        rsfc.addFilterRuleMoved("dummy", "notexisting", "basic", "OldDummyBasicMockRule");
        rsfc.addFilterRuleRenamed("dummy", "basic", "OldDummyBasicMockRule", "NewNameForDummyBasicMockRule");

        String out = rsfc.applyRef("rulesets/dummy/notexisting.xml/OldDummyBasicMockRule");

        assertEquals("rulesets/dummy/basic.xml/NewNameForDummyBasicMockRule", out);
    }

    @Test
    void testExclusion() {
        final String ruleset = "<?xml version=\"1.0\"?>\n" + "\n" + "<ruleset name=\"Test\"\n"
                + "    xmlns=\"http://pmd.sourceforge.net/ruleset/2.0.0\"\n"
                + "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                + "    xsi:schemaLocation=\"http://pmd.sourceforge.net/ruleset/2.0.0 https://pmd.sourceforge.io/ruleset_2_0_0.xsd\">\n"
                + "  <description>Test</description>\n" + "\n" + " <rule ref=\"rulesets/dummy/basic.xml\">\n"
                + "   <exclude name=\"OldNameOfSampleXPathRule\"/>\n" + " </rule>\n" + "</ruleset>\n";

        RuleSetFactoryCompatibility compat = new RuleSetFactoryCompatibility();
        compat.addFilterRuleRenamed("dummy", "basic", "OldNameOfSampleXPathRule", "SampleXPathRule");

        RuleSetLoader rulesetLoader = new RuleSetLoader().setCompatibility(compat);
        RuleSet createdRuleSet = rulesetLoader.loadFromString("dummy.xml", ruleset);

        assertNotNull(createdRuleSet.getRuleByName("DummyBasicMockRule"));
        assertNull(createdRuleSet.getRuleByName("SampleXPathRule"));
    }

    @Test
    void testExclusionRenamedAndMoved() {

        RuleSetFactoryCompatibility rsfc = new RuleSetFactoryCompatibility();
        rsfc.addFilterRuleMovedAndRenamed("dummy", "oldbasic", "OldDummyBasicMockRule", "basic", "NewNameForDummyBasicMockRule");

        String in = "rulesets/dummy/oldbasic.xml";
        String out = rsfc.applyRef(in);

        assertEquals(in, out);
    }

    @Test
    void testFilter() {
        RuleSetFactoryCompatibility rsfc = new RuleSetFactoryCompatibility();
        rsfc.addFilterRuleMoved("dummy", "notexisting", "basic", "DummyBasicMockRule");
        rsfc.addFilterRuleRemoved("dummy", "basic", "DeletedRule");
        rsfc.addFilterRuleRenamed("dummy", "basic", "OldNameOfBasicMockRule", "NewNameOfBasicMockRule");

        assertEquals("rulesets/dummy/basic.xml/DummyBasicMockRule",
                            rsfc.applyRef("rulesets/dummy/notexisting.xml/DummyBasicMockRule"));

        assertEquals("rulesets/dummy/basic.xml/NewNameOfBasicMockRule",
                            rsfc.applyRef("rulesets/dummy/basic.xml/OldNameOfBasicMockRule"));

        assertNull(rsfc.applyRef("rulesets/dummy/basic.xml/DeletedRule"));
    }

    @Test
    void testExclusionFilter() {
        RuleSetFactoryCompatibility rsfc = new RuleSetFactoryCompatibility();
        rsfc.addFilterRuleRenamed("dummy", "basic", "AnotherOldNameOfBasicMockRule", "NewNameOfBasicMockRule");

        String out = rsfc.applyExclude("rulesets/dummy/basic.xml", "AnotherOldNameOfBasicMockRule", false);

        assertEquals("NewNameOfBasicMockRule", out);
    }

}
