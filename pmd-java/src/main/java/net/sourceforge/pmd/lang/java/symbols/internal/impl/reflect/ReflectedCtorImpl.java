/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl.reflect;

import java.lang.reflect.Constructor;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.SymbolEquality;


class ReflectedCtorImpl extends AbstractReflectedExecutableSymbol<Constructor<?>> implements JConstructorSymbol {

    ReflectedCtorImpl(@NonNull ReflectedClassImpl owner, Constructor<?> myConstructor) {
        super(owner, myConstructor);
    }

    @Nullable
    @Override
    public ASTConstructorDeclaration getDeclaration() {
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        return SymbolEquality.CONSTRUCTOR.equals(this, obj);
    }

    @Override
    public int hashCode() {
        return SymbolEquality.CONSTRUCTOR.hash(this);
    }
}
