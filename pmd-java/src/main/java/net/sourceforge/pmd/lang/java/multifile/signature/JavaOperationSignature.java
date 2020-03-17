/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.multifile.signature;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameters;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTResultType;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.symboltable.ClassScope;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

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

            ClassScope scope = node.getScope().getEnclosingScope(ClassScope.class);

            // fields names mapped to their types
            Map<String, String> fieldNames = new HashMap<>();

            for (Map.Entry<VariableNameDeclaration, List<NameOccurrence>> decl
                : scope.getVariableDeclarations().entrySet()) {

                ASTFieldDeclaration field = decl.getKey()
                                                .getNode()
                                                .getFirstParentOfType(ASTFieldDeclaration.class);

                // the field might be null in record types - the fields for the record components are synthetic
                if (field != null) {
                    Matcher matcher = FIELD_NAME_PATTERN.matcher(field.getVariableName());
                    String varName = matcher.find() ? matcher.group(1) : field.getVariableName();

                    fieldNames.put(varName, field.getFirstChildOfType(ASTType.class).getTypeImage());
                }
            }

            return isGetter(node, fieldNames) || isSetter(node, fieldNames);
        }


        /** Attempts to determine if the method is a getter. */
        private static boolean isGetter(ASTMethodDeclaration node, Map<String, String> fieldNames) {

            if (node.getFirstDescendantOfType(ASTFormalParameters.class).size() != 0
                || node.getFirstDescendantOfType(ASTResultType.class).isVoid()) {
                return false;
            }

            if (node.getName().startsWith("get")) {
                return containsIgnoreCase(fieldNames.keySet(), node.getName().substring(3));
            } else if (node.getName().startsWith("is")) {
                return containsIgnoreCase(fieldNames.keySet(), node.getName().substring(2));
            }


            return fieldNames.containsKey(node.getName());
        }


        /** Attempts to determine if the method is a setter. */
        private static boolean isSetter(ASTMethodDeclaration node, Map<String, String> fieldNames) {

            if (node.getFirstDescendantOfType(ASTFormalParameters.class).size() != 1
                || !node.getFirstDescendantOfType(ASTResultType.class).isVoid()) {
                return false;
            }

            if (node.getName().startsWith("set")) {
                return containsIgnoreCase(fieldNames.keySet(), node.getName().substring(3));
            }

            return fieldNames.containsKey(node.getName());
        }


        private static boolean containsIgnoreCase(Set<String> set, String str) {
            for (String s : set) {
                if (str.equalsIgnoreCase(s)) {
                    return true;
                }
            }
            return false;
        }
    }
}
