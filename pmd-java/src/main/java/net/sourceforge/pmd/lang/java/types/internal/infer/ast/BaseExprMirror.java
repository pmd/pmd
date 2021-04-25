/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer.ast;

import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror;

abstract class BaseExprMirror<T extends JavaNode> implements ExprMirror {

    final JavaExprMirrors factory;
    final T myNode;

    BaseExprMirror(JavaExprMirrors factory, T myNode) {
        this.factory = factory;
        this.myNode = myNode;
    }

    @Override
    public JavaNode getLocation() {
        return myNode;
    }

    @Override
    public String toString() {
        return "Mirror of: " + myNode;
    }

    protected boolean mayMutateAst() {
        return this.factory.mayMutateAst();
    }
}
