/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.lang.rule.XPathRule;

public class CodeClimateRendererTest extends AbstractRendererTest {

    @Override
    public Renderer getRenderer() {
        return new CodeClimateRenderer();
    }

    @Override
    public String getExpected() {
        return "{\"type\":\"issue\",\"check_name\":\"Foo\",\"description\":\"blah\","
                + "\"content\":{\"body\":\"## Foo\\n\\nSince: PMD null\\n\\nPriority: Low\\n\\n"
                + "[Categories](https://github.com/codeclimate/platform/blob/master/spec/analyzers/SPEC.md#categories): Style\\n\\n"
                + "[Remediation Points](https://github.com/codeclimate/platform/blob/master/spec/analyzers/SPEC.md#remediation-points): 50000\\n\\n"
                + "Description with Unicode Character U+2013: – .\\n\\n"
                + "### [PMD properties](https://pmd.github.io/latest/pmd_userdocs_configuring_rules.html#rule-properties)\\n\\n"
                + "Name | Value | Description\\n" + "--- | --- | ---\\n"
                + "violationSuppressRegex | | Suppress violations with messages matching a regular expression\\n"
                + "violationSuppressXPath | | Suppress violations on nodes which match a given relative XPath expression.\\n"
                + "\"},\"categories\":[\"Style\"],\"location\":{\"path\":\"" + getSourceCodeFilename() + "\",\"lines\":{\"begin\":1,\"end\":1}},\"severity\":\"minor\",\"remediation_points\":50000}"
                + "\u0000" + PMD.EOL;
    }

    @Override
    public String getExpectedWithProperties() {
        return "{\"type\":\"issue\",\"check_name\":\"Foo\",\"description\":\"blah\","
                + "\"content\":{\"body\":\"## Foo\\n\\nSince: PMD null\\n\\nPriority: Low\\n\\n"
                + "[Categories](https://github.com/codeclimate/platform/blob/master/spec/analyzers/SPEC.md#categories): Style\\n\\n"
                + "[Remediation Points](https://github.com/codeclimate/platform/blob/master/spec/analyzers/SPEC.md#remediation-points): 50000\\n\\n"
                + "Description with Unicode Character U+2013: – .\\n\\n"
                + "### [PMD properties](https://pmd.github.io/latest/pmd_userdocs_configuring_rules.html#rule-properties)\\n\\n"
                + "Name | Value | Description\\n" + "--- | --- | ---\\n"
                + "violationSuppressRegex | | Suppress violations with messages matching a regular expression\\n"
                + "violationSuppressXPath | | Suppress violations on nodes which match a given relative XPath expression.\\n"
                + "multiString | default1,default2 | multi string property\\n"
                + "stringProperty | the string value\\nsecond line with 'quotes' | simple string property\\n"
                + "\"},\"categories\":[\"Style\"],\"location\":{\"path\":\"" + getSourceCodeFilename() + "\",\"lines\":{\"begin\":1,\"end\":1}},\"severity\":\"minor\",\"remediation_points\":50000}"
                + "\u0000" + PMD.EOL;
    }

    @Override
    public String getExpectedEmpty() {
        return "";
    }

    @Override
    public String getExpectedMultiple() {
        return "{\"type\":\"issue\",\"check_name\":\"Foo\",\"description\":\"blah\","
                + "\"content\":{\"body\":\"## Foo\\n\\nSince: PMD null\\n\\nPriority: Low\\n\\n"
                + "[Categories](https://github.com/codeclimate/platform/blob/master/spec/analyzers/SPEC.md#categories): Style\\n\\n"
                + "[Remediation Points](https://github.com/codeclimate/platform/blob/master/spec/analyzers/SPEC.md#remediation-points): 50000\\n\\n"
                + "Description with Unicode Character U+2013: – .\\n\\n"
                + "### [PMD properties](https://pmd.github.io/latest/pmd_userdocs_configuring_rules.html#rule-properties)\\n\\n"
                + "Name | Value | Description\\n" + "--- | --- | ---\\n"
                + "violationSuppressRegex | | Suppress violations with messages matching a regular expression\\n"
                + "violationSuppressXPath | | Suppress violations on nodes which match a given relative XPath expression.\\n"
                + "\"},\"categories\":[\"Style\"],\"location\":{\"path\":\"" + getSourceCodeFilename() + "\",\"lines\":{\"begin\":1,\"end\":1}},\"severity\":\"minor\",\"remediation_points\":50000}"
                + "\u0000" + PMD.EOL + "{\"type\":\"issue\",\"check_name\":\"Boo\",\"description\":\"blah\","
                + "\"content\":{\"body\":\"## Boo\\n\\nSince: PMD null\\n\\nPriority: High\\n\\n"
                + "[Categories](https://github.com/codeclimate/platform/blob/master/spec/analyzers/SPEC.md#categories): Style\\n\\n"
                + "[Remediation Points](https://github.com/codeclimate/platform/blob/master/spec/analyzers/SPEC.md#remediation-points): 50000\\n\\n"
                + "desc\\n\\n"
                + "### [PMD properties](https://pmd.github.io/latest/pmd_userdocs_configuring_rules.html#rule-properties)\\n\\n"
                + "Name | Value | Description\\n" + "--- | --- | ---\\n"
                + "violationSuppressRegex | | Suppress violations with messages matching a regular expression\\n"
                + "violationSuppressXPath | | Suppress violations on nodes which match a given relative XPath expression.\\n"
                + "\"},\"categories\":[\"Style\"],\"location\":{\"path\":\"" + getSourceCodeFilename() + "\",\"lines\":{\"begin\":1,\"end\":1}},\"severity\":\"critical\",\"remediation_points\":50000}"
                + "\u0000" + PMD.EOL;
    }

    @Test
    public void testXPathRule() throws Exception {
        Report report = new Report();

        XPathRule theRule = new XPathRule();
        theRule.setProperty(XPathRule.XPATH_DESCRIPTOR, "//dummyNode");
        // Setup as FooRule
        theRule.setDescription("Description with Unicode Character U+2013: – .");
        theRule.setName("Foo");

        report.addRuleViolation(newRuleViolation(1, 1, 1, 2, theRule));
        String rendered = renderReport(getRenderer(), report);

        // Output should be the exact same as for non xpath rules
        assertEquals(filter(getExpected()), filter(rendered));
    }
}
