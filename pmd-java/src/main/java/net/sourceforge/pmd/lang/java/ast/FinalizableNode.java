/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * A node that may have the final modifier.
 */
public interface FinalizableNode extends AccessNode {


    /**
     * Returns true if this variable, method or class is final (even implicitly).
     */
    @Override
    default boolean isFinal() {
        return hasModifiers(JModifier.FINAL);
    }

}
