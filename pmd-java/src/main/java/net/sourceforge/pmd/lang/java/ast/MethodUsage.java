/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol;

/**
 * A node that uses another method or constructor. Those are
 * {@link InvocationNode#getMethodType() InvocationNode}s and
 * {@link ASTMethodReference#getReferencedMethod() MethodReference}.
 *
 * TODO should these method be named the same, and added to this interface?
 */
public interface MethodUsage extends JavaNode {

    /**
     * Returns the name of the called method. If this is a constructor
     * call, returns {@link JConstructorSymbol#CTOR_NAME}.
     */
    default @NonNull String getMethodName() {
        return JConstructorSymbol.CTOR_NAME;
    }
}
