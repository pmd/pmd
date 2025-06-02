/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;

/**
 * A method reference or lambda expression.
 */
public interface FunctionalExpression extends ASTExpression {


    /**
     * Returns the type of the functional interface.
     * E.g. in {@code stringStream.map(s -> s.isEmpty())}, this is
     * {@code java.util.function.Function<java.lang.String, java.lang.Boolean>}.
     *
     * @see #getFunctionalMethod()
     */
    @Override
    @NonNull JTypeMirror getTypeMirror();

    /**
     * Returns the method that is overridden in the functional interface.
     * E.g. in {@code stringStream.map(s -> s.isEmpty())}, this is
     * {@code java.util.function.Function#apply(java.lang.String) ->
     * java.lang.Boolean}
     *
     * @see #getTypeMirror()
     */
    JMethodSig getFunctionalMethod();

}
