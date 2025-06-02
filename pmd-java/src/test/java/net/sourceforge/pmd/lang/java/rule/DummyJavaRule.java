/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTVariableId;
import net.sourceforge.pmd.reporting.RuleContext;

/**
 * @author Clément Fournier
 */
public class DummyJavaRule extends AbstractJavaRule {

    public void apply(Node node, RuleContext ctx) {

    }

    public static class DummyRuleOneViolationPerFile extends DummyJavaRule {

        @Override
        public void apply(Node node, RuleContext ctx) {
            ctx.addViolation(node);
        }
    }

    public static class DummyRulePrintsVars extends DummyJavaRule {

        @Override
        public void apply(Node node, RuleContext ctx) {
            node.acceptVisitor(this, ctx);
        }

        @Override
        public Object visit(ASTVariableId node, Object data) {
            asCtx(data).addViolation(node, node.getName());
            return super.visit(node, data);
        }
    }
}
