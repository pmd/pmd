/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTResultType;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class MethodTypeAndNameIsInconsistentRule extends AbstractJavaRule {
    private static final Set<String> BOOLEAN_PREFIXES;

    static {
        final Set<String> prefixCollection = new HashSet<String>();
        prefixCollection.add("is");
        prefixCollection.add("has");
        prefixCollection.add("can");
        prefixCollection.add("have");
        prefixCollection.add("will");
        prefixCollection.add("should");
        BOOLEAN_PREFIXES = Collections.unmodifiableSet(prefixCollection);
    }

    public MethodTypeAndNameIsInconsistentRule() {
        addRuleChainVisit(ASTMethodDeclaration.class);
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        String nameOfMethod = node.getMethodName();

        checkBooleanMethods(node, data, nameOfMethod);
        checkSetters(node, data, nameOfMethod);
        checkGetters(node, data, nameOfMethod);
        checkTransformMethods(node, data, nameOfMethod);

        return data;
    }

    private void checkTransformMethods(ASTMethodDeclaration node, Object data, String nameOfMethod) {
        ASTResultType resultType = node.getResultType();
        if (nameOfMethod.contains("To") && resultType.isVoid()) {
            // To in the middle somewhere
            // a transform method shouldn't return void linguistically
            addViolation(data, node);
        } else if (hasPrefix(nameOfMethod, "to") && resultType.isVoid()) {
            // a transform method shouldn't return void linguistically
            addViolation(data, node);
        }
    }

    private void checkGetters(ASTMethodDeclaration node, Object data, String nameOfMethod) {
        ASTResultType resultType = node.getResultType();
        if (hasPrefix(nameOfMethod, "get") && resultType.isVoid()) {
            // get method shouldn't return void linguistically
            addViolation(data, node);
        }
    }

    private void checkSetters(ASTMethodDeclaration node, Object data, String nameOfMethod) {
        ASTResultType resultType = node.getResultType();
        if (hasPrefix(nameOfMethod, "set") && !resultType.isVoid()) {
            // set method shouldn't return any type except void linguistically
            addViolation(data, node);
        }
    }

    private void checkBooleanMethods(ASTMethodDeclaration node, Object data, String nameOfMethod) {
        ASTResultType resultType = node.getResultType();
        ASTType t = node.getResultType().getFirstChildOfType(ASTType.class);
        if (!resultType.isVoid() && t != null) {
            for (String prefix : BOOLEAN_PREFIXES) {
                if (hasPrefix(nameOfMethod, prefix) && !"boolean".equals(t.getType().getName())) {
                    addViolation(data, node);
                }
            }
        }
    }

    private static boolean hasPrefix(String name, String prefix) {
        return name.startsWith(prefix) && name.length() > prefix.length()
                && Character.isUpperCase(name.charAt(prefix.length()));
    }
}
