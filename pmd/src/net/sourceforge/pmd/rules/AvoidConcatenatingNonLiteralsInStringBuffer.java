/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTAdditiveExpression;
import net.sourceforge.pmd.ast.ASTAllocationExpression;
import net.sourceforge.pmd.ast.ASTBlockStatement;
import net.sourceforge.pmd.ast.ASTLiteral;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTStatement;

/*
 * How this rule works:
 * find additive expresions: +
 * check that the additions is between literal and nonliteral
 * if true and also the parent is StringBuffer constructor or append,
 * report a violation.
 * 
 * @author mgriffa
 */
public final class AvoidConcatenatingNonLiteralsInStringBuffer extends AbstractRule {

    public final Object visit(ASTAdditiveExpression node, Object data) {
        final ASTBlockStatement bs = (ASTBlockStatement) node.getFirstParentOfType(ASTBlockStatement.class);
        
        if (!concatsLiteralAndNonLiteral(node)) {
            return data;
        }
        
        if (bs.isAllocation()) {
            if (isAllocatedStringBuffer(node)) {
                addViolation((RuleContext) data, node.getBeginLine());
            }
        } else if (isInStringBufferAppend(node)) {
            addViolation((RuleContext) data, node.getBeginLine());
        }
        return data;
    }

    private final boolean concatsLiteralAndNonLiteral(final ASTAdditiveExpression node) {
        return node.containsChildOfType(ASTName.class) && node.containsChildOfType(ASTLiteral.class);
    }

    private final boolean isInStringBufferAppend(final ASTAdditiveExpression node) {
        final ASTStatement s = (ASTStatement) node.getFirstParentOfType(ASTStatement.class);
        final ASTName n = (ASTName) s.getFirstChildOfType(ASTName.class);
        if (n.getImage()!=null && n.getImage().endsWith("append"))
            return true;
        return false;
    }
    
    private final boolean isAllocatedStringBuffer(final ASTAdditiveExpression node) {
        final ASTAllocationExpression ao = (ASTAllocationExpression) node.getFirstParentOfType(ASTAllocationExpression.class);
        final ASTName an = (ASTName) ao.getFirstChildOfType(ASTName.class);
        if (an.getImage().endsWith("StringBuffer"))
            return true;
        return false;
    }
}

