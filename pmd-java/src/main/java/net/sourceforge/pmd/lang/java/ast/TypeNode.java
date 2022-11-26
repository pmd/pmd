/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.annotation.DeprecatedUntil700;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.TypeSystem;
import net.sourceforge.pmd.lang.java.types.TypingContext;

/**
 * A node that has a statically known type. This includes e.g.
 * {@linkplain ASTType type}s, which are explicitly written types,
 * and {@linkplain ASTExpression expressions}, whose types is determined
 * from their form, or through type inference.
 */
public interface TypeNode extends JavaNode {

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
     *     {@link TypeSystem#UNKNOWN}.
     */
    @NonNull
    default JTypeMirror getTypeMirror() {
        return getTypeMirror(TypingContext.DEFAULT);
    }

    JTypeMirror getTypeMirror(TypingContext typing);


    /**
     * Get the Java Class associated with this node.
     *
     * @return The Java Class, may return <code>null</code>.
     *
     * @deprecated This doesn't work. PMD doesn't load classes, it just
     *         reads the bytecode. Compare the symbol of the {@link #getTypeMirror() type mirror}
     *         instead.
     */
    @Nullable
    @Deprecated
    @DeprecatedUntil700
    default Class<?> getType() {
        return null;
    }


}
