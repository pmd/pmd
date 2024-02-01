/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.ast.internal;

import static net.sourceforge.pmd.lang.java.types.ast.ExprContext.ExprContextKind.INVOCATION;
import static net.sourceforge.pmd.lang.java.types.ast.ExprContext.ExprContextKind.MISSING;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.ast.ExprContext;

public final class RegularCtx extends ExprContext {

    public static final RegularCtx NO_CTX = new RegularCtx(null, MISSING);

    final @Nullable JTypeMirror targetType;

    public RegularCtx(@Nullable JTypeMirror targetType, ExprContextKind kind) {
        super(kind);
        assert kind != INVOCATION;
        this.targetType = targetType;
    }

    @Override
    public @Nullable JTypeMirror getTargetType() {
        return targetType;
    }

    @Override
    public String toString() {
        return "RegularCtx{kind=" + kind + ", targetType=" + targetType + '}';
    }
}
