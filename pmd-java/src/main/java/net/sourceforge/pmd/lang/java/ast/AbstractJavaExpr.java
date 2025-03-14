/*
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
            constValue = buildConstValue();
            Objects.requireNonNull(constValue, "constValue must not be null");
        }
        return constValue;
    }

    protected @NonNull ConstResult buildConstValue() {
        return acceptVisitor(ConstantFolder.INSTANCE, null);
    }
}
