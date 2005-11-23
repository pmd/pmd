/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules.strings;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.symboltable.NameDeclaration;
import net.sourceforge.pmd.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.ast.ASTAdditiveExpression;
import net.sourceforge.pmd.ast.ASTAllocationExpression;
import net.sourceforge.pmd.ast.ASTBlockStatement;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.ast.ASTLiteral;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.Node;

import java.util.List;
import java.util.Iterator;

/*
 * How this rule works:
 * find additive expressions: +
 * check that the addition is between anything other than two literals
 * if true and also the parent is StringBuffer constructor or append,
 * report a violation.
 * 
 * @author mgriffa
 */
public class InefficientStringBuffering extends AbstractRule {

    public Object visit(ASTAdditiveExpression node, Object data) {
        ASTBlockStatement bs = (ASTBlockStatement) node.getFirstParentOfType(ASTBlockStatement.class);
        if (bs == null) {
            return data;
        }

        // two literals?  usually OK, although this misses buf.append("1" + "2" + (a+b))
        if (node.findChildrenOfType(ASTLiteral.class).size() == 2) {
            return data;
        }

        List nodes = node.findChildrenOfType(ASTLiteral.class);
        for (Iterator i = nodes.iterator();i.hasNext();) {
            ASTLiteral literal = (ASTLiteral)i.next();
            try {
                Integer.parseInt(literal.getImage());
                return data;
            } catch (NumberFormatException nfe) {
                // NFE means new StringBuffer("a" + "b"), want to flag those
            }
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

    private boolean isInStringBufferAppend(ASTAdditiveExpression node) {
        if (!eighthParentIsBlockStatement(node)) {
            return false;
        }
        ASTBlockStatement s = (ASTBlockStatement) node.getFirstParentOfType(ASTBlockStatement.class);
        if (s == null) {
            return false;
        }
        ASTName n = (ASTName) s.getFirstChildOfType(ASTName.class);
        if (!(n.getNameDeclaration() instanceof VariableNameDeclaration)) {
            return false;
        }

        VariableNameDeclaration vnd = (VariableNameDeclaration)n.getNameDeclaration();
        return vnd.getTypeImage().equals("StringBuffer");
    }

    private boolean eighthParentIsBlockStatement(ASTAdditiveExpression node) {
        Node curr = node;
        for (int i=0; i<8; i++) {
            if (node.jjtGetParent() == null) {
                return false;
            }
            curr = curr.jjtGetParent();
        }
        return curr instanceof ASTBlockStatement;
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

