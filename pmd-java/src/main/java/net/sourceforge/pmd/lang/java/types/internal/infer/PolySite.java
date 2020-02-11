/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.PolyExprMirror;

/**
 * Context of a poly expression. Includes info about an expected target
 * type, and the expression mirror.
 */
public class PolySite {

    private final JTypeMirror expectedType;
    private final PolyExprMirror expr;


    PolySite(PolyExprMirror expr, @Nullable JTypeMirror expectedType) {
        this.expectedType = expectedType;
        this.expr = expr;
    }

    @Nullable
    JTypeMirror getExpectedType() {
        return expectedType;
    }

    PolyExprMirror getExpr() {
        return expr;
    }

    @Override
    public String toString() {
        return "PolySite:" + expr;
    }


}
