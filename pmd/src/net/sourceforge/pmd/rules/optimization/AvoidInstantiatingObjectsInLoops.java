/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules.optimization;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTAllocationExpression;
import net.sourceforge.pmd.ast.ASTDoStatement;
import net.sourceforge.pmd.ast.ASTForStatement;
import net.sourceforge.pmd.ast.ASTInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTWhileStatement;
import net.sourceforge.pmd.ast.Node;

public class AvoidInstantiatingObjectsInLoops extends AbstractOptimizationRule {

    public Object visit(ASTInterfaceDeclaration decl, Object data) {
        return data; // just skip interfaces
    }

    public Object visit(ASTAllocationExpression node, Object data) {
        if (insideLoop(node)) {
            addViolation((RuleContext) data, node.getBeginLine());
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
