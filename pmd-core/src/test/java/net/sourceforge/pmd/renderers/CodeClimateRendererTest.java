/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.ReportTest;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.ParametricRuleViolation;
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
                + "[Categories](https://github.com/codeclimate/spec/blob/master/SPEC.md#categories): Style\\n\\n"
                + "[Remediation Points](https://github.com/codeclimate/spec/blob/master/SPEC.md#remediation-points): 50000\\n\\n"
                + "desc\\n\\n"
                + "### [PMD properties](https://pmd.github.io/latest/pmd_devdocs_working_with_properties.html)\\n\\n"
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
                + "[Categories](https://github.com/codeclimate/spec/blob/master/SPEC.md#categories): Style\\n\\n"
                + "[Remediation Points](https://github.com/codeclimate/spec/blob/master/SPEC.md#remediation-points): 50000\\n\\n"
                + "desc\\n\\n"
                + "### [PMD properties](https://pmd.github.io/latest/pmd_devdocs_working_with_properties.html)\\n\\n"
                + "Name | Value | Description\\n" + "--- | --- | ---\\n"
                + "violationSuppressRegex | | Suppress violations with messages matching a regular expression\\n"
                + "violationSuppressXPath | | Suppress violations on nodes which match a given relative XPath expression.\\n"
                + "multiString | default1,default2 | multi string property\\n"
                + "stringProperty | the string value\\nsecond line with 'quotes' | simple string property\\n"
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
                + "[Categories](https://github.com/codeclimate/spec/blob/master/SPEC.md#categories): Style\\n\\n"
                + "[Remediation Points](https://github.com/codeclimate/spec/blob/master/SPEC.md#remediation-points): 50000\\n\\n"
                + "desc\\n\\n"
                + "### [PMD properties](https://pmd.github.io/latest/pmd_devdocs_working_with_properties.html)\\n\\n"
                + "Name | Value | Description\\n" + "--- | --- | ---\\n"
                + "violationSuppressRegex | | Suppress violations with messages matching a regular expression\\n"
                + "violationSuppressXPath | | Suppress violations on nodes which match a given relative XPath expression.\\n"
                + "\"},\"categories\":[\"Style\"],\"location\":{\"path\":\"" + getSourceCodeFilename() + "\",\"lines\":{\"begin\":1,\"end\":1}},\"severity\":\"info\",\"remediation_points\":50000}"
                + "\u0000" + PMD.EOL + "{\"type\":\"issue\",\"check_name\":\"Foo\",\"description\":\"blah\","
                + "\"content\":{\"body\":\"## Foo\\n\\nSince: PMD null\\n\\nPriority: Low\\n\\n"
                + "[Categories](https://github.com/codeclimate/spec/blob/master/SPEC.md#categories): Style\\n\\n"
                + "[Remediation Points](https://github.com/codeclimate/spec/blob/master/SPEC.md#remediation-points): 50000\\n\\n"
                + "desc\\n\\n"
                + "### [PMD properties](https://pmd.github.io/latest/pmd_devdocs_working_with_properties.html)\\n\\n"
                + "Name | Value | Description\\n" + "--- | --- | ---\\n"
                + "violationSuppressRegex | | Suppress violations with messages matching a regular expression\\n"
                + "violationSuppressXPath | | Suppress violations on nodes which match a given relative XPath expression.\\n"
                + "\"},\"categories\":[\"Style\"],\"location\":{\"path\":\"" + getSourceCodeFilename() + "\",\"lines\":{\"begin\":1,\"end\":1}},\"severity\":\"info\",\"remediation_points\":50000}"
                + "\u0000" + PMD.EOL;
    }

    @Test
    public void testXPathRule() throws Exception {
        DummyNode node = createNode(1);
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFile(new File(getSourceCodeFilename()));
        Report report = new Report();
        XPathRule theRule = new XPathRule();
        theRule.setProperty(XPathRule.XPATH_DESCRIPTOR, "//dummyNode");

        // Setup as FooRule
        theRule.setDescription("desc");
        theRule.setName("Foo");

        report.addRuleViolation(new ParametricRuleViolation<Node>(theRule, ctx, node, "blah"));
        String rendered = ReportTest.render(getRenderer(), report);

        // Output should be the exact same as for non xpath rules
        assertEquals(filter(getExpected()), filter(rendered));
    }
}
