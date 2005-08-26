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
 * find additive expressions: +
 * check that the addition is between literal and nonliteral
 * if true and also the parent is StringBuffer constructor or append,
 * report a violation.
 * 
 * @author mgriffa
 */
public final class AvoidConcatenatingNonLiteralsInStringBuffer extends AbstractRule {

    public Object visit(ASTAdditiveExpression node, Object data) {
        ASTBlockStatement bs = (ASTBlockStatement) node.getFirstParentOfType(ASTBlockStatement.class);
        if (bs == null) {
            return data;
        }

        if (!concatsLiteralStringAndNonLiteral(node)) {
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

    private boolean concatsLiteralStringAndNonLiteral(ASTAdditiveExpression node) {
        if (!node.containsChildOfType(ASTName.class)) {
            return false;
        }
        ASTLiteral n = (ASTLiteral)node.getFirstChildOfType(ASTLiteral.class);
        if (n == null) {
            return false;
        }
        try {
            Integer.parseInt(n.getImage());
        } catch (NumberFormatException nfe) {
            return true;
        }
        return false;
    }

    private boolean isInStringBufferAppend(ASTAdditiveExpression node) {
        ASTBlockStatement s = (ASTBlockStatement) node.getFirstParentOfType(ASTBlockStatement.class);
        if (s == null) {
            return false;
        }
        ASTName n = (ASTName) s.getFirstChildOfType(ASTName.class);
        return n.getImage()!=null && n.getImage().endsWith("append");
    }
    
    private boolean isAllocatedStringBuffer(ASTAdditiveExpression node) {
        ASTAllocationExpression ao = (ASTAllocationExpression) node.getFirstParentOfType(ASTAllocationExpression.class);
        if (ao == null) {
            return false;
        }
        // note that the child can be an ArrayDimsAndInits, for example, from java.lang.FloatingDecimal:  t = new int[ nWords+wordcount+1 ];
        ASTClassOrInterfaceType an = (ASTClassOrInterfaceType) ao.getFirstChildOfType(ASTClassOrInterfaceType.class);
        return an != null && (an.getImage().endsWith("StringBuffer") || an.getImage().endsWith("StringBuilder"));
    }
}

