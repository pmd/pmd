/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTIfStatement;

public class AvoidDeeplyNestedIfStmtsRule extends AbstractRule {

    private int depth;

    public Object visit(ASTCompilationUnit node, Object data) {
        depth = 0;
        return super.visit(node, data);
    }

    public Object visit(ASTIfStatement node, Object data) {
        if (!node.hasElse()) {
            depth++;
        }
        super.visit(node, data);
        if (depth == getIntProperty("problemDepth")) {
            RuleContext ctx = (RuleContext) data;
            ctx.getReport().addRuleViolation(createRuleViolation(ctx, node.getBeginLine()));
        }
        depth--;
        return data;
    }

}
