/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import static net.sourceforge.pmd.lang.java.types.JVariableSig.FieldSig;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeParameterSymbol;
import net.sourceforge.pmd.lang.java.types.JPrimitiveType.PrimitiveTypeKind;

/**
 * Type mirrors represent Java types. They are created by a {@link TypeSystem}
 * from {@link JTypeDeclSymbol symbols} (a layer of abstraction above reflection
 * classes).
 *
 * <p>Type mirrors can be obtained {@linkplain TypesFromReflection from reflected types},
 * directly {@linkplain TypeNode#getTypeMirror() from nodes}, or from
 * arbitrary symbols (see {@link TypeSystem}).
 */
@Experimental
public interface JTypeMirror extends JTypeVisitable {

    /**
     * Returns the type system that built this type.
     */
    TypeSystem getTypeSystem();


    /**
     * Returns true if this type is the same type or a subtype of the given type.
     *
     * @param unchecked Whether unchecked conversion may be used to make
     *                  the types conform. A raw type may be converted
     *                  to any parameterized type of the same family
     *                  via unchecked conversion.
     */
    default boolean isSubtypeOf(JTypeMirror other, boolean unchecked) {
        return other != null && TypeOps.isSubtype(this, other, unchecked);
    }


    /**
     * Returns true if this type is the same type or a subtype of the given type.
     */
    default boolean isSubtypeOf(JTypeMirror other) {
        return isSubtypeOf(other, false);
    }


    /**
     * Returns the set of (nominal) supertypes of this type.
     * If this is a primitive type, returns the set of other
     * primitives to which this type is convertible (eg for
     * {@code long} returns {@code {long, float, double}}.
     *
     * <p>The returned set always contains this type, so is
     * never empty. Ordering is stable, though unspecified.
     *
     * @throws IllegalArgumentException If this is the null type
     */
    default Set<JTypeMirror> getSuperTypeSet() {
        return TypeOps.getSuperTypeSet(this);
    }


    /**
     * Returns the erasure of this type. Erasure is defined by JLS§4.6,
     * an adapted definition follows:
     *
     * <blockquote>
     * <ol>
     * <li>The erasure of a parameterized type (§4.5) {@literal G<T1,...,Tn>} is |G|.
     * <li>The erasure of a nested type T.C is |T|.C.
     * <li>The erasure of an array type T[] is |T|[].
     * <li>The erasure of a type variable (§4.4) is the erasure of its upper bound.
     * <li>The erasure of an intersection type is the erasure of its leftmost component.
     * <li>The erasure of every other type is the type itself.
     * </ol>
     * </blockquote>
     *
     * <p>The JVM representation of a type is in general the symbol
     * of its erasure. So to get a {@link Class} instance for the runtime
     * representation of a type, you should do {@code t.getErasure().getSymbol().getJvmRepr()}.
     * This should not throw an NPE, since the erasure procedure gets
     * rid of types that have no symbol (except if {@code t} is a wildcard type,
     * {@link TypeSystem#ERROR_TYPE})
     */
    default JTypeMirror getErasure() {
        return this;
    }


    /**
     * Returns the symbol declaring this type. {@linkplain #isReifiable() Reifiable}
     * types present a symbol, and some other types too. This method's
     * return value depends on this type:
     * <ul>
     * <li>{@link JClassType}: a {@link JClassSymbol}, always (even if not reifiable)
     * <li>{@link JPrimitiveType}: a {@link JClassSymbol}, always
     * <li>{@link JArrayType}: a {@link JClassSymbol}, if the element type does present a symbol
     * <li>{@link JTypeVar}: a {@link JTypeParameterSymbol}, always. Note that the
     * erasure yields a different symbol (eg Object for unbounded tvars).
     * <li>{@link JIntersectionType}: null, though their erasure always
     * presents a symbol. Additionally, intersection types
     * {@linkplain JIntersectionType#getInducedClassType() induce a class type}
     * (which is mostly meaningless and only relevant to check for functional interfaces).
     *
     * <li>{@link JWildcardType}, {@link TypeSystem#NULL_TYPE the null type}: null, always
     * </ul>
     */
    @Nullable
    default JTypeDeclSymbol getSymbol() {
        return null;
    }


    /**
     * Returns the primitive wrapper type of this type, if this is a
     * primitive type. Otherwise returns this type unchanged.
     */
    default JTypeMirror box() {
        return this;
    }


    /**
     * Returns the unboxed version of this type. Returns this type unchanged
     * if this is not a primitive wrapper type.
     */
    default JTypeMirror unbox() {
        return this;
    }


    /**
     * Returns the most specific declared supertype of this type whose erasure
     * is the same as that of the parameter. E.g. for {@code Enum<E>, Comparable},
     * returns {@code Comparable<E>}.
     *
     * <p>Returns null if that can't be found, meaning that the given type
     * is not a supertype of this type.
     */
    @Nullable
    default JTypeMirror getAsSuper(JClassSymbol symbol) {
        return TypeOps.asSuper(this, symbol);
    }



