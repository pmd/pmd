/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.html;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.html.ast.ASTHtmlComment;
import net.sourceforge.pmd.lang.html.ast.ASTHtmlDocument;
import net.sourceforge.pmd.lang.html.ast.ASTHtmlTextNode;
import net.sourceforge.pmd.lang.html.ast.HtmlParsingHelper;
import net.sourceforge.pmd.lang.rule.XPathRule;
import net.sourceforge.pmd.lang.rule.xpath.XPathVersion;

class HtmlXPathRuleTest {

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
    void selectTextNode() {
        // from https://developer.salesforce.com/docs/component-library/documentation/en/lwc/lwc.js_props_getter
        // "Don’t add spaces around the property, for example, { data } is not valid HTML."
        String xpath = "//text()[contains(., '{ ')]";

        List<RuleViolation> violations = runXPath(LIGHTNING_WEB_COMPONENT, xpath);
        assertEquals(1, violations.size());
        assertEquals(3, violations.get(0).getBeginLine());
    }

    @Test
    void selectTextNodeByNodeNameShouldNotWork() {
        String xpath = "//*[local-name() = '#text']";
        List<RuleViolation> violations = runXPath(LIGHTNING_WEB_COMPONENT, xpath);
        assertEquals(0, violations.size());
    }

    @Test
    void verifyTextNodeName() {
        ASTHtmlDocument document = HtmlParsingHelper.DEFAULT.parse("<p>foobar</p>");
        ASTHtmlTextNode textNode = document.getFirstDescendantOfType(ASTHtmlTextNode.class);
        assertEquals("#text", textNode.getXPathNodeName());
    }

    @Test
    void verifyCommentNodeName() {
        ASTHtmlDocument document = HtmlParsingHelper.DEFAULT.parse("<p><!-- a comment --></p>");
        ASTHtmlComment comment = document.getFirstDescendantOfType(ASTHtmlComment.class);
        assertEquals("#comment", comment.getXPathNodeName());
    }

    @Test
    void selectAttributes() {
        // from https://developer.salesforce.com/docs/component-library/documentation/en/lwc/lwc.js_props_getter
        // "Don’t add spaces around the property, for example, { data } is not valid HTML."
        String xpath = "//*[@value = '{']";

        List<RuleViolation> violations = runXPath(LIGHTNING_WEB_COMPONENT, xpath);
        assertEquals(1, violations.size());
        assertEquals(4, violations.get(0).getBeginLine());
    }

    @Test
    void selectAttributesMultiple() {
        // from https://developer.salesforce.com/docs/component-library/documentation/en/lwc/lwc.js_props_getter
        // "Don’t add spaces around the property, for example, { data } is not valid HTML."
        String xpath = "//*[@*[local-name() = ('value', 'onchange')] = '{']";

        List<RuleViolation> violations = runXPath(LIGHTNING_WEB_COMPONENT, xpath);
        assertEquals(2, violations.size());
        assertEquals(4, violations.get(0).getBeginLine());
        assertEquals(6, violations.get(1).getBeginLine());
    }

    @Test
    void selectAttributeByName() {
        String xpath = "//*[@*[local-name() = 'if:true']]";

        List<RuleViolation> violations = runXPath(LIGHTNING_WEB_COMPONENT, xpath);
        assertEquals(1, violations.size());
        assertEquals(10, violations.get(0).getBeginLine());
    }

    private List<RuleViolation> runXPath(String html, String xpath) {
        XPathRule rule = new XPathRule(XPathVersion.DEFAULT, xpath);
        rule.setMessage("test");
        rule.setLanguage(HtmlParsingHelper.DEFAULT.getLanguage());
        Report report = HtmlParsingHelper.DEFAULT.executeRule(rule, html);
        return report.getViolations();
    }
}
