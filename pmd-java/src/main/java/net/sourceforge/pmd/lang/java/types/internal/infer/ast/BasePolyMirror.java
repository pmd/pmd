/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer.ast;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.InternalApiBridge;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.PolyExprMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.ast.JavaExprMirrors.MirrorMaker;

abstract class BasePolyMirror<T extends JavaNode> extends BaseExprMirror<T> implements PolyExprMirror {

    private final MirrorMaker subexprMaker;
    private JTypeMirror inferredType;

    BasePolyMirror(JavaExprMirrors mirrors, T myNode, @Nullable ExprMirror parent, MirrorMaker subexprMaker) {
        super(mirrors, myNode, parent);
        this.subexprMaker = subexprMaker;
    }

    protected ExprMirror createSubexpression(ASTExpression subexpr) {
        return subexprMaker.createMirrorForSubexpression(subexpr, this, subexprMaker);
    }

    @Override
    public void setInferredType(JTypeMirror mirror) {
        this.inferredType = mirror;
        if (myNode instanceof TypeNode && mayMutateAst()) {
            InternalApiBridge.setTypeMirrorInternal((TypeNode) myNode, mirror);
        }
    }

    @Override
    public JTypeMirror getInferredType() {
        return inferredType;
    }

    @Override
    public @NonNull JClassType getEnclosingType() {
        return myNode.getEnclosingType().getTypeMirror();
    }


}
