/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.internal;

import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameters;
import net.sourceforge.pmd.lang.java.ast.ASTList;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;

/**
 * Utilities for rules to query the AST.
 */
public final class RuleAstUtil {

    private RuleAstUtil() {
        // utility class
    }


    /**
     * Returns true if the formal parameters of the method or constructor
     * match the given types exactly. Note that for varargs methods, the
     * last param must have an array type (but it is not checked to be varargs).
     * This will return false if we're not sure.
     *
     * @param node  Method or ctor
     * @param types List of types to match (may be empty)
     *
     * @throws NullPointerException If any of the classes is null, or the node is null
     * @see TypeTestUtil#isExactlyA(Class, TypeNode)
     */
    public static boolean hasParameters(ASTMethodOrConstructorDeclaration node, Class<?>... types) {
        ASTFormalParameters formals = node.getFormalParameters();
        if (formals.size() != types.length) {
            return false;
        }
        for (int i = 0; i < formals.size(); i++) {
            ASTFormalParameter fi = formals.get(i);
            if (!TypeTestUtil.isExactlyA(types[i], fi)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true if the {@code throws} declaration of the method or constructor
     * matches the given types exactly.
     *
     * @param node  Method or ctor
     * @param types List of exception types to match (may be empty)
     *
     * @throws NullPointerException If any of the classes is null, or the node is null
     * @see TypeTestUtil#isExactlyA(Class, TypeNode)
     */
    @SafeVarargs
    public static boolean hasExceptionList(ASTMethodOrConstructorDeclaration node, Class<? extends Throwable>... types) {
        @NonNull List<ASTClassOrInterfaceType> formals = ASTList.orEmpty(node.getThrowsList());
        if (formals.size() != types.length) {
            return false;
        }
        for (int i = 0; i < formals.size(); i++) {
            ASTClassOrInterfaceType fi = formals.get(i);
            if (!TypeTestUtil.isExactlyA(types[i], fi)) {
                return false;
            }
        }
        return true;
    }
}
