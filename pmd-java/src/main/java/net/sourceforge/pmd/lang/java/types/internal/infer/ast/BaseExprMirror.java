/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer.ast;

import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.TypingContext;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror;

abstract class BaseExprMirror<T extends JavaNode> implements ExprMirror {

    final JavaExprMirrors factory;
    final T myNode;
    private TypingContext typingContext;

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

    protected TypingContext getTypingContext() {
        return typingContext == null ? TypingContext.EMPTY
                                     : typingContext;
    }

    public void setTypingContext(TypingContext typingCtx) {
        this.typingContext = typingCtx;
    }

    /**
     * TODO get the type mirror like LazyTypeResolver does, but with a
     * contextual mapping of symbol -> type. Lambda parameters may have
     * a different type in this mirror hierarchy as they have in the AST.
     */
    protected JTypeMirror typeOf(ASTExpression e) {
        return e.getTypeMirror();
    }
}
