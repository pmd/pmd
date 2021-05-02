/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.FunctionalExpression;
import net.sourceforge.pmd.lang.java.ast.InternalApiBridge;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.FunctionalExprMirror;

/**
 *
 */
abstract class BaseFunctionalMirror<N extends FunctionalExpression> extends BasePolyMirror<N> implements FunctionalExprMirror {
    private JMethodSig inferredMethod;

    BaseFunctionalMirror(JavaExprMirrors mirrors, N myNode, @Nullable ExprMirror parent) {
        super(mirrors, myNode, parent);
    }

    @Override
    public void setFunctionalMethod(JMethodSig methodType) {
        this.inferredMethod = methodType;
        if (mayMutateAst()) {
            InternalApiBridge.setFunctionalMethod(myNode, methodType);
        }
    }

    protected JMethodSig getInferredMethod() {
        return inferredMethod;
    }
}
