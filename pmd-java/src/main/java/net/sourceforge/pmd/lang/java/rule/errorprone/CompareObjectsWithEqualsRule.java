/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAllocationExpression;
import net.sourceforge.pmd.lang.java.ast.ASTEqualityExpression;
import net.sourceforge.pmd.lang.java.ast.ASTInitializer;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTReferenceType;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;

public class CompareObjectsWithEqualsRule extends AbstractJavaRule {

    private boolean hasName(Node n) {
        return n.getNumChildren() > 0 && n.getChild(0) instanceof ASTName;
    }

    /**
     * Indicate whether this node is allocating a new object.
     *
     * @param n
     *            node that might be allocating a new object
     * @return true if child 0 is an AllocationExpression
     */
    private boolean isAllocation(Node n) {
        return n.getNumChildren() > 0 && n.getChild(0) instanceof ASTAllocationExpression
                && n.getParent().getNumChildren() == 1;
    }

    @Override
    public Object visit(ASTEqualityExpression node, Object data) {
        Node c0 = node.getChild(0).getChild(0);
        Node c1 = node.getChild(1).getChild(0);

        // If either side is allocating a new object, there's no way an
        // equals expression is correct
        if (isAllocation(c0) || isAllocation(c1)) {
            addViolation(data, node);
            return data;
        }

        // skip if either child is not a simple name
        if (!hasName(c0) || !hasName(c1)) {
            return data;
        }

        // skip if either is a qualified name
        if (isQualifiedName(c0.getChild(0)) || isQualifiedName(c1.getChild(0))) {
            return data;
        }

        // skip if either is part of a qualified name
        if (isPartOfQualifiedName(node.getChild(0)) || isPartOfQualifiedName(node.getChild(1))) {
            return data;
        }

        // skip static initializers... missing some cases here
        if (!node.getParentsOfType(ASTInitializer.class).isEmpty()) {
            return data;
        }

        ASTName n0 = (ASTName) c0.getChild(0);
        ASTName n1 = (ASTName) c1.getChild(0);

        if (n0.getNameDeclaration() instanceof VariableNameDeclaration
                && n1.getNameDeclaration() instanceof VariableNameDeclaration) {
            VariableNameDeclaration nd0 = (VariableNameDeclaration) n0.getNameDeclaration();
            VariableNameDeclaration nd1 = (VariableNameDeclaration) n1.getNameDeclaration();

            // skip array dereferences... this misses some cases
            // FIXME catch comparisons btwn array elements of reference types
            if (nd0.isArray() || nd1.isArray()) {
                return data;
            }

            if (nd0.isReferenceType() && nd1.isReferenceType()) {
                ASTReferenceType type0 = ((Node) nd0.getAccessNodeParent())
                        .getFirstDescendantOfType(ASTReferenceType.class);
                ASTReferenceType type1 = ((Node) nd1.getAccessNodeParent())
                        .getFirstDescendantOfType(ASTReferenceType.class);
                // skip, if it is an enum
                if (type0.getType() != null && type0.getType().equals(type1.getType())
                    // It may be a custom enum class or an explicit Enum class usage
                    && (type0.getType().isEnum() || type0.getType() == java.lang.Enum.class)) {
                    return data;
                }

                addViolation(data, node);
            }
        }

        return data;
    }

    /**
     * Checks whether the given node contains a qualified name, consisting of
     * one ASTPrimaryPrefix and one or more ASTPrimarySuffix nodes.
     *
     * @param node
     *            the node
     * @return <code>true</code> if it is a qualified name
     */
    private boolean isPartOfQualifiedName(Node node) {
        return node.getChild(0) instanceof ASTPrimaryPrefix
                && !node.findChildrenOfType(ASTPrimarySuffix.class).isEmpty();
    }
}
