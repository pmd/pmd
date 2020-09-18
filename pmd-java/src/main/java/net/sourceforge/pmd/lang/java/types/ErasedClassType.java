/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import java.util.Collections;

import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;

final class ErasedClassType extends ClassTypeImpl {

    ErasedClassType(TypeSystem typeSystem, JClassSymbol symbol) {
        super(typeSystem, symbol, Collections.emptyList(), false);
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
