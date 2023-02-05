/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.pcollections.PSet;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JExecutableSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeParameterSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymAnnot;
import net.sourceforge.pmd.lang.java.types.JPrimitiveType.PrimitiveTypeKind;
import net.sourceforge.pmd.lang.java.types.TypeOps.Convertibility;
import net.sourceforge.pmd.lang.java.types.internal.infer.InferenceVar;
import net.sourceforge.pmd.util.AssertionUtil;

/**
 * Type mirrors represent Java types. They are created by a {@link TypeSystem}
 * from {@linkplain JTypeDeclSymbol symbols}, a layer of abstraction above reflection
 * classes.
 *
 * <p>Type mirrors can be obtained {@linkplain TypesFromReflection from reflected types},
 * directly {@linkplain TypeNode#getTypeMirror() from nodes}, or from
 * arbitrary symbols (see {@link TypeSystem}).
 *
 * <p>Type mirrors are primarily divided between {@linkplain JPrimitiveType primitive types}
 * and reference types. Reference types can be of one of those kinds:
 * <ul>
 * <li>{@linkplain JClassType class or interface types}
 * <li>{@linkplain JArrayType array types}
 * <li>{@linkplain JIntersectionType intersection types}
 * <li>{@linkplain JTypeVar type variables}
 * </ul>
 *
 * <p>{@linkplain JWildcardType Wildcard types} implement this interface,
 * but are not really types, they can only occur as type arguments of a
 * class type.
 *
 * <p>A few other special types do not implement one of these public interfaces:
 * <ul>
 * <li>{@linkplain TypeSystem#NULL_TYPE The null type}
 * <li>{@linkplain TypeSystem#ERROR The error type}
 * <li>{@linkplain TypeSystem#UNKNOWN The unresolved type}
 * </ul>
 *
 * <p>Lastly, types may be {@linkplain InferenceVar inference variables},
 * which <i>should not ever occur</i> outside of a type inference
 * run and should be ignored when querying the AST. If you find an ivar,
 * report a bug.
 *
 * <p>Note that implementing this type hierarchy outside of this package
 * is not a supported usage of this API. The API is built to work with any
 * symbol implementation, and that is its extension point.
 */
public interface JTypeMirror extends JTypeVisitable {
    // TODO: unstable stuff (@Experimental)
    //  - Member access:
    //    - #getDeclaredField
    //    - #getDeclaredClass
    //  - In JWildcardType, the specification of some methods is unstable:
    //    - #isSubtypeOf/#isConvertibleTo
    //    - #getErasure

    // TODO figure out what parts of TypeOps/TypeConversion are internal
    //  - problem is that parts of those access package-private API, eg
    //  to implement capture.

    /**
     * Returns the type system that built this type.
     */
    TypeSystem getTypeSystem();


    /**
     * Return a list of annotations on this type. Annotations can be written
     * on nearly any type (eg {@code @A Out.@B In<@C T>}, {@code @A ? extends @B Up}).
     *
     * <p>For {@link JTypeVar}, this includes both the annotations defined
     * on the type var and those defined at use site. For instance
     * <pre>{@code
     *    <@A T> void accept(@B T t);
     * }</pre>
     * The T type var will have annotation {@code @A} in the symbol
     * ({@link JTypeParameterSymbol#getDeclaredAnnotations()})
     * and in the type var that is in the {@link JMethodSig#getTypeParameters()}.
     * In the formal parameter, the type var will have annotations {@code @B @A}.
     */
    // todo annotations do not participate in equality of types.
    PSet<SymAnnot> getTypeAnnotations();


    /**
     * Returns true if this type is the same type or a subtype of the
     * given type. Note that for convenience, this returns true if both
     * types are primitive, and this type is convertible to the other
     * through primitive widening. See {@link Convertibility#bySubtyping()}.
     *
     * @throws NullPointerException If the argument is null
     */
    default boolean isSubtypeOf(@NonNull JTypeMirror other) {
        return isConvertibleTo(other).bySubtyping();
    }

