/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static net.sourceforge.pmd.lang.java.ast.JModifier.STRICTFP;

import java.util.Set;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.ast.NodeStream;

/**
 * A node that owns a {@linkplain ASTModifierList modifier list}.
 *
 * <p>{@link AccessNode} methods take into account the syntactic context of the
 * declaration, e.g. {@link #isPublic()} will always return true for a field
 * declared inside an interface, regardless of whether the {@code public}
 * modifier was specified explicitly or not. If you want to know whether
 * the modifier was explicitly stated, use {@link #hasExplicitModifiers(JModifier, JModifier...)}.
 *
 * TODO make modifiers accessible from XPath
 *   * Ideally we'd have two attributes, eg @EffectiveModifiers and @ExplicitModifiers,
 *     which would each return a sequence, eg ("public", "static", "final")
 *   * Ideally we'd have a way to add attributes that are not necessarily
 *     getters on the node. It makes no sense in the Java API to expose
 *     those getters on the node, it's more orthogonal to query getModifiers() directly.
 *
 * TODO rename to ModifierOwner, kept out from PR to reduce diff
 */
public interface AccessNode extends Annotatable {

    @Override
    default NodeStream<ASTAnnotation> getDeclaredAnnotations() {
        return getModifiers().children(ASTAnnotation.class);
    }


    /**
     * Returns the node representing the modifier list of this node.
     */
    default @NonNull ASTModifierList getModifiers() {
        return children(ASTModifierList.class).firstOrThrow();
    }


    /**
     * Returns the visibility corresponding to the {@link ASTModifierList#getEffectiveModifiers() effective modifiers}.
     * Eg a public method will have visibility {@link Visibility#V_PUBLIC public},
     * a local class will have visibility {@link Visibility#V_LOCAL local}.
     * There cannot be any conflict with {@link #hasModifiers(JModifier, JModifier...)}} on
     * well-formed code (e.g. for any {@code n}, {@code (n.getVisibility() == V_PROTECTED) ==
     * n.hasModifiers(PROTECTED)})
     *
     * <p>TODO a public method of a private class can be considered to be private
     * we could probably add another method later on that takes this into account
     */
    default Visibility getVisibility() {
        Set<JModifier> effective = getModifiers().getEffectiveModifiers();
        if (effective.contains(JModifier.PUBLIC)) {
            return Visibility.V_PUBLIC;
        } else if (effective.contains(JModifier.PROTECTED)) {
            return Visibility.V_PROTECTED;
        } else if (effective.contains(JModifier.PRIVATE)) {
            return Visibility.V_PRIVATE;
        } else {
            return Visibility.V_PACKAGE;
        }
    }

    /**
     * Returns the "effective" visibility of a member. This is the minimum
     * visibility of its enclosing type declarations. For example, a public
     * method of a private class is "effectively private".
     *
     * <p>Local declarations keep local visibility, eg a local variable
     * somewhere in an anonymous class doesn't get anonymous visibility.
     */
    default Visibility getEffectiveVisibility() {
        NodeStream<ASTAnyTypeDeclaration> ancestors = ancestors(ASTAnyTypeDeclaration.class);
        Visibility minv = getVisibility();
        if (minv == Visibility.V_LOCAL) {
            return minv;
        }
        for (ASTAnyTypeDeclaration enclosing : ancestors) {
            minv = Visibility.min(minv, enclosing.getVisibility());
            if (minv == Visibility.V_LOCAL) {
                return minv;
            }
        }
        return minv;
    }

    /**
     * Returns true if this node has <i>all</i> the given modifiers
     * either explicitly written or inferred through context.
     */
    default boolean hasModifiers(JModifier mod1, JModifier... mod) {
        return getModifiers().hasAll(mod1, mod);
    }


    /**
     * Returns true if this node has <i>all</i> the given modifiers
     * explicitly written in the source.
     */
    default boolean hasExplicitModifiers(JModifier mod1, JModifier... mod) {
        return getModifiers().hasAllExplicitly(mod1, mod);
    }


