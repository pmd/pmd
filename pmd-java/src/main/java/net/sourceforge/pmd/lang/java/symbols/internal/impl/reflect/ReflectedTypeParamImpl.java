/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl.reflect;

import java.lang.reflect.TypeVariable;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.symbols.JTypeParameterOwnerSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeParameterSymbol;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.SymbolEquality;

@SuppressWarnings("PMD")
// yeah this looks weird for now
// but it will fall into place when we introduce type mirrors
class ReflectedTypeParamImpl implements JTypeParameterSymbol {

    private final ReflectionSymFactory factory;
    private final JTypeParameterOwnerSymbol ownerSymbol;
    private final String name;
    private final TypeVariable<?> reflected;

    ReflectedTypeParamImpl(ReflectionSymFactory factory, JTypeParameterOwnerSymbol ownerSymbol, TypeVariable<?> tvar) {
        this.factory = factory;
        this.ownerSymbol = ownerSymbol;
        this.name = tvar.getName();
        this.reflected = tvar;

    }

    @Override
    public @Nullable Class<?> getJvmRepr() {
        return Object.class; // TODO upper bound, when we have implemented types
    }

    @Override
    public JTypeParameterOwnerSymbol getDeclaringSymbol() {
        return ownerSymbol;
    }

    @NonNull
    @Override
    public String getSimpleName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        return SymbolEquality.TYPE_PARAM.equals(this, o);
    }

    @Override
    public int hashCode() {
        return SymbolEquality.TYPE_PARAM.hash(this);
    }
}
