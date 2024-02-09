/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ecmascript.rule.AbstractEcmascriptRule;
import net.sourceforge.pmd.reporting.RuleContext;

/**
 * @author Clément Fournier
 */
public class DummyJsRule extends AbstractEcmascriptRule {

    public void apply(Node node, RuleContext ctx) {

    }

    public static class DummyRuleOneViolationPerFile extends DummyJsRule {

        @Override
        public void apply(Node node, RuleContext ctx) {
            ctx.addViolation(node);
        }
    }

}
