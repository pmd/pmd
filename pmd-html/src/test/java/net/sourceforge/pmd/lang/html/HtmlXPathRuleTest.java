/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.html;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.Parser;
import net.sourceforge.pmd.lang.ast.Parser.ParserTask;
import net.sourceforge.pmd.lang.ast.SemanticErrorReporter;
import net.sourceforge.pmd.lang.html.ast.ASTHtmlComment;
import net.sourceforge.pmd.lang.html.ast.ASTHtmlDocument;
import net.sourceforge.pmd.lang.html.ast.ASTHtmlTextNode;
import net.sourceforge.pmd.lang.html.ast.HtmlParsingHelper;
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

        List<RuleViolation> violations = runXPath(LIGHTNING_WEB_COMPONENT, xpath);
        Assert.assertEquals(1, violations.size());
        Assert.assertEquals(3, violations.get(0).getBeginLine());
    }

    @Test
    public void selectTextNodeByNodeNameShouldNotWork() {
        String xpath = "//*[local-name() = '#text']";
        List<RuleViolation> violations = runXPath(LIGHTNING_WEB_COMPONENT, xpath);
        Assert.assertEquals(0, violations.size());
    }

    @Test
    public void verifyTextNodeName() {
        ASTHtmlDocument document = HtmlParsingHelper.DEFAULT.parse("<p>foobar</p>");
        ASTHtmlTextNode textNode = document.getFirstDescendantOfType(ASTHtmlTextNode.class);
        Assert.assertEquals("#text", textNode.getXPathNodeName());
    }

    @Test
    public void verifyCommentNodeName() {
        ASTHtmlDocument document = HtmlParsingHelper.DEFAULT.parse("<p><!-- a comment --></p>");
        ASTHtmlComment comment = document.getFirstDescendantOfType(ASTHtmlComment.class);
        Assert.assertEquals("#comment", comment.getXPathNodeName());
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

    @Test
    public void selectAttributesMultiple() {
        // from https://developer.salesforce.com/docs/component-library/documentation/en/lwc/lwc.js_props_getter
        // "Don’t add spaces around the property, for example, { data } is not valid HTML."
        String xpath = "//*[@*[local-name() = ('value', 'onchange')] = '{']";

        List<RuleViolation> violations = runXPath(LIGHTNING_WEB_COMPONENT, xpath);
        Assert.assertEquals(2, violations.size());
        Assert.assertEquals(4, violations.get(0).getBeginLine());
        Assert.assertEquals(6, violations.get(1).getBeginLine());
    }

    @Test
    public void selectAttributeByName() {
        String xpath = "//*[@*[local-name() = 'if:true']]";

        List<RuleViolation> violations = runXPath(LIGHTNING_WEB_COMPONENT, xpath);
        Assert.assertEquals(1, violations.size());
        Assert.assertEquals(10, violations.get(0).getBeginLine());
    }

    private List<RuleViolation> runXPath(String html, String xpath) {
        LanguageVersion htmlLanguage = LanguageRegistry.findLanguageByTerseName(HtmlLanguageModule.TERSE_NAME).getDefaultVersion();
        Parser parser = htmlLanguage.getLanguageVersionHandler().getParser();
        ParserTask parserTask = new ParserTask(htmlLanguage, "n/a", html, SemanticErrorReporter.noop());
        Node node = parser.parse(parserTask);

        List<RuleViolation> violations = new ArrayList<>();
        XPathRule rule = new XPathRule(XPathVersion.DEFAULT, xpath);
        rule.setMessage("test");
        rule.setLanguage(htmlLanguage.getLanguage());
        RuleContext context = RuleContext.create(violations::add, rule);
        rule.apply(node, context);
        return violations;
    }
}