    /**
     * Tests this type's convertibility to the other type. See
     * {@link Convertibility} for a description of the results.
     *
     * <p>Note that this does not check for boxing/unboxing conversions.
     *
     * @throws NullPointerException If the argument is null
     */
    default Convertibility isConvertibleTo(@NonNull JTypeMirror other) {
        return TypeOps.isConvertible(this, other);
    }


    /**
     * Returns the set of (nominal) supertypes of this type.
     * If this is a primitive type, returns the set of other
     * primitives to which this type is convertible by widening
     * conversion (eg for {@code long} returns {@code {long, float, double}}).
     *
     * <p>The returned set always contains this type, so is
     * never empty. Ordering is stable, though unspecified.
     *
     * <p>Note that this set contains {@link TypeSystem#OBJECT}
     * for interfaces too.
     *
     * @throws UnsupportedOperationException If this is the null type
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
     * representation of a type, you should do {@code t.getErasure().getSymbol()}.
     * The erasure procedure gets rid of types that have no symbol (except
     * if {@code t} is a wildcard type, or the {@link TypeSystem#NULL_TYPE})
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
     * <li>{@link JArrayType}: a {@link JClassSymbol}, if the element type does present a symbol.
     * <li>{@link JTypeVar}: a {@link JTypeParameterSymbol}, or null if this is a capture variable.
     * Note that the erasure yields a different symbol (eg Object for unbounded tvars).
     * <li>{@link JIntersectionType}: null, though their erasure always
     * presents a symbol.
     * <li>{@link JWildcardType}, {@link TypeSystem#NULL_TYPE the null type}: null, always
     * </ul>
     *
     * <p>Note that type annotations are not reflected on the
     * symbol, but only on the type.
     */
    default @Nullable JTypeDeclSymbol getSymbol() {
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
    default @Nullable JTypeMirror getAsSuper(@NonNull JClassSymbol symbol) {
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
        }

        return this instanceof JClassType && TypeOps.allArgsAreUnboundedWildcards(((JClassType) this).getTypeArgs());
    }


    /** Returns true if this type is a {@linkplain JPrimitiveType primitive type}. */
    default boolean isPrimitive() {
        return false; // overridden in JPrimitiveType
    }

    /** Returns true if this type is the primitive type of the given kind in its type system. */
    default boolean isPrimitive(PrimitiveTypeKind kind) {
        return false; // overridden in JPrimitiveType
    }

    /**
     * Returns true if this type is a {@linkplain JPrimitiveType primitive type}
     * of a floating point type.
     */
    default boolean isFloatingPoint() {
        return false; // overridden in JPrimitiveType
    }

    /**
     * Returns true if this type is a {@linkplain JPrimitiveType primitive type}
     * of an integral type.
     */
    default boolean isIntegral() {
        return false; // overridden in JPrimitiveType
    }


    /** Returns true if this type is a {@linkplain JTypeVar type variable}. */
    default boolean isTypeVariable() {
        return this instanceof JTypeVar;
    }

