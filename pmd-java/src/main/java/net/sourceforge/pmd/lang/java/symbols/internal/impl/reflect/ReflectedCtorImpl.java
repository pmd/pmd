/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl.reflect;

import java.lang.reflect.Constructor;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.SymbolEquality;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.SymbolToStrings;


class ReflectedCtorImpl extends AbstractReflectedExecutableSymbol<Constructor<?>> implements JConstructorSymbol {

    ReflectedCtorImpl(@NonNull ReflectedClassImpl owner, Constructor<?> myConstructor) {
        super(owner, myConstructor);
    }

    @Override
    public boolean equals(Object obj) {
        return SymbolEquality.CONSTRUCTOR.equals(this, obj);
    }

    @Override
    public int hashCode() {
        return SymbolEquality.CONSTRUCTOR.hash(this);
    }


    @Override
    public String toString() {
        return SymbolToStrings.REFLECT.ctorToString(this);
    }

}
