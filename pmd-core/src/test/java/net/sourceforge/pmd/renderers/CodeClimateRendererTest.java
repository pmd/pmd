/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.ReportTest;
import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.ParametricRuleViolation;
import net.sourceforge.pmd.lang.rule.XPathRule;
import net.sourceforge.pmd.lang.rule.xpath.XPathVersion;

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
                + "desc\\n\\n"
                + "### [PMD properties](https://pmd.github.io/latest/pmd_userdocs_configuring_rules.html#rule-properties)\\n\\n"
                + "Name | Value | Description\\n" + "--- | --- | ---\\n"
                + "violationSuppressRegex | | Suppress violations with messages matching a regular expression\\n"
                + "violationSuppressXPath | | Suppress violations on nodes which match a given relative XPath expression.\\n"
                + "\"},\"categories\":[\"Style\"],\"location\":{\"path\":\"" + getSourceCodeFilename() + "\",\"lines\":{\"begin\":1,\"end\":1}},\"severity\":\"info\",\"remediation_points\":50000}"
                + "\u0000" + PMD.EOL;
    }

    @Override
    public String getExpectedWithProperties() {
        return "{\"type\":\"issue\",\"check_name\":\"Foo\",\"description\":\"blah\","
                + "\"content\":{\"body\":\"## Foo\\n\\nSince: PMD null\\n\\nPriority: Low\\n\\n"
                + "[Categories](https://github.com/codeclimate/platform/blob/master/spec/analyzers/SPEC.md#categories): Style\\n\\n"
                + "[Remediation Points](https://github.com/codeclimate/platform/blob/master/spec/analyzers/SPEC.md#remediation-points): 50000\\n\\n"
                + "desc\\n\\n"
                + "### [PMD properties](https://pmd.github.io/latest/pmd_userdocs_configuring_rules.html#rule-properties)\\n\\n"
                + "Name | Value | Description\\n" + "--- | --- | ---\\n"
                + "violationSuppressRegex | | Suppress violations with messages matching a regular expression\\n"
                + "violationSuppressXPath | | Suppress violations on nodes which match a given relative XPath expression.\\n"
                + "stringProperty | the string value\\nsecond line with 'quotes' | simple string property\\n"
                + "multiString | default1,default2 | multi string property\\n"
                + "\"},\"categories\":[\"Style\"],\"location\":{\"path\":\"" + getSourceCodeFilename() + "\",\"lines\":{\"begin\":1,\"end\":1}},\"severity\":\"info\",\"remediation_points\":50000}"
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
                + "desc\\n\\n"
                + "### [PMD properties](https://pmd.github.io/latest/pmd_userdocs_configuring_rules.html#rule-properties)\\n\\n"
                + "Name | Value | Description\\n" + "--- | --- | ---\\n"
                + "violationSuppressRegex | | Suppress violations with messages matching a regular expression\\n"
                + "violationSuppressXPath | | Suppress violations on nodes which match a given relative XPath expression.\\n"
                + "\"},\"categories\":[\"Style\"],\"location\":{\"path\":\"" + getSourceCodeFilename() + "\",\"lines\":{\"begin\":1,\"end\":1}},\"severity\":\"info\",\"remediation_points\":50000}"
                + "\u0000" + PMD.EOL + "{\"type\":\"issue\",\"check_name\":\"Foo\",\"description\":\"blah\","
                + "\"content\":{\"body\":\"## Foo\\n\\nSince: PMD null\\n\\nPriority: High\\n\\n"
                + "[Categories](https://github.com/codeclimate/platform/blob/master/spec/analyzers/SPEC.md#categories): Style\\n\\n"
                + "[Remediation Points](https://github.com/codeclimate/platform/blob/master/spec/analyzers/SPEC.md#remediation-points): 50000\\n\\n"
                + "desc\\n\\n"
                + "### [PMD properties](https://pmd.github.io/latest/pmd_userdocs_configuring_rules.html#rule-properties)\\n\\n"
                + "Name | Value | Description\\n" + "--- | --- | ---\\n"
                + "violationSuppressRegex | | Suppress violations with messages matching a regular expression\\n"
                + "violationSuppressXPath | | Suppress violations on nodes which match a given relative XPath expression.\\n"
                + "\"},\"categories\":[\"Style\"],\"location\":{\"path\":\"" + getSourceCodeFilename() + "\",\"lines\":{\"begin\":1,\"end\":1}},\"severity\":\"blocker\",\"remediation_points\":50000}"
                + "\u0000" + PMD.EOL;
    }

    @Test
    public void testXPathRule() throws Exception {
        DummyNode node = createNode(1);
        XPathRule theRule = new XPathRule(XPathVersion.XPATH_3_1, "//dummyNode");

        // Setup as FooRule
        theRule.setDescription("desc");
        theRule.setName("Foo");

        String rendered = ReportTest.render(getRenderer(), it -> it.onRuleViolation(new ParametricRuleViolation<Node>(theRule, node, "blah")));

        // Output should be the exact same as for non xpath rules
        assertEquals(filter(getExpected()), filter(rendered));
    }
}