    /**
     * Returns true if this type is a boxed primitive type. This is a {@link JClassType},
     * whose {@link #unbox()} method returns a {@link JPrimitiveType}.
     */
    default boolean isBoxedPrimitive() {
        return unbox() != this; // NOPMD CompareObjectsWithEquals
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

    /**
     * Returns true if this is {@link TypeSystem#OBJECT}.
     */
    default boolean isTop() {
        return false; // overridden
    }


    /**
     * Returns true if this is {@link TypeSystem#NULL_TYPE}.
     */
    default boolean isBottom() {
        return false; // overridden
    }

    /**
     * Returns true if this is {@link TypeSystem#NO_TYPE}, ie {@code void}.
     */
    default boolean isVoid() {
        return this == getTypeSystem().NO_TYPE; // NOPMD CompareObjectsWithEquals
    }

    /** Returns true if this is an {@linkplain JArrayType array type}. */
    default boolean isArray() {
        return this instanceof JArrayType;
    }

    /**
     * Returns true if this represents the *declaration* of a generic
     * class or interface and not some parameterization. This is the
     * "canonical" form of a parameterized type.
     *
     * <p>In that case, the {@link JClassType#getTypeArgs()} is the same
     * as {@link JClassType#getFormalTypeParams()}.
     *
     * <p>The generic type declaration of a generic type may be obtained
     * with {@link JClassType#getGenericTypeDeclaration()}.
     */
    default boolean isGenericTypeDeclaration() {
        return false;
    }



    /**
     * Returns true if this type is a generic class type.
     * This means, the symbol declares some type parameters,
     * which is also true for erased types, including raw types.
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
     * Returns true if this is an interface type. Annotations are also
     * interface types.
     */
    default boolean isInterface() {
        JTypeDeclSymbol sym = getSymbol();
        return sym != null && sym.isInterface();
    }


    /**
     * Returns a stream of method signatures declared in and inherited
     * by this type. Method signatures are created on-demand by this
     * method, they're not reused between calls. This stream does not
     * include constructors.
     *
     * @param prefilter Filter selecting symbols for which a signature
     *                  should be created and yielded by the stream
     *
     * @experimental streams are a bit impractical when it comes to
     *     configuring the filter. Possibly a specialized API should be introduced.
     *     We need to support the use cases of the symbol table, ie filter by name + accessibility + staticity,
     *     and also possibly use cases for rules, like getting a method from
     *     a known signature. See also {@link JClassType#getDeclaredMethod(JExecutableSymbol)},
     *     which looks like this. Unifying this API would be nice.
     */
    @Experimental
    default Stream<JMethodSig> streamMethods(Predicate<? super JMethodSymbol> prefilter) {
        return Stream.empty();
    }

    /**
     * Like {@link #streamMethods(Predicate) streamMethods}, but does
     * not recurse into supertypes. Note that only class and array types
     * declare methods themselves.
     *
     * @experimental See {@link #streamMethods(Predicate)}
     *
     * @see #streamMethods(Predicate)
     */
    @Experimental
    default Stream<JMethodSig> streamDeclaredMethods(Predicate<? super JMethodSymbol> prefilter) {
        return Stream.empty();
    }


    /**
     * Returns a list of all the declared constructors for this type.
     * Abstract types like type variables and interfaces have no constructors.
     */
    @Experimental
    default List<JMethodSig> getConstructors() {
        return Collections.emptyList();
    }


    @Override
    JTypeMirror subst(Function<? super SubstVar, ? extends @NonNull JTypeMirror> subst);


    /**
     * Returns a type mirror that is equal to this instance but has different
     * type annotations. Note that some types ignore this method and return
     * themselves without changing. Eg the null type cannot be annotated.
     *
     * @param newTypeAnnots New type annotations (not null)
     *
     * @return A new type, maybe this one
     */
    JTypeMirror withAnnotations(PSet<SymAnnot> newTypeAnnots);

    /**
     * Returns a type mirror that is equal to this instance but has one
     * more type annotation.
     *
     * @see #withAnnotations(PSet)
     */
    default JTypeMirror addAnnotation(@NonNull SymAnnot newAnnot) {
        AssertionUtil.requireParamNotNull("annot", newAnnot);
        return withAnnotations(getTypeAnnotations().plus(newAnnot));
    }


    /**
     * Returns true if the object is a type equivalent to this one. A
     * few kinds of types use reference identity, like captured type
     * variables, or the null type. A few special types are represented
     * by constants (see {@link TypeSystem}). Apart from those, types
     * should always be compared using this method. or {@link TypeOps#isSameType(JTypeMirror, JTypeMirror)}
     * (which is null-safe).
     *
     * <p>Note that types belonging to different type systems do <i>not</i>
     * test equal. The type system object is global to the analysis though,
     * so this should not ever happen in rules.
     *
     * @param o {@inheritDoc}
     *
     * @return {@inheritDoc}
     *
     * @implSpec This method should be implemented with
     *     {@link TypeOps#isSameType(JTypeMirror, JTypeMirror)},
     *     and perform no side-effects on inference variables.
     */
    @Override
    boolean equals(Object o);


    /**
     * The toString of type mirrors prints useful debug information,
     * but shouldn't be relied on anywhere, as it may change anytime.
     * Use {@link TypePrettyPrint} to display types.
     */
    @Override
    String toString();

}
