/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl.reflect;

import java.lang.reflect.Constructor;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol;


class ReflectedCtorImpl extends AbstractReflectedExecutableSymbol<Constructor<?>> implements JConstructorSymbol {

    ReflectedCtorImpl(@NonNull ReflectedClassImpl owner, Constructor<?> myConstructor) {
        super(owner, myConstructor);
    }

}
