/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl;


import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.reflect.ReflectSymInternals;
import net.sourceforge.pmd.lang.java.types.internal.impl.JTypeFactory;

/**
 * Builds symbols.
 *
 * <p>This may be improved later to eg cache and reuse the most recently
 * accessed symbols (there may be a lot of cache hits in a typical java file).
 */
public interface SymbolFactory<T> {


    // object
    JClassSymbol OBJECT_SYM = ReflectSymInternals.createSharedSym(Object.class);
    // unresolved symbol
    JClassSymbol UNRESOLVED_CLASS_SYM = new UnresolvedClassImpl("/*unresolved*/");

    // primitives
    JClassSymbol BOOLEAN_SYM = ReflectSymInternals.createSharedSym(boolean.class);
    JClassSymbol BYTE_SYM = ReflectSymInternals.createSharedSym(byte.class);
    JClassSymbol CHAR_SYM = ReflectSymInternals.createSharedSym(char.class);
    JClassSymbol DOUBLE_SYM = ReflectSymInternals.createSharedSym(double.class);
    JClassSymbol FLOAT_SYM = ReflectSymInternals.createSharedSym(float.class);
    JClassSymbol INT_SYM = ReflectSymInternals.createSharedSym(int.class);
    JClassSymbol LONG_SYM = ReflectSymInternals.createSharedSym(long.class);
    JClassSymbol SHORT_SYM = ReflectSymInternals.createSharedSym(short.class);
    JClassSymbol VOID_SYM = ReflectSymInternals.createSharedSym(void.class);

    // primitive wrappers
    JClassSymbol BOXED_BOOLEAN_SYM = ReflectSymInternals.createSharedSym(Boolean.class);
    JClassSymbol BOXED_BYTE_SYM = ReflectSymInternals.createSharedSym(Byte.class);
    JClassSymbol BOXED_CHAR_SYM = ReflectSymInternals.createSharedSym(Character.class);
    JClassSymbol BOXED_DOUBLE_SYM = ReflectSymInternals.createSharedSym(Double.class);
    JClassSymbol BOXED_FLOAT_SYM = ReflectSymInternals.createSharedSym(Float.class);
    JClassSymbol BOXED_INT_SYM = ReflectSymInternals.createSharedSym(Integer.class);
    JClassSymbol BOXED_LONG_SYM = ReflectSymInternals.createSharedSym(Long.class);
    JClassSymbol BOXED_SHORT_SYM = ReflectSymInternals.createSharedSym(Short.class);
    JClassSymbol BOXED_VOID_SYM = ReflectSymInternals.createSharedSym(Void.class);

    // array supertypes
    JClassSymbol CLONEABLE_SYM = ReflectSymInternals.createSharedSym(Cloneable.class);
    JClassSymbol SERIALIZABLE_SYM = ReflectSymInternals.createSharedSym(Serializable.class);
    List<JClassSymbol> ARRAY_SUPER_INTERFACES = Collections.unmodifiableList(Arrays.asList(CLONEABLE_SYM, SERIALIZABLE_SYM));

    // other important/common types
    JClassSymbol CLASS_SYM = ReflectSymInternals.createSharedSym(Class.class);
    JClassSymbol ITERABLE_SYM = ReflectSymInternals.createSharedSym(Iterable.class);
    JClassSymbol ENUM_SYM = ReflectSymInternals.createSharedSym(Enum.class);
    JClassSymbol STRING_SYM = ReflectSymInternals.createSharedSym(String.class);


    JTypeFactory types();


    default JTypeDeclSymbol fakeSymbol(String name) {
        return new FakeTypeSymbol(name);
    }


    @NonNull
    default JClassSymbol makeUnresolvedReference(String canonicalName) {
        return new UnresolvedClassImpl(canonicalName);
    }


    @NonNull
    default JClassSymbol makeArraySymbol(JTypeDeclSymbol component) {
        return new ArraySymbolImpl(this, component);
    }


    /**
     * Returns the symbol representing the given class. Returns null if
     * the given class is itself null.
     */
    JClassSymbol getClassSymbol(@Nullable T klass);


}
