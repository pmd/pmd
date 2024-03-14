/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.ast;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.java.ast.InvocationNode;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.ast.internal.InvocCtx;
import net.sourceforge.pmd.lang.java.types.ast.internal.RegularCtx;
import net.sourceforge.pmd.util.AssertionUtil;

/**
 * Internal API.
 *
 * <p>Acts as a bridge between outer parts of PMD and the restricted access
 * internal API of this package.
 *
 * <p><b>None of this is published API, and compatibility can be broken anytime!</b>
 * Use this only at your own risk.
 *
 * @apiNote Internal API
 */
@InternalApi
public final class InternalApiBridge {
    private InternalApiBridge() {}

    public static boolean canGiveContextToPoly(ExprContext exprContext, boolean lambdaOrMethodRef) {
        return exprContext.canGiveContextToPoly(lambdaOrMethodRef);
    }

    public static ExprContext newOtherContext(@NonNull JTypeMirror targetType, ExprContext.ExprContextKind kind) {
        AssertionUtil.requireParamNotNull("target type", targetType);
        return new RegularCtx(targetType, kind);
    }

    public static ExprContext newInvocContext(InvocationNode invocNode, int argumentIndex) {
        return new InvocCtx(argumentIndex, invocNode);
    }
}
