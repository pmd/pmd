/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.html.rule.bestpractices;

import java.util.Arrays;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.html.ast.ASTHtmlElement;
import net.sourceforge.pmd.lang.html.rule.AbstractHtmlRule;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;
import net.sourceforge.pmd.reporting.RuleContext;

public class UseAltAttributeForImagesRule extends AbstractHtmlRule {

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forXPathNames(Arrays.asList("img"));
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
