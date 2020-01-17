/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.Node;

/**
 * KEEP PRIVATE
 * @author Clément Fournier
 */
final class AstImplUtil {

    private AstImplUtil() {

    }

    @Nullable
    public static <T extends Node> T getChildAs(JavaNode javaNode, int idx, Class<T> type) {
        if (javaNode.getNumChildren() <= idx || idx < 0) {
            return null;
        }
        Node child = javaNode.getChild(idx);
        return type.isInstance(child) ? type.cast(child) : null;
    }


    static void bumpParenDepth(ASTExpression expression) {
        if (expression instanceof AbstractJavaExpr) {
            ((AbstractJavaExpr) expression).bumpParenDepth();
        } else if (expression instanceof ASTLambdaExpression) {
            ((ASTLambdaExpression) expression).bumpParenDepth();
        } else {
            throw new IllegalStateException(expression.getClass() + " doesn't have parenDepth attribute!");
        }
    }
}
