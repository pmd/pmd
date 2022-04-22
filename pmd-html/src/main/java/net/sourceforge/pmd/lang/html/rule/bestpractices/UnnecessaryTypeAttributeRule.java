/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.html.rule.bestpractices;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.xpath.Attribute;
import net.sourceforge.pmd.lang.html.ast.ASTHtmlElement;
import net.sourceforge.pmd.lang.html.rule.AbstractHtmlRule;

public class UnnecessaryTypeAttributeRule extends AbstractHtmlRule {

    @Override
    public Object visit(ASTHtmlElement node, Object data) {
        if ("link".equalsIgnoreCase(node.getNodeName())) {
            checkLink(node, data);
        } else if ("script".equalsIgnoreCase(node.getNodeName())) {
            checkScript(node, data);
        }
        return super.visit(node, data);
    }

    private void checkScript(ASTHtmlElement node, Object data) {
        if (getAttribute(node, "type") != null) {
            addViolation(node, data);
        }
    }

    private void checkLink(ASTHtmlElement node, Object data) {
        String rel = getAttribute(node, "rel");
        if (getAttribute(node, "type") != null && "stylesheet".equalsIgnoreCase(rel)) {
            addViolation(node, data);
        }
    }

    private String getAttribute(ASTHtmlElement node, String rel) {
        return node.getAttributes().stream()
                .filter(attribute -> rel.equalsIgnoreCase(attribute.getName()))
                .findFirst()
                .map(Attribute::getValue)
                .map(String::valueOf)
                .orElse(null);
    }

    private void addViolation(ASTHtmlElement node, Object data) {
        RuleContext ctx = (RuleContext) data;
        ctx.addViolation(node);
    }
}
