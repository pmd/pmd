/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import static java.util.Collections.emptyList;

import java.util.List;

import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymAnnot;

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
    BoxedPrimitive(TypeSystem factory, JClassSymbol boxType, JPrimitiveType unboxed, List<SymAnnot> typeAnnots) {
        super(factory, boxType, emptyList(), true, typeAnnots); // not erased
        this.unboxed = unboxed;
    }

    @Override
    public JTypeMirror withAnnotations(List<SymAnnot> symAnnots) {
        if (symAnnots.equals(this.getTypeAnnotations())) {
            return this;
        }
        return new BoxedPrimitive(
            getTypeSystem(),
            this.getSymbol(),
            this.unboxed,
            symAnnots
        );
    }

    @Override
    public JTypeMirror unbox() {
        return unboxed.withAnnotations(this.getTypeAnnotations());
    }

    @Override
    public JClassType getErasure() {
        return this;
    }
}
