/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl.reflect;

import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;

import net.sourceforge.pmd.lang.java.symbols.JExecutableSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFormalParamSymbol;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.SymbolEquality;

final class ReflectedMethodParamImpl extends AbstractReflectedSymbol implements JFormalParamSymbol {

    private final AbstractReflectedExecutableSymbol<?> owner;
    private final Parameter reflected;

    /** Constructor for a reflected method or constructor parameter. */
    public ReflectedMethodParamImpl(AbstractReflectedExecutableSymbol<?> owner, Parameter reflected) {
        super(owner.symFactory);
        this.owner = owner;
        this.reflected = reflected;
    }

    @Override
    public JExecutableSymbol getDeclaringSymbol() {
        return owner;
    }

    @Override
    public boolean isFinal() {
        return Modifier.isFinal(reflected.getModifiers());
    }

    @Override
    public String getSimpleName() {
        return reflected.getName();
    }

    @Override
    public boolean equals(Object o) {
        return SymbolEquality.FORMAL_PARAM.equals(this, o);
    }

    @Override
    public int hashCode() {
        return SymbolEquality.FORMAL_PARAM.hash(this);
    }
}
