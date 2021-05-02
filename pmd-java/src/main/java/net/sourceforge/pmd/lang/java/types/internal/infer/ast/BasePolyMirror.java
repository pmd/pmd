/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer.ast;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.InternalApiBridge;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.PolyExprMirror;

abstract class BasePolyMirror<T extends JavaNode> extends BaseExprMirror<T> implements PolyExprMirror {
    private JTypeMirror inferredType;

    BasePolyMirror(JavaExprMirrors mirrors, T myNode, @Nullable ExprMirror parent) {
        super(mirrors, myNode, parent);
    }

    @Override
    public void setInferredType(JTypeMirror mirror) {
        this.inferredType = mirror;
        if (myNode instanceof TypeNode && mayMutateAst()) {
            InternalApiBridge.setTypeMirrorInternal((TypeNode) myNode, mirror);
        }
    }

    /** Return the value set in the last call to {@link #setInferredType(JTypeMirror)}. */
    protected JTypeMirror getInferredType() {
        return inferredType;
    }

    @Override
    public @NonNull JClassType getEnclosingType() {
        return myNode.getEnclosingType().getTypeMirror();
    }


}
