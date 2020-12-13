/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.multifile.signature;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.symbols.JElementSymbol;

/**
 * Signature for an operation.
 *
 * @author Clément Fournier
 */
public final class JavaOperationSignature extends JavaSignature<ASTMethodOrConstructorDeclaration> {

    private static final Map<Integer, JavaOperationSignature> POOL = new ConcurrentHashMap<>();
    public final Role role;
    public final boolean isAbstract;


    private JavaOperationSignature(Visibility visibility, Role role, boolean isAbstract) {
        super(visibility);
        this.role = role;
        this.isAbstract = isAbstract;
    }


    @Override
    public boolean equals(Object o) {
        return this == o;
    }


    @Override
    public int hashCode() {
        return code(visibility, role, isAbstract);
    }


    @Override
    public String toString() {
        return "JavaOperationSignature{"
            + "role=" + role
            + ", isAbstract=" + isAbstract
            + ", visibility=" + visibility
            + '}';
    }


    /** Used internally by the pooler. */
    private static int code(Visibility visibility, Role role, boolean isAbstract) {
        return visibility.hashCode() * 31 + role.hashCode() * 2 + (isAbstract ? 1 : 0);
    }


    /**
     * Builds an operation signature from a method or constructor declaration.
     *
     * @param node The node
     *
     * @return The signature of the parameter
     */
    public static JavaOperationSignature buildFor(ASTMethodOrConstructorDeclaration node) {
        int code = code(Visibility.get(node), Role.get(node), node.isAbstract());
        if (!POOL.containsKey(code)) {
            POOL.put(code, new JavaOperationSignature(Visibility.get(node), Role.get(node), node.isAbstract()));
        }
        return POOL.get(code);
    }


    /**
     * Role of an operation.
     */
    public enum Role {
        GETTER_OR_SETTER, CONSTRUCTOR, METHOD, STATIC;

        private static final Pattern FIELD_NAME_PATTERN = Pattern.compile("(?:m_|_)?(\\w+)");


        public static Role get(ASTMethodOrConstructorDeclaration node) {
            return node instanceof ASTConstructorDeclaration ? CONSTRUCTOR : get((ASTMethodDeclaration) node);
        }


        private static Role get(ASTMethodDeclaration node) {
            if (node.isStatic()) {
                return STATIC;
            } else if (isGetterOrSetter(node)) {
                return GETTER_OR_SETTER;
            } else {
                return METHOD;
            }
        }


        private static boolean isGetterOrSetter(ASTMethodDeclaration node) {
            return isGetter(node) || isSetter(node);
        }

        private static boolean containerHasFieldNamed(ASTMethodDeclaration m, String nameIgnoringCase) {
            return m.getEnclosingType()
                    .getSymbol()
                    .getDeclaredFields()
                    .stream()
                    .map(JElementSymbol::getSimpleName)
                    .anyMatch(nameIgnoringCase::equalsIgnoreCase);
        }


        /** Attempts to determine if the method is a getter. */
        private static boolean isGetter(ASTMethodDeclaration node) {

            if (node.getArity() != 0 || node.isVoid()) {
                return false;
            }

            if (node.getName().startsWith("get")) {
                return containerHasFieldNamed(node, node.getName().substring(3));
            } else if (node.getName().startsWith("is")) {
                return containerHasFieldNamed(node, node.getName().substring(2));
            }


            return containerHasFieldNamed(node, node.getName());
        }


        /** Attempts to determine if the method is a setter. */
        private static boolean isSetter(ASTMethodDeclaration node) {

            if (node.getArity() != 1 || !node.isVoid()) {
                return false;
            }

            if (node.getName().startsWith("set")) {
                return containerHasFieldNamed(node, node.getName().substring(3));
            }

            return containerHasFieldNamed(node, node.getName());
        }
    }
}
