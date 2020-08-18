/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer.ast;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.ast.InternalApiBridge;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.TypeSystem;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.PolyExprMirror;

abstract class BasePolyMirror<T extends JavaNode> extends BaseExprMirror<T> implements PolyExprMirror {

    BasePolyMirror(JavaExprMirrors mirrors, T myNode) {
        super(mirrors, myNode);
    }

    @Override
    public void setInferredType(JTypeMirror mirror) {
        if (myNode instanceof TypeNode) {
            InternalApiBridge.setTypeMirrorInternal((TypeNode) myNode, mirror);
        }
    }


    @Override
    public @NonNull JClassType getEnclosingType() {
        return myNode.getEnclosingType().getTypeMirror();
    }


}
