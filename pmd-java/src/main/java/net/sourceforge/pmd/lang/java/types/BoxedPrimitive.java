/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import java.util.Collections;

import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;

/**
 * Special primitive wrappers, these are the only ones for which
 * {@link #unbox()} is not the identity conversion.
 *
 * <p>Order of initialization is tricky since there is a circular
 * dependency between JPrimitiveType constants and their PrimitiveWrapper.
 * The current solution is to leak the 'this' instance in the enum
 * constructor.
 */
final class BoxedPrimitive extends ClassTypeImpl {

    private final JPrimitiveType unboxed;

    // constructor called by JPrimitiveType, exactly once per type system and per primitive
    BoxedPrimitive(TypeSystem factory, JClassSymbol boxType, JPrimitiveType unboxed) {
        super(factory, boxType, Collections.emptyList(), true); // not erased
        this.unboxed = unboxed;
    }

    @Override
    public JTypeMirror unbox() {
        return unboxed;
    }

    @Override
    public JClassType getErasure() {
        return this;
    }
}
