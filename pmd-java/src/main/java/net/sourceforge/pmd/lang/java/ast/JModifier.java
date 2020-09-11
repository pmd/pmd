/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;


import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Locale;

import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;

/**
 * A Java modifier. The ordering of constants respects the ordering
 * recommended by the JLS.
 */
// Note: the class is named JModifier and not Modifier to avoid conflict
// with java.lang.reflect.Modifier
public enum JModifier {
    // for anything
    PUBLIC(Modifier.PUBLIC),
    PROTECTED(Modifier.PROTECTED),
    PRIVATE(Modifier.PRIVATE),

    /** Modifier {@code "sealed"} (preview feature of JDK 15). */
    SEALED(0),
    /** Modifier {@code "non-sealed"} (preview feature of JDK 15). */
    NON_SEALED("non-sealed", 0),

    ABSTRACT(Modifier.ABSTRACT),
    STATIC(Modifier.STATIC),
    FINAL(Modifier.FINAL),

    // for methods
    SYNCHRONIZED(Modifier.SYNCHRONIZED),
    NATIVE(Modifier.NATIVE),
    DEFAULT(0),

    // not for fields
    STRICTFP(Modifier.STRICT),

    // for fields
    TRANSIENT(Modifier.TRANSIENT),
    VOLATILE(Modifier.VOLATILE);


    private final String token;
    private final int reflect;

    JModifier(int reflect) {
        this.token = name().toLowerCase(Locale.ROOT);
        this.reflect = reflect;
    }

    JModifier(String token, int reflect) {
        this.token = token;
        this.reflect = reflect;
    }

    /**
     * Returns the constant of java.lang.reflect.Modifier that this
     * modifier corresponds to. Be aware that the following constants
     * are source-level modifiers only, for which this method returns 0:
     * <ul>
     * <li>{@link #DEFAULT}: this doesn't exist at the class file level.
     * A default method is a non-static non-abstract public method declared
     * in an interface ({@link JMethodSymbol#isDefaultMethod()}.
     * <li>{@link #SEALED}: a sealed class has an attribute {@code PermittedSubclasses}
     * with a non-zero length (in the compiled class file)
     * <li>{@link #NON_SEALED}: this doesn't exist at the class file level at all.
     * But a class must have the non-sealed modifier in source if it
     * is neither sealed, nor final, and appears in the {@code PermittedSubclasses}
     * attribute of some direct supertype.
     * </ul>
     */
    public int getReflectMod() {
        return reflect;
    }


    /**
     * Returns how the modifier is written in source.
     */
    public String getToken() {
        return token;
    }

    @Override
    public String toString() {
        return getToken();
    }


    public static int toReflect(Collection<JModifier> mods) {
        int res = 0;
        for (JModifier mod : mods) {
            res |= mod.getReflectMod();
        }
        return res;
    }

}
