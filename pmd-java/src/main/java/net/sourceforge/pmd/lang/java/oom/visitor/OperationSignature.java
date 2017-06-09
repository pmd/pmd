/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.visitor;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;

/**
 * Signature for an operation.
 *
 * @author Clément Fournier
 */
public class OperationSignature extends Signature {

    private static final Map<Integer, OperationSignature> POOL = new HashMap<>();

    public final Role role;
    public final boolean isAbstract;


    private OperationSignature(Visibility visibility, Role role, boolean isAbstract) {
        super(visibility);
        this.role = role;
        this.isAbstract = isAbstract;
    }

    /**
     * Builds an operation signature from a method or constructor declaration.
     *
     * @param node The node
     *
     * @return The signature of the parameter
     */
    public static OperationSignature buildFor(ASTMethodOrConstructorDeclaration node) {
        int code = code(Visibility.get(node), Role.get(node), node.isAbstract());
        if (!POOL.containsKey(code)) {
            POOL.put(code, new OperationSignature(Visibility.get(node), Role.get(node), node.isAbstract()));
        }
        return POOL.get(code);
    }

    /** Used internally by the pooler. */
    private static int code(Visibility visibility, Role role, boolean isAbstract) {
        return visibility.hashCode() * 31 + role.hashCode() * 2 + (isAbstract ? 1 : 0);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof OperationSignature && super.equals(o) && role == ((OperationSignature) o).role
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
        GETTER_OR_SETTER, CONSTRUCTOR, METHOD, STATIC;

        public static Role get(ASTMethodOrConstructorDeclaration node) {
            return node instanceof ASTConstructorDeclaration ? CONSTRUCTOR : get((ASTMethodDeclaration) node);
        }

        private static Role get(ASTMethodDeclaration node) {
            // TODO better getter or setter detection
            boolean isGetterOrSetter = node.getName().startsWith("get")
                || node.getName().startsWith("set");

            return node.isStatic() ? Role.STATIC : isGetterOrSetter ? Role.GETTER_OR_SETTER : Role.METHOD;
        }
    }
}
