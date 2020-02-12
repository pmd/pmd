/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl.reflect;

import java.lang.reflect.Method;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.SymbolEquality;


class ReflectedMethodImpl extends AbstractReflectedExecutableSymbol<Method> implements JMethodSymbol {

    ReflectedMethodImpl(@NonNull ReflectedClassImpl owner, Method method) {
        super(owner, method);
    }


    @Override
    public String getSimpleName() {
        return reflected.getName();
    }


    @Override
    public boolean equals(Object o) {
        return SymbolEquality.METHOD.equals(this, o);
    }

    @Override
    public int hashCode() {
        return SymbolEquality.METHOD.hash(this);
    }

}
