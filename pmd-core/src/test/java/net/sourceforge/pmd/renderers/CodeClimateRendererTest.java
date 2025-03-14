/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.PMDVersion;
import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.rule.xpath.XPathRule;
import net.sourceforge.pmd.lang.rule.xpath.XPathVersion;

class CodeClimateRendererTest extends AbstractRendererTest {
    private static final String VERSION_PART = PMDVersion.isUnknown() || PMDVersion.isSnapshot() ? "latest" : "pmd-doc-" + PMDVersion.VERSION;

    @Override
    Renderer getRenderer() {
        return new CodeClimateRenderer();
    }

    @Override
    String getExpected() {
        return "{\"type\":\"issue\",\"check_name\":\"Foo\",\"description\":\"blah\","
                + "\"content\":{\"body\":\"## Foo\\n\\nSince: PMD null\\n\\nPriority: Low\\n\\n"
                + "[Categories](https://github.com/codeclimate/platform/blob/master/spec/analyzers/SPEC.md#categories): Style\\n\\n"
                + "[Remediation Points](https://github.com/codeclimate/platform/blob/master/spec/analyzers/SPEC.md#remediation-points): 50000\\n\\n"
                + "Description with Unicode Character U+2013: – .\\n\\n"
                + "### [PMD properties](https://docs.pmd-code.org/" + VERSION_PART + "/pmd_userdocs_configuring_rules.html#rule-properties)\\n\\n"
                + "Name | Value | Description\\n" + "--- | --- | ---\\n"
                + "violationSuppressRegex | | Suppress violations with messages matching a regular expression\\n"
                + "violationSuppressXPath | | Suppress violations on nodes which match a given relative XPath expression.\\n"
                + "\"},\"categories\":[\"Style\"],\"location\":{\"path\":\"" + getSourceCodeFilename() + "\",\"lines\":{\"begin\":1,\"end\":1}},\"severity\":\"info\",\"remediation_points\":50000}"
                + "\u0000" + EOL;
    }

    @Override
    String getExpectedWithProperties() {
        return "{\"type\":\"issue\",\"check_name\":\"Foo\",\"description\":\"blah\","
                + "\"content\":{\"body\":\"## Foo\\n\\nSince: PMD null\\n\\nPriority: Low\\n\\n"
                + "[Categories](https://github.com/codeclimate/platform/blob/master/spec/analyzers/SPEC.md#categories): Style\\n\\n"
                + "[Remediation Points](https://github.com/codeclimate/platform/blob/master/spec/analyzers/SPEC.md#remediation-points): 50000\\n\\n"
                + "Description with Unicode Character U+2013: – .\\n\\n"
                + "### [PMD properties](https://docs.pmd-code.org/" + VERSION_PART + "/pmd_userdocs_configuring_rules.html#rule-properties)\\n\\n"
                + "Name | Value | Description\\n" + "--- | --- | ---\\n"
                + "violationSuppressRegex | | Suppress violations with messages matching a regular expression\\n"
                + "violationSuppressXPath | | Suppress violations on nodes which match a given relative XPath expression.\\n"
                + "stringProperty | the string value\\nsecond line with 'quotes' | simple string property\\n"
                + "multiString | default1,default2 | multi string property\\n"
                + "\"},\"categories\":[\"Style\"],\"location\":{\"path\":\"" + getSourceCodeFilename() + "\",\"lines\":{\"begin\":1,\"end\":1}},\"severity\":\"info\",\"remediation_points\":50000}"
                + "\u0000" + EOL;
    }

    @Override
    String getExpectedEmpty() {
        return "";
    }

    @Override
    String getExpectedMultiple() {
        return "{\"type\":\"issue\",\"check_name\":\"Foo\",\"description\":\"blah\","
                + "\"content\":{\"body\":\"## Foo\\n\\nSince: PMD null\\n\\nPriority: Low\\n\\n"
                + "[Categories](https://github.com/codeclimate/platform/blob/master/spec/analyzers/SPEC.md#categories): Style\\n\\n"
                + "[Remediation Points](https://github.com/codeclimate/platform/blob/master/spec/analyzers/SPEC.md#remediation-points): 50000\\n\\n"
                + "Description with Unicode Character U+2013: – .\\n\\n"
                + "### [PMD properties](https://docs.pmd-code.org/" + VERSION_PART + "/pmd_userdocs_configuring_rules.html#rule-properties)\\n\\n"
                + "Name | Value | Description\\n" + "--- | --- | ---\\n"
                + "violationSuppressRegex | | Suppress violations with messages matching a regular expression\\n"
                + "violationSuppressXPath | | Suppress violations on nodes which match a given relative XPath expression.\\n"
                + "\"},\"categories\":[\"Style\"],\"location\":{\"path\":\"" + getSourceCodeFilename() + "\",\"lines\":{\"begin\":1,\"end\":1}},\"severity\":\"info\",\"remediation_points\":50000}"
                + "\u0000" + EOL + "{\"type\":\"issue\",\"check_name\":\"Boo\",\"description\":\"blah\","
                + "\"content\":{\"body\":\"## Boo\\n\\nSince: PMD null\\n\\nPriority: High\\n\\n"
                + "[Categories](https://github.com/codeclimate/platform/blob/master/spec/analyzers/SPEC.md#categories): Style\\n\\n"
                + "[Remediation Points](https://github.com/codeclimate/platform/blob/master/spec/analyzers/SPEC.md#remediation-points): 50000\\n\\n"
                + "desc\\n\\n"
                + "### [PMD properties](https://docs.pmd-code.org/" + VERSION_PART + "/pmd_userdocs_configuring_rules.html#rule-properties)\\n\\n"
                + "Name | Value | Description\\n" + "--- | --- | ---\\n"
                + "violationSuppressRegex | | Suppress violations with messages matching a regular expression\\n"
                + "violationSuppressXPath | | Suppress violations on nodes which match a given relative XPath expression.\\n"
                + "\"},\"categories\":[\"Style\"],\"location\":{\"path\":\"" + getSourceCodeFilename() + "\",\"lines\":{\"begin\":1,\"end\":1}},\"severity\":\"blocker\",\"remediation_points\":50000}"
                + "\u0000" + EOL;
    }

    @Test
    void testXPathRule() throws Exception {
        FileLocation node = createLocation(1, 1, 1, 1);
        XPathRule theRule = new XPathRule(XPathVersion.XPATH_3_1, "//dummyNode");

        // Setup as FooRule
        theRule.setDescription("Description with Unicode Character U+2013: – .");
        theRule.setName("Foo");

        String rendered = renderReport(getRenderer(), it -> it.onRuleViolation(newRuleViolation(theRule, node, "blah")));

        // Output should be the exact same as for non xpath rules
        assertEquals(filter(getExpected()), filter(rendered));
    }
}
