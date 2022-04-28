/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.html;

import java.io.StringReader;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.XPathRule;
import net.sourceforge.pmd.lang.rule.xpath.XPathVersion;

public class HtmlXPathRuleTest {
    // from https://developer.salesforce.com/docs/component-library/documentation/en/lwc/lwc.js_props_getter
    private static final String LIGHTNING_WEB_COMPONENT = "<!-- helloExpressions.html -->\n"
            + "<template>\n"
            + "    <p>Hello, { greeting}!</p>\n"
            + "    <lightning-input label=\"Name\" value={ greeting} onchange={handleChange}></lightning-input>\n"
            + "    <div class=\"slds-m-around_medium\">\n"
            + "        <lightning-input name='firstName' label=\"First Name\" onchange={ handleChange}></lightning-input>\n"
            + "        <lightning-input name='lastName' label=\"Last Name\" onchange={handleChange}></lightning-input>\n"
            + "        <p class=\"slds-m-top_medium\">Uppercased Full Name: {uppercasedFullName}</p>\n"
            + "    </div>\n"
            + "    <template if:true={visible}>\n"
            + "      <p>Test</p>\n"
            + "    </template>\n"
            + "</template>";

    @Test
    public void selectTextNode() {
        // from https://developer.salesforce.com/docs/component-library/documentation/en/lwc/lwc.js_props_getter
        // "Don’t add spaces around the property, for example, { data } is not valid HTML."
        String xpath = "//text()[contains(., '{ ')]";

        Report report = runXPath(LIGHTNING_WEB_COMPONENT, xpath);
        Assert.assertEquals(1, report.getViolations().size());
        Assert.assertEquals(3, report.getViolations().get(0).getBeginLine());
    }

    @Test
    public void selectAttributes() {
        // from https://developer.salesforce.com/docs/component-library/documentation/en/lwc/lwc.js_props_getter
        // "Don’t add spaces around the property, for example, { data } is not valid HTML."
        String xpath = "//*[@value = '{']";

        Report report = runXPath(LIGHTNING_WEB_COMPONENT, xpath);
        Assert.assertEquals(1, report.getViolations().size());
        Assert.assertEquals(4, report.getViolations().get(0).getBeginLine());
    }

    @Test
    public void selectAttributesMultiple() {
        // from https://developer.salesforce.com/docs/component-library/documentation/en/lwc/lwc.js_props_getter
        // "Don’t add spaces around the property, for example, { data } is not valid HTML."
        String xpath = "//*[@*[local-name() = ('value', 'onchange')] = '{']";

        Report report = runXPath(LIGHTNING_WEB_COMPONENT, xpath);
        Assert.assertEquals(2, report.getViolations().size());
        Assert.assertEquals(4, report.getViolations().get(0).getBeginLine());
        Assert.assertEquals(6, report.getViolations().get(1).getBeginLine());
    }

    @Test
    public void selectAttributeByName() {
        String xpath = "//*[@*[local-name() = 'if:true']]";

        Report report = runXPath(LIGHTNING_WEB_COMPONENT, xpath);
        Assert.assertEquals(1, report.getViolations().size());
        Assert.assertEquals(10, report.getViolations().get(0).getBeginLine());
    }

    private Report runXPath(String html, String xpath) {
        LanguageVersion htmlLanguage = LanguageRegistry.findLanguageByTerseName(HtmlLanguageModule.TERSE_NAME).getDefaultVersion();
        Parser parser = htmlLanguage.getLanguageVersionHandler().getParser(htmlLanguage.getLanguageVersionHandler().getDefaultParserOptions());

        XPathRule rule = new XPathRule(XPathVersion.XPATH_2_0, xpath);
        rule.setMessage("test");
        Node node = parser.parse("n/a", new StringReader(html));
        RuleContext context = new RuleContext();
        context.setLanguageVersion(htmlLanguage);
        context.setCurrentRule(rule);
        rule.apply(Arrays.asList(node), context);
        return context.getReport();
    }
}
