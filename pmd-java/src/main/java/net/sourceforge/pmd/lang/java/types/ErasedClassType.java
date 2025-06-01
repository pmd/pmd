/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import java.util.Collections;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymAnnot;
import org.pcollections.PSet;

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
