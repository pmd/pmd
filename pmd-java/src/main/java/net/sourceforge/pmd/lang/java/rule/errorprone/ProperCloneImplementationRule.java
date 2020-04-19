/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.rule.errorprone;

import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTAllocationExpression;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class ProperCloneImplementationRule extends AbstractJavaRule {

    public ProperCloneImplementationRule() {
        addRuleChainVisit(ASTMethodDeclaration.class);
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        if (!"clone".equals(node.getName()) || node.getArity() > 0) {
            return data;
        }
        
        ASTBlock block = node.getFirstChildOfType(ASTBlock.class);
        if (block == null) {
            return data;
        }
        
        String enclosingClassName = node.getFirstParentOfType(ASTClassOrInterfaceDeclaration.class).getSimpleName();
        if (blockHasAllocations(block, enclosingClassName)) {
            addViolation(data, node);
        }
        
        return data;
    }

    private boolean blockHasAllocations(ASTBlock block, String enclosingClassName) {
        List<ASTAllocationExpression> allocations = block.findDescendantsOfType(ASTAllocationExpression.class);
        for (ASTAllocationExpression alloc : allocations) {
            ASTClassOrInterfaceType type = alloc.getFirstChildOfType(ASTClassOrInterfaceType.class);
            if (type.hasImageEqualTo(enclosingClassName)) {
                return true;
            }
        }
        return false;
    }
}
