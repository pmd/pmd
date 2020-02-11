/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Collections;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.internal.util.IteratorUtil;
import net.sourceforge.pmd.lang.java.types.JMethodSig;

/**
 * Groups {@linkplain ASTMethodCall method} and {@linkplain ASTConstructorCall constructor call},
 * together, as well as {@linkplain ASTExplicitConstructorInvocation explicit constructor invocation statements},
 * and {@linkplain ASTEnumConstant enum constant declarations}.
 * Those last two are included, because they are special syntax
 * to call a constructor.
 *
 * <p>This bestows an invocation context upon the arguments, so
 * that the type of the arguments may depend on the resolution
 * of the {@linkplain #getMethodType() compile-time declaration}
 * of this node.
 */
public interface InvocationNode extends TypeNode {

    /**
     * Returns the node representing the list of arguments
     * passed to the invocation. Can be null if this is an
     * {@link ASTEnumConstant}.
     */
    @Nullable
    ASTArgumentList getArguments();


    /**
     * Returns the list of arguments passed to the invocation.
     * This is never null and as such is safer than {@link #getArguments()}.
     */
    @NonNull
    default List<ASTExpression> getArgumentsList() {
        ASTArgumentList args = getArguments();
        return args == null ? Collections.emptyList() : IteratorUtil.toList(args.iterator());
    }


    /**
     * Returns the explicit type arguments if they exist.
     */
    @Nullable
    ASTTypeArguments getExplicitTypeArguments();


    /**
     * Returns the list of arguments passed to the invocation.
     * If there are no type arguments, returns an empty list.
     * This is never null and as such is safer than {@link #getArguments()}.
     */
    @NonNull
    default List<ASTType> getExplicitTypeArgumentList() {
        ASTTypeArguments args = getExplicitTypeArguments();
        return args == null ? Collections.emptyList() : IteratorUtil.toList(args.iterator());
    }


    /**
     * Gets the type of the method or constructor that is called by
     * this expression, statement or declaration. This is a method
     * type whose type parameters have been instantiated by their
     * actual inferred values.
     *
     * <p>For constructors the return type of this signature may be
     * different from the type of this node. For an anonymous class
     * constructor (in {@link ASTEnumConstant} or {@link ASTConstructorCall}),
     * the selected constructor is the *superclass* constructor. In
     * particular, if the anonymous class implements an interface,
     * the constructor is the constructor of class {@link Object}.
     * In that case though, the {@link #getTypeMirror()} of this node
     * will be the type of the anonymous class (hence the difference).
     */
    JMethodSig getMethodType();


    /**
     * Returns true if this is a varargs call. This means, that the
     * called method is varargs, and was not resolved in the varargs
     * phase. For example:
     * <pre>{@code
     * String[] arr = { "a", "b" };
     *
     * Arrays.asList("a", "b"); // this is a varargs call
     * Arrays.asList(arr);      // this is not a varargs call
     * }</pre>
     */
    boolean isVarargsCall();

}
