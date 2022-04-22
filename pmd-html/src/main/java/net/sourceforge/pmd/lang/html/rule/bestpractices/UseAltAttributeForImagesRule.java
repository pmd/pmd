/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.html.rule.bestpractices;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.html.ast.ASTHtmlElement;
import net.sourceforge.pmd.lang.html.rule.AbstractHtmlRule;

public class UseAltAttributeForImagesRule extends AbstractHtmlRule {

    public UseAltAttributeForImagesRule() {
        addRuleChainVisit("img");
    }

    @Override
    public Object visit(ASTHtmlElement node, Object data) {
        if (!node.hasAttribute("alt")) {
            RuleContext ctx = (RuleContext) data;
            ctx.addViolation(node);
            return data;
        }

        return data;
    }
}
