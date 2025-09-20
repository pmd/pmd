/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.reporting.RuleContext;

/**
 * Rule that checks {@link Comparable} classes for proper equals/hashCode implementations.
 *
 * <p>This is essentially the same as the rule {@link OverrideBothEqualsAndHashcodeRule} but only for
 * {@link Comparable} types.
 *
 * @see <a href="https://www.jetbrains.com/help/inspectopedia/ComparableImplementedButEqualsNotOverridden.html">ComparableImplementedButEqualsNotOverridden</a>
 * @since 7.17.0
 */
public class OverrideBothEqualsAndHashCodeOnComparableRule extends OverrideBothEqualsAndHashcodeRule {
    private static final String MESSAGE_PREFIX = "When implementing Comparable, ";
    private static final String MISSING_HASH_CODE = MESSAGE_PREFIX + "hashCode() should be overridden";
    private static final String MISSING_EQUALS = MESSAGE_PREFIX + "equals() should be overridden";
    private static final String MISSING_EQUALS_AND_HASH_CODE = MESSAGE_PREFIX + "both equals() and hashCode() should be overridden";

    @Override
    protected boolean skipType(ASTTypeDeclaration node) {
        return !TypeTestUtil.isA(Comparable.class, node) || TypeTestUtil.isA(Enum.class, node);
    }

    private static boolean hasBrokenEqualsMethod(ASTTypeDeclaration node) {
        for (ASTMethodDeclaration m : node.getDeclarations(ASTMethodDeclaration.class)) {
            if ("equals".equals(m.getName()) && !JavaAstUtils.isEqualsMethod(m)) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected void maybeReport(RuleContext ctx, ASTTypeDeclaration node, ASTMethodDeclaration hashCodeMethod, ASTMethodDeclaration equalsMethod) {
        ASTMethodDeclaration compareToMethod = node
                .getDeclarations(ASTMethodDeclaration.class)
                .first(JavaAstUtils::isCompareToMethod);
        if (compareToMethod == null) {
            return;
        }

        if (equalsMethod == null && hashCodeMethod == null) {
            if (!node.isRecord() || hasBrokenEqualsMethod(node)) {
                ctx.addViolationWithMessage(compareToMethod, MISSING_EQUALS_AND_HASH_CODE);
            }
        } else if (equalsMethod == null) {
            ctx.addViolationWithMessage(hashCodeMethod, MISSING_EQUALS);
        } else if (hashCodeMethod == null) {
            ctx.addViolationWithMessage(equalsMethod, MISSING_HASH_CODE);
        }
    }
}