    /**
     * Returns true if this type is reifiable. If so, its {@link #getSymbol() symbol}
     * will not be null (the reverse is not necessarily true).
     *
     * <p>Reifiable types are those types that are completely available
     * at run time. See also <a href="https://docs.oracle.com/javase/specs/jls/se13/html/jls-4.html#jls-4.7">JLS§4.7</a>
     */
    default boolean isReifiable() {
        if (this instanceof JPrimitiveType) {
            return true;
        } else if (this instanceof JArrayType) {
            return ((JArrayType) this).getElementType().isReifiable();
        } else if (this instanceof JClassType) {
            return TypeOps.allArgsAreUnboundedWildcards(((JClassType) this).getTypeArgs());
        } else {
            return false;
        }
    }


    /** Returns true if this type is a {@linkplain JPrimitiveType primitive type}. */
    default boolean isPrimitive() {
        return false; // overridden in JPrimitiveType
    }

    /** Returns true if this type is the primitive type of the given kind in its type system. */
    default boolean isPrimitive(PrimitiveTypeKind kind) {
        return false; // overridden in JPrimitiveType
    }


    /** Returns true if this type is a {@linkplain JTypeVar type variable}. */
    default boolean isTypeVariable() {
        return this instanceof JTypeVar;
    }


    /**
     * Returns true if this type is generic, and it it neither {@linkplain #isRaw() raw},
     * nor a {@linkplain JClassType#isGenericTypeDeclaration() generic type declaration}.
     *
     * <p>E.g. returns true for {@code List<String>} or {@code Enum<KeyCode>},
     * but not for {@code List} (raw type), {@code List<T>} (generic type declaration),
     * or {@code KeyCode} (not a generic type).
     */
    default boolean isParameterizedType() {
        return false;
    }


    /**
     * Returns true if this type is a boxed primitive type. This is a {@link JClassType},
     * whose {@link #unbox()} method returns a {@link JPrimitiveType}.
     */
    default boolean isBoxedPrimitive() {
        return unbox() != this;
    }


    /**
     * Returns true if this is a primitive numeric type. The only
     * non-numeric primitive type is {@link TypeSystem#BOOLEAN}.
     */
    default boolean isNumeric() {
        return false;
    }


    /** Returns true if this is a {@linkplain JClassType class or interface type}. */
    default boolean isClassOrInterface() {
        return this instanceof JClassType;
    }


    /** Returns true if this is an {@linkplain JArrayType array type}. */
    default boolean isArray() {
        return this instanceof JArrayType;
    }


    /**
     * Returns true if this is a raw type. This may be
     * <ul>
     * <li>A generic class or interface type for which no type arguments
     * were provided
     * <li>An array type whose element type is raw
     * <li>A non-static member type of a raw type
     * </ul>
     *
     * <p>https://docs.oracle.com/javase/specs/jls/se11/html/jls-4.html#jls-4.8
     *
     * @see JClassType#isRaw()
     */
    default boolean isRaw() {
        return false;
    }


    /**
     * Returns true if this type is a generic class type.
     * This means, the symbol declares some type parameters,
     * and is also true for erased types, including raw types.
     *
     * <p>For example, {@code List}, {@code List<T>}, and {@code List<String>}
     * are generic, but {@code String} is not.
     *
     * @see JClassType#isGeneric().
     */
    default boolean isGeneric() {
        return false;
    }


    /**
     * Returns a stream of method signatures declared in and inherited
     * by this type. Method signatures are created on-demand by this
     * method, they're not reused between calls. This stream does not
     * include constructors.
     *
     * @param prefilter Filter selecting symbols for which a signature
     *                  should be created and yielded by the stream
     */
    default Stream<JMethodSig> streamMethods(Predicate<? super JMethodSymbol> prefilter) {
        return Stream.empty();
    }


    /**
     * Returns a list of all the declared constructors for this type.
     * Abstract types like type variables and interfaces have no constructors.
     */
    default List<JMethodSig> getConstructors() {
        return Collections.emptyList();
    }


    /**
     * Returns the signature of the field with the given name, possibly
     * inherited from a supertype. May return {@link TypeSystem#UNRESOLVED_TYPE}.
     *
     * @param name Name of the field
     */
    default @Nullable FieldSig getField(String name) { // todo change param to JFieldSymbol
        return null;
    }


    /**
     * Returns true if this is an interface type. Annotations are also
     * interface types.
     */
    default boolean isInterface() {
        JTypeDeclSymbol sym = getSymbol();
        return sym != null && sym.isInterface();
    }


    @Override
    JTypeMirror subst(Function<? super SubstVar, ? extends @NonNull JTypeMirror> subst);


    /**
     * Returns true if the object is a type equivalent to this one. A
     * few kinds of types use reference identity, like captured type
     * variables, or the null type. Apart from those, types should always
     * be compared using this method. A few special types are represented
     * by constants (see {@link TypeSystem}).
     *
     * <p>This method should be implemented with {@link TypeOps#isSameType(JTypeMirror, JTypeMirror)},
     * and perform no side-effects on inference variables.
     *
     * @param o {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    boolean equals(Object o);


    /**
     * The toString of type mirrors prints useful debug information,
     * but shouldn't be relied on anywhere.
     *
     * FIXME this is relied on in many tests...
     */
    @Override
    String toString();

}
