/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;


import java.lang.reflect.Modifier;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.symbols.internal.EmptyClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.internal.SymbolEquality;
import net.sourceforge.pmd.lang.java.symbols.internal.SymbolToStrings;
import net.sourceforge.pmd.lang.java.types.JPrimitiveType.PrimitiveTypeKind;

abstract class BasePrimitiveSymbol extends EmptyClassSymbol {

    BasePrimitiveSymbol(TypeSystem ts) {
        super(() -> ts);
    }

    static final class VoidSymbol extends BasePrimitiveSymbol {

        VoidSymbol(TypeSystem ts) {
            super(ts);
        }

        @Override
        public @NonNull String getBinaryName() {
            return "void";
        }

        @Override
        public String getCanonicalName() {
            return "void";
        }

        @Override
        public @NonNull String getSimpleName() {
            return "void";
        }

    }


    static final class RealPrimitiveSymbol extends BasePrimitiveSymbol {

        private final PrimitiveTypeKind kind;

        RealPrimitiveSymbol(TypeSystem ts, PrimitiveTypeKind kind) {
            super(ts);
            this.kind = kind;
        }

        @Override
        public @NonNull String getBinaryName() {
            return kind.getSimpleName();
        }

        @Override
        public @Nullable String getCanonicalName() {
            return kind.getSimpleName();
        }

        @Override
        public @NonNull String getSimpleName() {
            return kind.getSimpleName();
        }


    }

    @Override
    public @NonNull String getPackageName() {
        return PRIMITIVE_PACKAGE;
    }

    @Override
    public boolean isPrimitive() {
        return true;
    }

    @Override
    public int getModifiers() {
        return Modifier.PUBLIC | Modifier.ABSTRACT | Modifier.FINAL;
    }

    @Override
    public int hashCode() {
        return SymbolEquality.CLASS.hash(this);
    }

    @Override
    public boolean equals(Object obj) {
        return SymbolEquality.CLASS.equals(this, obj);
    }

    @Override
    public String toString() {
        return SymbolToStrings.SHARED.toString(this);
    }
}
