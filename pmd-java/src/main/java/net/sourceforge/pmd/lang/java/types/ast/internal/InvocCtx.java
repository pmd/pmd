/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.ast.internal;

import static net.sourceforge.pmd.lang.java.types.ast.ExprContext.ExprContextKind.INVOCATION;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.InvocationNode;
import net.sourceforge.pmd.lang.java.symbols.JExecutableSymbol;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.OverloadSelectionResult;
import net.sourceforge.pmd.lang.java.types.ast.ExprContext;

public final class InvocCtx extends ExprContext {

    private final int arg;
    private final InvocationNode node;

    public InvocCtx(int arg, InvocationNode node) {
        super(INVOCATION);
        this.arg = arg;
        this.node = node;
    }

    @Override
    public @Nullable JTypeMirror getTargetType() {
        // this triggers type resolution of the enclosing expr.
        OverloadSelectionResult overload = node.getOverloadSelectionInfo();
        if (overload.isFailed()) {
            return null;
        }
        return overload.ithFormalParam(arg);
    }

    @Nullable JTypeMirror getTargetTypeOfSymbol() {
        OverloadSelectionResult overload = node.getOverloadSelectionInfo();
        if (overload.isFailed()) {
            return null;
        }
        JExecutableSymbol symbol = overload.getMethodType().getSymbol();
        JMethodSig genericMethod = symbol.getGenericSignature();
        return genericMethod.ithFormalParam(arg, overload.isVarargsCall());
    }

    @Override
    public boolean isMissing() {
        return false;
    }

    public InvocationNode getNode() {
        return node;
    }

    @Override
    public String toString() {
        return "InvocCtx{arg=" + arg + ", node=" + node + '}';
    }
}
