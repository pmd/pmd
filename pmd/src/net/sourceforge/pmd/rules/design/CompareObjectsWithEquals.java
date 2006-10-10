package net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.AbstractRule;
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
    
    public Object visit(ASTEqualityExpression node, Object data) {
        // skip if either child is not a simple name
        if (!hasName(((SimpleNode) node.jjtGetChild(0)).jjtGetChild(0)) || !hasName(((SimpleNode) node.jjtGetChild(1)).jjtGetChild(0))) {
            return data;
        }

        // skip if either is a qualified name
        if (isQualifiedName((SimpleNode) node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0))
            || isQualifiedName((SimpleNode) node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0))) {
            return data;
        }

        // skip static initializers... missing some cases here
        if (!node.getParentsOfType(ASTInitializer.class).isEmpty()) {
            return data;
        }

        ASTName n0 = (ASTName) node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0);
        ASTName n1 = (ASTName) node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0);

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
