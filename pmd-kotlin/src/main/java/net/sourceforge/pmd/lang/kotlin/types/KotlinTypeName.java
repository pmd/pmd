/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.types;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.annotation.Experimental;

/**
 * Represents a resolved Kotlin type name with metadata. This is the PMD-owned
 * type abstraction stored on Kotlin AST nodes — it does not expose any
 * third-party library types, keeping the type-mapper implementation replaceable.
 *
 * <p>Instances are immutable and created during the pre-analysis pass.
 *
 * @since 7.27.0
 * @experimental
 */
@Experimental
public final class KotlinTypeName {

    private final String fqName;
    private final boolean nullable;
    private final boolean unresolved;
    private final String displayString;

    /**
     * Creates a new KotlinTypeName.
     *
     * @param fqName        raw fully-qualified name (no generics, no nullable marker)
     * @param nullable      whether the type is marked nullable ({@code ?})
     * @param unresolved    whether the type could not be resolved (missing classpath)
     * @param displayString full representation including generics and nullable marker,
     *                      used for the {@code @TypeName} XPath attribute
     */
    public KotlinTypeName(@NonNull String fqName, boolean nullable, boolean unresolved,
                          @NonNull String displayString) {
        this.fqName = fqName;
        this.nullable = nullable;
        this.unresolved = unresolved;
        this.displayString = displayString;
    }

    /**
     * Convenience factory for simple FQN-only types (not nullable, not unresolved,
     * no generic arguments). Used for annotation and delegation specifier type names.
     */
    public static KotlinTypeName ofFqName(@NonNull String fqName) {
        return new KotlinTypeName(fqName, false, false, fqName);
    }

    /**
     * Null-safe helper returning the display string of a type, or {@code null} if the type is null.
     * Used by XPath attribute getters that must return String.
     */
    public static @Nullable String displayStringOf(@Nullable KotlinTypeName type) {
        return type != null ? type.displayString : null;
    }

    /**
     * Returns the raw fully-qualified name without generic type arguments or
     * nullable marker. Suitable for subtype and equivalence checks.
     * Example: {@code "kotlin.collections.List"} for type {@code List<String>?}.
     */
    public @NonNull String getFqName() {
        return fqName;
    }

    /**
     * Returns {@code true} when the type is nullable (marked with {@code ?} in source).
     */
    public boolean isNullable() {
        return nullable;
    }

    /**
     * Returns {@code true} when the compiler could not resolve this type,
     * typically due to missing classpath dependencies.
     */
    public boolean isUnresolved() {
        return unresolved;
    }

    /**
     * Returns the full type representation including generic arguments and nullable
     * marker, matching what the user wrote (e.g. {@code "kotlin.collections.List<kotlin.String>?"}).
     * This is the value exposed as the {@code @TypeName} / {@code @ReturnTypeName} XPath attribute.
     */
    public @NonNull String toDisplayString() {
        return displayString;
    }

    @Override
    public String toString() {
        return displayString;
    }
}
