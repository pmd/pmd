/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.rule.xpath.NoAttribute;

/**
 * An {@link AccessNode} that is not a local declaration, and can receive
 * a visibility modifier. Has a couple more convenient methods.
 */
interface NonLocalDeclarationNode extends AccessNode {


    /**
     * Returns true if this declaration has a static modifier, implicitly or explicitly.
     */
    default boolean isStatic() {
        return hasModifiers(JModifier.STATIC);
    }


    // these are about visibility


    /** Returns true if this node has private visibility. */
    @NoAttribute
    default boolean isPrivate() {
        return getVisibility() == Visibility.V_PRIVATE;
    }


    /** Returns true if this node has public visibility. */
    @NoAttribute
    default boolean isPublic() {
        return getVisibility() == Visibility.V_PUBLIC;
    }


    /** Returns true if this node has protected visibility. */
    @NoAttribute
    default boolean isProtected() {
        return getVisibility() == Visibility.V_PROTECTED;
    }


    /** Returns true if this node has package visibility. */
    @NoAttribute
    default boolean isPackagePrivate() {
        return getVisibility() == Visibility.V_PACKAGE;
    }
}
