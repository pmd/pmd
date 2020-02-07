/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;


import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Locale;

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


    private final String token = name().toLowerCase(Locale.ROOT);
    private final int reflect;

    JModifier(int reflect) {
        this.reflect = reflect;
    }

    /**
     * Returns the constant of java.lang.reflect.Modifier that this
     * modifier corresponds to. Be aware that {@link #DEFAULT} has
     * no equivalent in {@link Modifier}.
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
