/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules.optimization;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTAllocationExpression;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTDoStatement;
import net.sourceforge.pmd.ast.ASTForStatement;
import net.sourceforge.pmd.ast.ASTWhileStatement;
import net.sourceforge.pmd.ast.Node;

public class AvoidInstantiatingObjectsInLoops extends AbstractOptimizationRule {

    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (node.isInterface()) {
            return data;
        }
        return super.visit(node, data);
    }


    public Object visit(ASTAllocationExpression node, Object data) {
        if (insideLoop(node)) {
            addViolation((RuleContext) data, node);
        }
        return data;
    }

    private boolean insideLoop(ASTAllocationExpression node) {
        Node doNode = node.getFirstParentOfType(ASTDoStatement.class);
        Node whileNode = node.getFirstParentOfType(ASTWhileStatement.class);
        Node forNode = node.getFirstParentOfType(ASTForStatement.class);
        if (doNode!=null)
            return true;
        if (whileNode!=null)
            return true;
        if (forNode!=null)
            return true;
        return false;
    }
}
