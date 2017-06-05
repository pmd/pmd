/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.visitor;

import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;

/**
 * Signature for an operation.
 *
 * @author Clément Fournier
 */
public class OperationSignature extends Signature {

    public final Role role;

    public final boolean isAbstract;

    public OperationSignature(Visibility visibility, Role role, boolean isAbstract) {
        super(visibility);
        this.role = role;
        this.isAbstract = isAbstract;
    }

    /**
     * Builds an operation signature from a method declaration.
     *
     * @param node The method declaration
     *
     * @return The signature of the parameter
     */
    public static OperationSignature buildFor(ASTMethodDeclaration node) {
        // TODO better getter or setter detection
        boolean isGetterOrSetter = node.getName().startsWith("get")
            || node.getName().startsWith("set");
        Role role = isGetterOrSetter ? Role.GETTER_OR_SETTER
                                     : node.isStatic() ? Role.STATIC : Role.METHOD;

        return new OperationSignature(Visibility.get(node), role, node.isAbstract());
    }

    /**
     * Builds an operation signature from a constructor declaration.
     *
     * @param node The constructor declaration
     *
     * @return The signature of the parameter
     */
    public static OperationSignature buildFor(ASTConstructorDeclaration node) {
        return new OperationSignature(Visibility.get(node), Role.CONSTRUCTOR, node.isAbstract());
    }

    /**
     * Builds an operation signature from a method or constructor declaration.
     *
     * @param node The node
     *
     * @return The signature of the parameter
     */
    public static OperationSignature buildFor(ASTMethodOrConstructorDeclaration node) {
        return node instanceof ASTMethodDeclaration ? buildFor((ASTMethodDeclaration) node)
                                                    : buildFor((ASTConstructorDeclaration) node);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof OperationSignature && super.equals(o) && role == (
            (OperationSignature) o).role
            && isAbstract == ((OperationSignature) o).isAbstract;
    }

    @Override
    public int hashCode() {
        return super.hashCode() * 2 + role.hashCode() * 4 + (isAbstract ? 1 : 0);
    }

    /**
     * Role of an operation.
     */
    public enum Role {
        GETTER_OR_SETTER, CONSTRUCTOR, METHOD, STATIC
    }
}
