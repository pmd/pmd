/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.TypeSystem;

/**
 * Instances of this class are nodes that have a compile-time type. This
 * is an upgrade of {@link TypeNode} (will be merged later into TypeNode).
 */
public interface TypedNode extends JavaNode {


    /**
     * Returns the compile-time type of this node. For example, for a
     * string literal, returns the type mirror for {@link String}, for
     * a method call, returns the return type of the call, etc.
     *
     * <p>This method ignores conversions applied to the value of the
     * node because of its context. For example, in {@code 1 + ""}, the
     * numeric literal will have type {@code int}, but it is converted
     * to {@code String} by the surrounding concatenation expression.
     * Similarly, in {@code Collections.singletonList(1)}, the {@link ASTNumericLiteral}
     * node has type {@code int}, but the type of the method formal is
     * {@link Integer}, and boxing is applied at runtime. Possibly, an
     * API will be added to expose this information.
     *
     * @return The type mirror. Never returns null; if the type is unresolved, returns
     *     {@link TypeSystem#UNRESOLVED_TYPE}.
     */
    @NonNull
    JTypeMirror getTypeMirror();

}
