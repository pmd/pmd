/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror;

class StandaloneExprMirror extends BaseExprMirror<ASTExpression> implements ExprMirror {

    StandaloneExprMirror(JavaExprMirrors factory, ASTExpression myNode) {
        super(factory, myNode);
    }

    @Override
    public @Nullable JTypeMirror getStandaloneType() {
        return myNode.getTypeMirror();
    }

    @Override
    public void setInferredType(JTypeMirror mirror) {
        // do nothing
    }
}
