/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.html;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.html.ast.ASTHtmlElement;
import net.sourceforge.pmd.lang.html.rule.AbstractHtmlRule;
import net.sourceforge.pmd.lang.rule.xpath.Attribute;

class HtmlJavaRuleTest {
    // from https://developer.salesforce.com/docs/component-library/documentation/en/lwc/lwc.js_props_getter
    private static final String LIGHTNING_WEB_COMPONENT = "<!-- helloExpressions.html -->\n"
            + "<template>\n"
            + "    <p>Hello, { greeting}!</p>\n"
            + "    <lightning-input label=\"Name\" value={ greeting} onchange=\"{ handleChange }\"></lightning-input>\n"
            + "    <div class=\"slds-m-around_medium\">\n"
            + "        <lightning-input name='firstName' label=\"First Name\" onchange={ handleChange }></lightning-input>\n"
            + "        <lightning-input name='lastName' label=\"Last Name\" onchange={handleChange}></lightning-input>\n"
            + "        <p class=\"slds-m-top_medium\">Uppercased Full Name: {uppercasedFullName}</p>\n"
            + "    </div>\n"
            + "</template>";

    @Test
    void findAllAttributesWithInvalidExpression() {
        // "Don’t add spaces around the property, for example, { data } is not valid HTML."
        Rule rule = new AbstractHtmlRule() {
            @Override
            public String getMessage() {
                return "Invalid expression";
            }

            @Override
            public Object visit(ASTHtmlElement node, Object data) {
                for (Attribute attribute : node.getAttributes()) {
                    if ("{".equals(attribute.getValue())) {
                        RuleContext ctx = (RuleContext) data;
                        ctx.addViolation(node);
                    }
                }
                return super.visit(node, data);
            }
        };
        rule.setLanguage(HtmlParsingHelper.DEFAULT.getLanguage());
        List<RuleViolation> violations = runRule(LIGHTNING_WEB_COMPONENT, rule);
        assertEquals(2, violations.size());
        assertEquals(4, violations.get(0).getBeginLine());
        assertEquals(6, violations.get(1).getBeginLine());
    }

    private List<RuleViolation> runRule(String html, Rule rule) {
        Report report = HtmlParsingHelper.DEFAULT.executeRule(rule, html);
        return report.getViolations();
    }
}
