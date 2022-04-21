/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript;

import java.util.List;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ecmascript.rule.AbstractEcmascriptRule;

/**
 * @author Clément Fournier
 */
public class DummyJsRule extends AbstractEcmascriptRule {

    @Override
    public void apply(List<? extends Node> nodes, RuleContext ctx) {
        for (Node node : nodes) {
            apply(node, ctx);
        }
    }

    public void apply(Node node, RuleContext ctx) {

    }

    public static class DummyRuleOneViolationPerFile extends DummyJsRule {

        @Override
        public void apply(Node node, RuleContext ctx) {
            ctx.addViolation(node);
        }
    }

}