    // TODO remove all those, kept only for compatibility with rules

    // these are about effective modifiers


    @Deprecated
    default boolean isFinal() {
        return hasModifiers(JModifier.FINAL);
    }


    @Deprecated
    default boolean isAbstract() {
        return hasModifiers(JModifier.ABSTRACT);
    }


    @Deprecated
    default boolean isStrictfp() {
        return hasModifiers(STRICTFP);
    }


    @Deprecated
    default boolean isSynchronized() {
        return hasModifiers(JModifier.SYNCHRONIZED);
    }


    @Deprecated
    default boolean isNative() {
        return hasModifiers(JModifier.NATIVE);
    }


    @Deprecated
    default boolean isStatic() {
        return hasModifiers(JModifier.STATIC);
    }


    @Deprecated
    default boolean isVolatile() {
        return hasModifiers(JModifier.VOLATILE);
    }


    @Deprecated
    default boolean isTransient() {
        return hasModifiers(JModifier.TRANSIENT);
    }


    // these are about visibility


    @Deprecated
    default boolean isPrivate() {
        return getVisibility() == Visibility.V_PRIVATE;
    }


    @Deprecated
    default boolean isPublic() {
        return getVisibility() == Visibility.V_PUBLIC;
    }


    @Deprecated
    default boolean isProtected() {
        return getVisibility() == Visibility.V_PROTECTED;
    }


    @Deprecated
    default boolean isPackagePrivate() {
        return getVisibility() == Visibility.V_PACKAGE;
    }

    // these are about explicit modifiers


    @Deprecated
    default boolean isSyntacticallyAbstract() {
        return hasExplicitModifiers(JModifier.ABSTRACT);
    }


    @Deprecated
    default boolean isSyntacticallyPublic() {
        return hasExplicitModifiers(JModifier.PUBLIC);
    }


    @Deprecated
    default boolean isSyntacticallyStatic() {
        return hasExplicitModifiers(JModifier.STATIC);
    }


    @Deprecated
    default boolean isSyntacticallyFinal() {
        return hasExplicitModifiers(JModifier.FINAL);
    }


    /**
     * Represents the visibility of a declaration.
     *
     * <p>The ordering of the constants encodes a "contains" relationship,
     * ie, given two visibilities {@code v1} and {@code v2}, {@code v1 < v2}
     * means that {@code v2} is strictly more permissive than {@code v1}.
     */
    enum Visibility {
        // Note: constants are prefixed with "V_" to avoid conflicts with JModifier

        /** Special visibility of anonymous classes, even more restricted than local. */
        V_ANONYMOUS("anonymous"),
        /** Confined to a local scope, eg method parameters, local variables, local classes. */
        V_LOCAL("local"),
        /** File-private. Corresponds to modifier {@link JModifier#PRIVATE}. */
        V_PRIVATE("private"),
        /** Package-private. */
        V_PACKAGE("package"),
        /** Package-private + visible to subclasses. Corresponds to modifier {@link JModifier#PROTECTED}. */
        V_PROTECTED("protected"),
        /** Visible everywhere. Corresponds to modifier {@link JModifier#PUBLIC}. */
        V_PUBLIC("public");

        private final String myName;

        Visibility(String name) {
            this.myName = name;
        }

        @Override
        public String toString() {
            return myName;
        }

        /**
         * Returns true if this visibility is greater than or equal to
         * the parameter.
         */
        public boolean isAtLeast(Visibility other) {
            return this.compareTo(other) >= 0;
        }

        /**
         * Returns true if this visibility is strictly lower than the
         * parameter.
         */
        public boolean isAtMost(Visibility other) {
            return this.compareTo(other) < 0;
        }

        /**
         * The minimum of both visibilities.
         */
        static Visibility min(Visibility v1, Visibility v2) {
            return v1.compareTo(v2) <= 0 ? v1 : v2;
        }
    }
}
