/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl.reflect;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.SymbolFactory;

/**
 * Bridge into the internal API of this package.
 */
public final class ReflectSymInternals {

    public static final ReflectionSymFactory STATIC_FACTORY = new ReflectionSymFactory();
    // object
    public static final JClassSymbol OBJECT_SYM = createSharedSym(Object.class);
    // primitives
    public static final JClassSymbol BOOLEAN_SYM = createSharedSym(boolean.class);
    public static final JClassSymbol BYTE_SYM = createSharedSym(byte.class);
    public static final JClassSymbol CHAR_SYM = createSharedSym(char.class);
    public static final JClassSymbol DOUBLE_SYM = createSharedSym(double.class);
    public static final JClassSymbol FLOAT_SYM = createSharedSym(float.class);
    public static final JClassSymbol INT_SYM = createSharedSym(int.class);
    public static final JClassSymbol LONG_SYM = createSharedSym(long.class);
    public static final JClassSymbol SHORT_SYM = createSharedSym(short.class);
    public static final JClassSymbol VOID_SYM = createSharedSym(void.class);
    // primitive wrappers
    public static final JClassSymbol BOXED_BOOLEAN_SYM = createSharedSym(Boolean.class);
    public static final JClassSymbol BOXED_BYTE_SYM = createSharedSym(Byte.class);
    public static final JClassSymbol BOXED_CHAR_SYM = createSharedSym(Character.class);
    public static final JClassSymbol BOXED_DOUBLE_SYM = createSharedSym(Double.class);
    public static final JClassSymbol BOXED_FLOAT_SYM = createSharedSym(Float.class);
    public static final JClassSymbol BOXED_INT_SYM = createSharedSym(Integer.class);
    public static final JClassSymbol BOXED_LONG_SYM = createSharedSym(Long.class);
    public static final JClassSymbol BOXED_SHORT_SYM = createSharedSym(Short.class);
    public static final JClassSymbol BOXED_VOID_SYM = createSharedSym(Void.class);
    // array supertypes
    public static final JClassSymbol CLONEABLE_SYM = createSharedSym(Cloneable.class);
    public static final JClassSymbol SERIALIZABLE_SYM = createSharedSym(Serializable.class);
    public static final List<JClassSymbol> ARRAY_SUPER_INTERFACES = Collections.unmodifiableList(Arrays.asList(CLONEABLE_SYM, SERIALIZABLE_SYM));
    // other important/common types
    public static final JClassSymbol CLASS_SYM = createSharedSym(Class.class);
    public static final JClassSymbol ITERABLE_SYM = createSharedSym(Iterable.class);
    public static final JClassSymbol ENUM_SYM = createSharedSym(Enum.class);
    public static final JClassSymbol STRING_SYM = createSharedSym(String.class);

    private ReflectSymInternals() {
        // util class
    }

    /**
     * {@link SymbolFactory} cannot use {@link ReflectionSymFactory}
     * directly, because of class init cycle.
     */
    public static JClassSymbol createSharedSym(Class<?> klass) {
        return ReflectedClassImpl.createOuterClass(STATIC_FACTORY, klass);
    }

}
