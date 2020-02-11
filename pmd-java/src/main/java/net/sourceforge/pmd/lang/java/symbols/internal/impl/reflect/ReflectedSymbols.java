/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl.reflect;


import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolResolver;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.SymbolFactory;
import net.sourceforge.pmd.util.CollectionUtil;

/**
 * Symbol factory building type symbols from {@link Class} instances.
 * Reflected symbol implementations carry an instance of this around,
 * so as to allow caching recently accessed symbols later on.
 */
public final class ReflectedSymbols {

    @Nullable
    public static JClassSymbol getClassSymbol(SymbolFactory f, @Nullable Class<?> klass) {
        if (klass == null) {
            return null;
        }
        if (klass.isArray()) {
            JClassSymbol component = getClassSymbol(f, klass.getComponentType());
            return f.makeArraySymbol(component);
        }
        return createReflected(f, klass);
    }

    private static ReflectedClassImpl createReflected(SymbolFactory factory, Class<?> klass) {
        if (klass.getEnclosingClass() != null) {
            ReflectedClassImpl enclosing = createReflected(factory, klass.getEnclosingClass());
            assert enclosing != null;
            return ReflectedClassImpl.createWithEnclosing(factory, enclosing, klass);
        }

        return ReflectedClassImpl.createOuterClass(factory, klass);
    }

    /**
     * Initialize the cache of the given core by creating symbols for
     * commonly used classes (of java lang mainly).
     */
    public static Map<Object, JClassSymbol> initCommonSyms(SymbolResolver resolver) {
        Map<Object, JClassSymbol> cache = new HashMap<>();
        for (Class<?> klass : COMMON_TYPES) {
            JClassSymbol sym = resolver.resolveClassFromBinaryName(klass.getName());
            assert sym != null : "Bootstrap " + klass + " was not found by " + resolver;
            cache.put(klass, sym);
            cache.put(klass.getName(), sym); // this is used by symbol resolvers
        }
        return cache;
    }

    private static final List<Class<?>> COMMON_TYPES = CollectionUtil.listOf(
        // todo consider putting java.util in there ?

        boolean.class,
        byte.class,
        char.class,
        double.class,
        float.class,
        int.class,
        long.class,
        short.class,
        void.class,

        Serializable.class,

        // These are just those that seem the most common,
        // I didn't run any statistics or anything
        // If a type is not in there it will be queried like all
        // the others through the ClassLoader
        AssertionError.class,
        Boolean.class,
        Byte.class,
        Character.class,
        CharSequence.class,
        Class.class,
        ClassCastException.class,
        ClassLoader.class,
        ClassNotFoundException.class,
        Cloneable.class,
        Comparable.class,
        Deprecated.class,
        Double.class,
        Enum.class,
        Error.class,
        Exception.class,
        Float.class,
        FunctionalInterface.class,
        IllegalAccessException.class,
        IllegalArgumentException.class,
        IllegalStateException.class,
        IndexOutOfBoundsException.class,
        Integer.class,
        InternalError.class,
        InterruptedException.class,
        Iterable.class,
        LinkageError.class,
        Long.class,
        Math.class,
        NegativeArraySizeException.class,
        NoClassDefFoundError.class,
        NoSuchFieldError.class,
        NoSuchFieldException.class,
        NoSuchMethodError.class,
        NoSuchMethodException.class,
        NullPointerException.class,
        Number.class,
        NumberFormatException.class,
        Object.class,
        OutOfMemoryError.class,
        Override.class,
        Package.class,
        Process.class,
        ReflectiveOperationException.class,
        Runnable.class,
        Runtime.class,
        RuntimeException.class,
        SafeVarargs.class,
        Short.class,
        StackOverflowError.class,
        String.class,
        StringBuffer.class,
        StringBuilder.class,
        SuppressWarnings.class,
        System.class,
        Thread.class,
        Throwable.class,
        Void.class
    );

}
