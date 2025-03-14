/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.html.rule.bestpractices;

import net.sourceforge.pmd.lang.html.ast.ASTHtmlElement;
import net.sourceforge.pmd.lang.html.rule.AbstractHtmlRule;
import net.sourceforge.pmd.reporting.RuleContext;

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
        if (node.hasAttribute("type")) {
            addViolation(node, data);
        }
    }

    private void checkLink(ASTHtmlElement node, Object data) {
        String rel = node.getAttribute("rel");
        if (node.hasAttribute("type") && "stylesheet".equalsIgnoreCase(rel)) {
            addViolation(node, data);
        }
    }


    private void addViolation(ASTHtmlElement node, Object data) {
        RuleContext ctx = (RuleContext) data;
        ctx.addViolation(node);
    }
}
