/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTUnaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTUnaryExpressionNotPlusMinus;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class AvoidMultipleUnaryOperatorsRule extends AbstractJavaRule {

    public AvoidMultipleUnaryOperatorsRule() {
        super.addRuleChainVisit(ASTUnaryExpression.class);
        super.addRuleChainVisit(ASTUnaryExpressionNotPlusMinus.class);
    }

    @Override
    public Object visit(ASTUnaryExpression node, Object data) {
        checkUnaryDescendent(node, data);
        return data;
    }

    @Override
    public Object visit(ASTUnaryExpressionNotPlusMinus node, Object data) {
        checkUnaryDescendent(node, data);
        return data;
    }

    private void checkUnaryDescendent(Node node, Object data) {
        boolean match = false;
        if (node.getNumChildren() == 1) {
            Node child = node.getChild(0);
            if (child instanceof ASTUnaryExpression || child instanceof ASTUnaryExpressionNotPlusMinus) {
                match = true;
            } else if (child instanceof ASTPrimaryExpression) {
                Node primaryExpression = child;
                // Skip down PrimaryExpression/PrimaryPrefix/Expression chains
                // created by parentheses
                while (true) {
                    if (primaryExpression.getNumChildren() == 1
                            && primaryExpression.getChild(0) instanceof ASTPrimaryPrefix
                            && primaryExpression.getChild(0).getNumChildren() == 1
                            && primaryExpression.getChild(0).getChild(0) instanceof ASTExpression
                            && primaryExpression.getChild(0).getChild(0).getNumChildren() == 1) {
                        Node candidate = primaryExpression.getChild(0).getChild(0).getChild(0);
                        if (candidate instanceof ASTUnaryExpression
                                || candidate instanceof ASTUnaryExpressionNotPlusMinus) {
                            match = true;
                            break;
                        } else if (candidate instanceof ASTPrimaryExpression) {
                            primaryExpression = candidate;
                            continue;
                        } else {
                            break;
                        }
                    } else {
                        break;
                    }
                }
            }
        }

        if (match) {
            addViolation(data, node);
        }
    }
}
