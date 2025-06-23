/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Objects;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

abstract class AbstractJavaExpr extends AbstractJavaTypeNode implements ASTExpression {

    private int parenDepth;
    private @Nullable ConstResult constValue;

    AbstractJavaExpr(int i) {
        super(i);
    }


    void bumpParenDepth() {
        parenDepth++;
    }

    @Override
    public int getParenthesisDepth() {
        return parenDepth;
    }

    @Override
    public @NonNull ConstResult getConstFoldingResult() {
        if (constValue == null) {
            constValue = ConstResult.NO_CONST_VALUE; // make non-null, so that we don't reenter on cycle
            constValue = doBuildConstValue();
            Objects.requireNonNull(constValue, "constValue must not be null");
        }
        return constValue;
    }

    final @NonNull ConstResult doBuildConstValue() {
        return acceptVisitor(ConstantFolder.INSTANCE, null);
    }

    /**
     * @deprecated Since 7.12.0. Kept for binary compatibility. This method should have been package-private from the start.
     *     Use {@link ASTExpression#getConstValue()} or {@link ASTExpression#getConstFoldingResult()} instead.
     */
    @Deprecated
    protected @Nullable Object buildConstValue() {
        return doBuildConstValue().getValue();
    }
}
