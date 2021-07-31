/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * KEEP PRIVATE
 */
final class AstImplUtil {

    private AstImplUtil() {
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
