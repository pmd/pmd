/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.metrics.cyclo;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.java.ast.ASTCatchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTDoStatement;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLabel;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorAdapter;

/**
 * Visitor helping to calculate CYCLO. It visits decision points in the AST and increments a counter in its data Object.
 */
public class CycloOperationVisitor extends JavaParserVisitorAdapter {

    @Override
    public Object visit(ASTSwitchStatement node, Object data) {
        int childCount = node.jjtGetNumChildren();
        int lastIndex = childCount - 1;

        for (int n = 0; n < lastIndex; n++) {
            Node childNode = node.jjtGetChild(n);
            if (childNode instanceof ASTSwitchLabel) {
                // default is generally not considered a decision (same as "else")
                ASTSwitchLabel sl = (ASTSwitchLabel) childNode;
                if (!sl.isDefault()) {
                    childNode = node.jjtGetChild(n + 1);
                    if (childNode instanceof ASTBlockStatement) {
                        ((Accumulator) data).addDecisionPoint();
                    }
                }
            }
        }
        super.visit(node, data);
        return data;
    }

    @Override
    public Object visit(ASTConditionalExpression node, Object data) {
        if (node.isTernary()) {
            ((Accumulator) data).addDecisionPoint();
            super.visit(node, data);
        }
        return data;
    }

    @Override
    public Object visit(ASTWhileStatement node, Object data) {
        ((Accumulator) data).addDecisionPoint();
        super.visit(node, data);
        return data;
    }

    @Override
    public Object visit(ASTIfStatement node, Object data) {
        ((Accumulator) data).addDecisionPoint();
        super.visit(node, data);
        return data;
    }

    @Override
    public Object visit(ASTCatchStatement node, Object data) {
        ((Accumulator) data).addDecisionPoint();
        super.visit(node, data);
        return data;
    }

    @Override
    public Object visit(ASTForStatement node, Object data) {
        ((Accumulator) data).addDecisionPoint();
        super.visit(node, data);
        return data;
    }

    @Override
    public Object visit(ASTDoStatement node, Object data) {
        ((Accumulator) data).addDecisionPoint();
        super.visit(node, data);
        return data;
    }
}
