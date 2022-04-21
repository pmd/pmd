/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule;

import java.util.List;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.JavaNode;

/**
 * @author Clément Fournier
 */
public class DummyJavaRule extends AbstractJavaRule {

    @Override
    public void apply(List<? extends Node> nodes, RuleContext ctx) {
        for (Node node : nodes) {
            apply(node, ctx);
        }
    }

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
            ((JavaNode) node).jjtAccept(this, ctx);
        }

        @Override
        public Object visit(ASTVariableDeclaratorId node, Object data) {
            asCtx(data).addViolation(node, node.getName());
            return super.visit(node, data);
        }
    }
}
