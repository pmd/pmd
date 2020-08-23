/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.rule.xpath.NoAttribute;

/**
 * Package private, though the method is public.
 */
interface FinalizableNode extends AccessNode {


    /**
     * Returns true if this variable, method or class is final (even implicitly).
     */
    @Override
    // because the interface is package-private, reflection may fail
    // to invoke this attribute. Either make the interface public, or
    // keep this @NoAttribute
    @NoAttribute
    default boolean isFinal() {
        return hasModifiers(JModifier.FINAL);
    }

}
