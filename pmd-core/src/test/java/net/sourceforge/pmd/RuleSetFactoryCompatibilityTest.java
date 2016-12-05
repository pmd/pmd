/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

public class RuleSetFactoryCompatibilityTest {
    private static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    @Test
    public void testCorrectOldReference() throws Exception {
        final String ruleset = "<?xml version=\"1.0\"?>\n" + "\n" + "<ruleset name=\"Test\"\n"
                + "    xmlns=\"http://pmd.sourceforge.net/ruleset/2.0.0\"\n"
                + "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                + "    xsi:schemaLocation=\"http://pmd.sourceforge.net/ruleset/2.0.0 http://pmd.sourceforge.net/ruleset_2_0_0.xsd\">\n"
                + "  <description>Test</description>\n" + "\n"
                + " <rule ref=\"rulesets/dummy/notexisting.xml/DummyBasicMockRule\" />\n" + "</ruleset>\n";

        RuleSetFactory factory = new RuleSetFactory();
        factory.getCompatibilityFilter().addFilterRuleMoved("dummy", "notexisting", "basic", "DummyBasicMockRule");

        RuleSet createdRuleSet = createRulesetFromString(ruleset, factory);
        Assert.assertNotNull(createdRuleSet.getRuleByName("DummyBasicMockRule"));
    }

    @Test
    public void testExclusion() throws Exception {
        final String ruleset = "<?xml version=\"1.0\"?>\n" + "\n" + "<ruleset name=\"Test\"\n"
                + "    xmlns=\"http://pmd.sourceforge.net/ruleset/2.0.0\"\n"
                + "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                + "    xsi:schemaLocation=\"http://pmd.sourceforge.net/ruleset/2.0.0 http://pmd.sourceforge.net/ruleset_2_0_0.xsd\">\n"
                + "  <description>Test</description>\n" + "\n" + " <rule ref=\"rulesets/dummy/basic.xml\">\n"
                + "   <exclude name=\"OldNameOfSampleXPathRule\"/>\n" + " </rule>\n" + "</ruleset>\n";

        RuleSetFactory factory = new RuleSetFactory();
        factory.getCompatibilityFilter().addFilterRuleRenamed("dummy", "basic", "OldNameOfSampleXPathRule",
                "SampleXPathRule");

        RuleSet createdRuleSet = createRulesetFromString(ruleset, factory);
        Assert.assertNotNull(createdRuleSet.getRuleByName("DummyBasicMockRule"));
        Assert.assertNull(createdRuleSet.getRuleByName("SampleXPathRule"));
    }

    @Test
    public void testFilter() throws Exception {
        RuleSetFactoryCompatibility rsfc = new RuleSetFactoryCompatibility();
        rsfc.addFilterRuleMoved("dummy", "notexisting", "basic", "DummyBasicMockRule");
        rsfc.addFilterRuleRemoved("dummy", "basic", "DeletedRule");
        rsfc.addFilterRuleRenamed("dummy", "basic", "OldNameOfBasicMockRule", "NewNameOfBasicMockRule");

        String in = "<?xml version=\"1.0\"?>\n" + "\n" + "<ruleset name=\"Test\"\n"
                + "    xmlns=\"http://pmd.sourceforge.net/ruleset/2.0.0\"\n"
                + "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                + "    xsi:schemaLocation=\"http://pmd.sourceforge.net/ruleset/2.0.0 http://pmd.sourceforge.net/ruleset_2_0_0.xsd\">\n"
                + "  <description>Test</description>\n" + "\n"
                + " <rule ref=\"rulesets/dummy/notexisting.xml/DummyBasicMockRule\" />\n"
                + " <rule ref=\"rulesets/dummy/basic.xml/DeletedRule\" />\n"
                + " <rule ref=\"rulesets/dummy/basic.xml/OldNameOfBasicMockRule\" />\n" + "</ruleset>\n";
        InputStream stream = new ByteArrayInputStream(in.getBytes(ISO_8859_1));
        Reader filtered = rsfc.filterRuleSetFile(stream);
        String out = IOUtils.toString(filtered);

        Assert.assertFalse(out.contains("notexisting.xml"));
        Assert.assertTrue(out.contains("<rule ref=\"rulesets/dummy/basic.xml/DummyBasicMockRule\" />"));

        Assert.assertFalse(out.contains("DeletedRule"));

        Assert.assertFalse(out.contains("OldNameOfBasicMockRule"));
        Assert.assertTrue(out.contains("<rule ref=\"rulesets/dummy/basic.xml/NewNameOfBasicMockRule\" />"));
    }

    @Test
    public void testExclusionFilter() throws Exception {
        RuleSetFactoryCompatibility rsfc = new RuleSetFactoryCompatibility();
        rsfc.addFilterRuleRenamed("dummy", "basic", "AnotherOldNameOfBasicMockRule", "NewNameOfBasicMockRule");

        String in = "<?xml version=\"1.0\"?>\n" + "\n" + "<ruleset name=\"Test\"\n"
                + "    xmlns=\"http://pmd.sourceforge.net/ruleset/2.0.0\"\n"
                + "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                + "    xsi:schemaLocation=\"http://pmd.sourceforge.net/ruleset/2.0.0 http://pmd.sourceforge.net/ruleset_2_0_0.xsd\">\n"
                + "  <description>Test</description>\n" + "\n" + " <rule ref=\"rulesets/dummy/basic.xml\">\n"
                + "   <exclude name=\"AnotherOldNameOfBasicMockRule\"/>\n" + " </rule>\n" + "</ruleset>\n";
        InputStream stream = new ByteArrayInputStream(in.getBytes(ISO_8859_1));
        Reader filtered = rsfc.filterRuleSetFile(stream);
        String out = IOUtils.toString(filtered);

        Assert.assertFalse(out.contains("OldNameOfBasicMockRule"));
        Assert.assertTrue(out.contains("<exclude name=\"NewNameOfBasicMockRule\" />"));
    }

    @Test
    public void testEncoding() {
        RuleSetFactoryCompatibility rsfc = new RuleSetFactoryCompatibility();
        String testString;

        testString = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><x></x>";
        Assert.assertEquals("ISO-8859-1", rsfc.determineEncoding(testString.getBytes(ISO_8859_1)));

        testString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><x></x>";
        Assert.assertEquals("UTF-8", rsfc.determineEncoding(testString.getBytes(ISO_8859_1)));
    }

    private RuleSet createRulesetFromString(final String ruleset, RuleSetFactory factory)
            throws RuleSetNotFoundException {
        return factory.createRuleSet(new RuleSetReferenceId(null) {
            @Override
            public InputStream getInputStream(ClassLoader classLoader) throws RuleSetNotFoundException {
                return new ByteArrayInputStream(ruleset.getBytes(UTF_8));
            }
        });
    }
}
