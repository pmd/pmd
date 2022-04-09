/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.Node;

/**
 * KEEP PRIVATE
 *
 * @author Clément Fournier
 */
final class AstImplUtil {

    private AstImplUtil() {

    }

    public static String getLastSegment(String nameWithDots, char sep) {
        assert nameWithDots != null;
        int lastIdx = nameWithDots.lastIndexOf(sep);
        return lastIdx < 0 ? nameWithDots : nameWithDots.substring(lastIdx + 1);
    }

    public static String getFirstSegment(String nameWithDots, char sep) {
        assert nameWithDots != null;
        int lastIdx = nameWithDots.indexOf(sep);
        return lastIdx < 0 ? nameWithDots : nameWithDots.substring(0, lastIdx);
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
        assert expression instanceof AbstractJavaExpr
            : expression.getClass() + " doesn't have parenDepth attribute!";

        ((AbstractJavaExpr) expression).bumpParenDepth();
    }

    static void bumpParenDepth(ASTPattern pattern) {
        assert pattern instanceof ASTTypePattern || pattern instanceof ASTGuardedPattern
            : pattern.getClass() + " doesn't have parenDepth attribute!";

        if (pattern instanceof ASTTypePattern) {
            ((ASTTypePattern) pattern).bumpParenDepth();
        } else if (pattern instanceof ASTGuardedPattern) {
            ((ASTGuardedPattern) pattern).bumpParenDepth();
        }
    }
}
