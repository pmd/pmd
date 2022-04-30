/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.html;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.rule.XPathRule;
import net.sourceforge.pmd.lang.rule.xpath.XPathVersion;

public class HtmlXPathRuleTest {

    // from https://developer.salesforce.com/docs/component-library/documentation/en/lwc/lwc.js_props_getter
    private static final String LIGHTNING_WEB_COMPONENT = "<!-- helloExpressions.html -->\n"
        + "<template>\n"
        + "    <p>Hello, { greeting}!</p>\n"
        + "    <lightning-input label=\"Name\" value={ greeting} onchange={handleChange}></lightning-input>"
        + "    <div class=\"slds-m-around_medium\">\n"
        + "        <lightning-input name='firstName' label=\"First Name\" onchange={handleChange}></lightning-input>\n"
        + "        <lightning-input name='lastName' label=\"Last Name\" onchange={handleChange}></lightning-input>\n"
        + "        <p class=\"slds-m-top_medium\">Uppercased Full Name: {uppercasedFullName}</p>\n"
        + "    </div>\n"
        + "</template>";

    @Test
    public void selectTextNode() {
        // from https://developer.salesforce.com/docs/component-library/documentation/en/lwc/lwc.js_props_getter
        // "Don’t add spaces around the property, for example, { data } is not valid HTML."
        String xpath = "//*[local-name() = '#text'][contains(@Text, '{ ')]";

        List<RuleViolation> violations = runXPath(LIGHTNING_WEB_COMPONENT, xpath);
        Assert.assertEquals(1, violations.size());
        Assert.assertEquals(3, violations.get(0).getBeginLine());
    }

    @Test
    public void selectAttributes() {
        // from https://developer.salesforce.com/docs/component-library/documentation/en/lwc/lwc.js_props_getter
        // "Don’t add spaces around the property, for example, { data } is not valid HTML."
        String xpath = "//*[@value = '{']";

        List<RuleViolation> violations = runXPath(LIGHTNING_WEB_COMPONENT, xpath);
        Assert.assertEquals(1, violations.size());
        Assert.assertEquals(4, violations.get(0).getBeginLine());
    }

    private List<RuleViolation> runXPath(String html, String xpath) {
        XPathRule rule = new XPathRule(XPathVersion.DEFAULT, xpath);
        rule.setMessage("test");
        rule.setLanguage(HtmlParsingHelper.DEFAULT.getLanguage());
        Report report = HtmlParsingHelper.DEFAULT.executeRule(rule, html);
        return report.getViolations();
    }
}
