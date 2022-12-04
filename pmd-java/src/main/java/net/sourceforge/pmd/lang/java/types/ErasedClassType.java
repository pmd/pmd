/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import java.util.Collections;

import org.pcollections.PSet;

import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymAnnot;

final class ErasedClassType extends ClassTypeImpl {

    ErasedClassType(TypeSystem typeSystem, JClassSymbol symbol, PSet<SymAnnot> typeAnnots) {
        super(typeSystem, symbol, Collections.emptyList(), true, typeAnnots);
    }

    @Override
    public boolean hasErasedSuperTypes() {
        return true;
    }

    @Override
    public JClassType getErasure() {
        return this;
    }
}
