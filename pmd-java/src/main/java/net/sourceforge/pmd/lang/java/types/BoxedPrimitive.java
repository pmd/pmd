/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import org.pcollections.PSet;

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
    BoxedPrimitive(TypeSystem factory, JClassSymbol boxType, JPrimitiveType unboxed, PSet<SymAnnot> typeAnnots) {
        super(factory, boxType, typeAnnots);
        this.unboxed = unboxed;
    }

    @Override
    public JClassType withAnnotations(PSet<SymAnnot> newTypeAnnots) {
        if (newTypeAnnots.isEmpty() && this.getTypeAnnotations().isEmpty()) {
            return this;
        }
        return new BoxedPrimitive(
            getTypeSystem(),
            this.getSymbol(),
            this.unboxed,
            newTypeAnnots
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
