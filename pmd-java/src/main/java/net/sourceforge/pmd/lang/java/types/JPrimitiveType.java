/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.pcollections.PSet;

import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymAnnot;

/**
 * Mirror a primitive types. Even though {@code void.class.isPrimitive()}
 * returns true, we don't treat it as such and represent it with
 * {@link TypeSystem#NO_TYPE} instead.
 */
public final class JPrimitiveType implements JTypeMirror {

    Set<JTypeMirror> superTypes; // set by the constructor of TypeSystem

    private final TypeSystem ts;

    private final PrimitiveTypeKind kind;
    /** Primitive class. */
    private final JClassSymbol type;
    /** Boxed representation. */
    private final JClassType box;
    private final PSet<SymAnnot> typeAnnots;

    JPrimitiveType(TypeSystem ts, PrimitiveTypeKind kind, JClassSymbol type, JClassSymbol boxType, PSet<SymAnnot> typeAnnots) {
        this.ts = ts;
        this.kind = kind;
        this.type = type;
        this.typeAnnots = typeAnnots;
        this.box = new BoxedPrimitive(ts, boxType, this, typeAnnots); // not erased
    }

    @Override
    public PSet<SymAnnot> getTypeAnnotations() {
        return typeAnnots;
    }

    @Override
    public JTypeMirror withAnnotations(PSet<SymAnnot> newTypeAnnots) {
        if (newTypeAnnots.isEmpty() && this.typeAnnots.isEmpty()) {
            return this;
        }
        return new JPrimitiveType(ts, kind, type, box.getSymbol(), newTypeAnnots);
    }

    @Override
    public TypeSystem getTypeSystem() {
        return ts;
    }

    @Override
    public JClassType box() {
        return box;
    }

    @Override
    public JPrimitiveType unbox() {
        return this;
    }

    @Override
    public JTypeMirror getErasure() {
        return this;
    }

    /**
     * Returns the type of the primitive class, eg {@link Integer#TYPE}.
     * The returned type {@link Class#isPrimitive()} is true.
     */
    @Override
    public @NonNull JClassSymbol getSymbol() {
        return type;
    }

    @Override
    public boolean isNumeric() {
        return kind != PrimitiveTypeKind.BOOLEAN;
    }


    @Override
    public boolean isPrimitive(PrimitiveTypeKind kind) {
        return this.kind == Objects.requireNonNull(kind, "null kind");
    }

    @Override
    public boolean isFloatingPoint() {
        return kind == PrimitiveTypeKind.DOUBLE || kind == PrimitiveTypeKind.FLOAT;
    }

    @Override
    public boolean isIntegral() {
        return kind != PrimitiveTypeKind.BOOLEAN && !isFloatingPoint();
    }

    @Override
    public boolean isPrimitive() {
        return true;
    }

    @Override
    public Set<JTypeMirror> getSuperTypeSet() {
        return superTypes;
    }

    @Override
    public String toString() {
        return TypePrettyPrint.prettyPrint(this);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof JPrimitiveType && ((JPrimitiveType) obj).kind == this.kind;
    }

    @Override
    public int hashCode() {
        return kind.hashCode();
    }

    /**
     * Returns the token used to represent the type in source,
     * e.g. "int" or "double".
     */
    public @NonNull String getSimpleName() {
        return kind.name;
    }

    public PrimitiveTypeKind getKind() {
        return kind;
    }

    @Override
    public <T, P> T acceptVisitor(JTypeVisitor<T, P> visitor, P p) {
        return visitor.visitPrimitive(this, p);
    }

    @Override
    public JTypeMirror subst(Function<? super SubstVar, ? extends @NonNull JTypeMirror> subst) {
        return this;
    }

    public enum PrimitiveTypeKind {
        BOOLEAN(boolean.class),
        CHAR(char.class),
        BYTE(byte.class),
        SHORT(short.class),
        INT(int.class),
        LONG(long.class),
        FLOAT(float.class),
        DOUBLE(double.class);

        final String name = name().toLowerCase(Locale.ROOT);
        private final Class<?> jvm;

        PrimitiveTypeKind(Class<?> jvm) {
            this.jvm = jvm;
        }

        public String getSimpleName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }

        public Class<?> jvmRepr() {
            return jvm;
        }

        /**
         * Gets an enum constant from the token used to represent it in source,
         * e.g. "int" or "double". Note that "void" is not a valid primitive name
         * in this API, and this would return null in this case.
         *
         * @param token String token
         *
         * @return A constant, or null if the string doesn't correspond
         *     to a primitive type
         */
        public static @Nullable PrimitiveTypeKind fromName(String token) {
            switch (token) {
            case "boolean": return BOOLEAN;
            case "char": return CHAR;
            case "byte": return BYTE;
            case "short": return SHORT;
            case "int": return INT;
            case "long": return LONG;
            case "float": return FLOAT;
            case "double": return DOUBLE;
            default:
                return null;
            }
        }
    }

}
