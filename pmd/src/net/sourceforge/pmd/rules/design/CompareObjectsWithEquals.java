package net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTAllocationExpression;
import net.sourceforge.pmd.ast.ASTEqualityExpression;
import net.sourceforge.pmd.ast.ASTInitializer;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.symboltable.VariableNameDeclaration;

public class CompareObjectsWithEquals extends AbstractRule {

    private boolean hasName(Node n) {
        return n.jjtGetNumChildren() > 0 && n.jjtGetChild(0) instanceof ASTName;
    }
    
    /**
	 * Indicate whether this node is allocating a new object.
	 * 
	 * @param n
	 *            node that might be allocating a new object
	 * @return true if child 0 is an AllocationExpression
	 */
	private boolean isAllocation(Node n) {
        return n.jjtGetNumChildren() > 0 && n.jjtGetChild(0) instanceof ASTAllocationExpression && n.jjtGetParent().jjtGetNumChildren() == 1;
	}
        
    public Object visit(ASTEqualityExpression node, Object data) {
        Node c0 = node.jjtGetChild(0).jjtGetChild(0);
        Node c1 = node.jjtGetChild(1).jjtGetChild(0);

        // If either side is allocating a new object, there's no way an
        // equals expression is correct
        if ((isAllocation(c0)) || (isAllocation(c1))) {
            addViolation(data, node);
            return data;
        }
        
        // skip if either child is not a simple name
        if (!hasName(c0) || !hasName(c1)) {
            return data;
        }

        // skip if either is a qualified name
        if (isQualifiedName((SimpleNode) c0.jjtGetChild(0)) || isQualifiedName((SimpleNode) c1.jjtGetChild(0))) {
            return data;
        }

        // skip static initializers... missing some cases here
        if (!node.getParentsOfType(ASTInitializer.class).isEmpty()) {
            return data;
        }
              
        ASTName n0 = (ASTName) c0.jjtGetChild(0);
        ASTName n1 = (ASTName) c1.jjtGetChild(0);

        if (n0.getNameDeclaration() instanceof VariableNameDeclaration && n1.getNameDeclaration() instanceof VariableNameDeclaration) {
            VariableNameDeclaration nd0 = (VariableNameDeclaration) n0.getNameDeclaration();
            VariableNameDeclaration nd1 = (VariableNameDeclaration) n1.getNameDeclaration();

            // skip array dereferences... this misses some cases
            // FIXME catch comparisons btwn array elements of reference types
            if (nd0.isArray() || nd1.isArray()) {
                return data;
            }

            if (nd0.isReferenceType() && nd1.isReferenceType()) {
                addViolation(data, node);
            }
        }

        return data;
    }
}
