/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTAdditiveExpression;
import net.sourceforge.pmd.ast.ASTAllocationExpression;
import net.sourceforge.pmd.ast.ASTBlockStatement;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceType;
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

    public Object visit(ASTAdditiveExpression node, Object data) {
        final ASTBlockStatement bs = (ASTBlockStatement) node.getFirstParentOfType(ASTBlockStatement.class);
        if (bs == null) {
            return data;
        }

        if (!concatsLiteralAndNonLiteral(node)) {
            return data;
        }
        
        if (bs.isAllocation()) {
            if (isAllocatedStringBuffer(node)) {
                addViolation(data, node);
            }
        } else if (isInStringBufferAppend(node)) {
            addViolation(data, node);
        }
        return data;
    }

    private boolean concatsLiteralAndNonLiteral(final ASTAdditiveExpression node) {
        return node.containsChildOfType(ASTName.class) && node.containsChildOfType(ASTLiteral.class);
    }

    private boolean isInStringBufferAppend(final ASTAdditiveExpression node) {
        final ASTStatement s = (ASTStatement) node.getFirstParentOfType(ASTStatement.class);
        if (s == null) {
            return false;
        }
        final ASTName n = (ASTName) s.getFirstChildOfType(ASTName.class);
        return n.getImage()!=null && n.getImage().endsWith("append");
    }
    
    private boolean isAllocatedStringBuffer(final ASTAdditiveExpression node) {
        final ASTAllocationExpression ao = (ASTAllocationExpression) node.getFirstParentOfType(ASTAllocationExpression.class);
        if (ao == null) {
            return false;
        }
        // note that the child can be an ArrayDimsAndInits, for example, from java.lang.FloatingDecimal:  t = new int[ nWords+wordcount+1 ];
        final ASTClassOrInterfaceType an = (ASTClassOrInterfaceType) ao.getFirstChildOfType(ASTClassOrInterfaceType.class);
        return an != null && an.getImage().endsWith("StringBuffer");
    }
}

