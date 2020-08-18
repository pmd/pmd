/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.OverloadSelectionResult;

/**
 * Groups {@linkplain ASTMethodCall method} and {@linkplain ASTConstructorCall constructor call},
 * together, as well as {@linkplain ASTExplicitConstructorInvocation explicit constructor invocation statements},
 * and {@linkplain ASTEnumConstant enum constant declarations}.
 * Those last two are included, because they are special syntax
 * to call a constructor.
 *
 * <p>The arguments of the invocation are said to be in an "invocation context",
 * which influences what conversions they are subject to. It also
 * means the type of the arguments may depend on the resolution
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
     * Returns the explicit type arguments if they exist.
     */
    @Nullable
    ASTTypeArguments getExplicitTypeArguments();


    /**
     * Gets the type of the method or constructor that is called by
     * this node. See {@link OverloadSelectionResult#getMethodType()}.
     */
    default JMethodSig getMethodType() {
        return getOverloadSelectionInfo().getMethodType();
    }


    /**
     * Returns true if this is a varargs call. This means, that the
     * called method is varargs, and was overload-selected in the varargs
     * phase. For example:
     * <pre>{@code
     * Arrays.asList("a", "b");                     // this is a varargs call
     * Arrays.asList(new String[] { "a", "b" });    // this is not a varargs call
     * }</pre>
     */
    default boolean isVarargsCall() {
        return getOverloadSelectionInfo().isVarargsCall();
    }

    OverloadSelectionResult getOverloadSelectionInfo();

    /**
     * Returns the name of the called method. If this is a constructor
     * call, returns {@link JConstructorSymbol#CTOR_NAME}.
     */
    default @NonNull String getMethodName() {
        return JConstructorSymbol.CTOR_NAME;
    }

}
