/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import static net.sourceforge.pmd.util.CollectionUtil.emptyList;
import static net.sourceforge.pmd.util.CollectionUtil.immutableSetOf;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.pcollections.HashTreePSet;
import org.pcollections.PSet;

import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JExecutableSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFormalParamSymbol;
import net.sourceforge.pmd.lang.java.symbols.JLocalVariableSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeParameterSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolResolver;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymAnnot;
import net.sourceforge.pmd.lang.java.symbols.internal.UnresolvedClassStore;
import net.sourceforge.pmd.lang.java.symbols.internal.asm.AsmSymbolResolver;
import net.sourceforge.pmd.lang.java.symbols.internal.asm.Classpath;
import net.sourceforge.pmd.lang.java.types.BasePrimitiveSymbol.RealPrimitiveSymbol;
import net.sourceforge.pmd.lang.java.types.BasePrimitiveSymbol.VoidSymbol;
import net.sourceforge.pmd.lang.java.types.JPrimitiveType.PrimitiveTypeKind;
import net.sourceforge.pmd.util.AssertionUtil;
import net.sourceforge.pmd.util.CollectionUtil;

/**
 * Root context object for type analysis. Type systems own a global
 * {@link SymbolResolver}, which creates and caches external symbols.
 * Methods of this class promote symbols to types, and compose types together.
 * {@link TypeOps} and {@link TypeConversion} have some more operations on types.
 *
 * <p>Some special types are presented as constant fields, eg {@link #OBJECT}
 * or {@link #NULL_TYPE}. These are always comparable by reference.
 * Note that the primitive wrapper types are not exposed as constants
 * here, but can be accessed by using the {@link JTypeMirror#box() box}
 * method on some primitive constant.
 *
 * <p>The lifetime of a type system is the analysis: it is shared by
 * all compilation units.
 * TODO this is hacked together by comparing the ClassLoader, but this
 * should be in the language instance
 *
 * <p>Nodes have a reference to the type system they were created for:
 * {@link JavaNode#getTypeSystem()}.
 */
@SuppressWarnings("PMD.CompareObjectsWithEquals")
public final class TypeSystem {

    /**
     * Top type of the reference type system. This is the type for the
     * {@link Object} class. Note that even interfaces have this type
     * as a supertype ({@link JTypeMirror#getSuperTypeSet()}).
     */
    public final JClassType OBJECT;

    /**
     * The bottom type of the reference type system. This is named
     * the <i>null type</i> in the JLS and is not denotable in Java
     * programs.
     *
     * <p>This implementation uses this as the type of the 'null' literal.
     *
     * <p>The null type has no symbol.
     */
    public final JTypeMirror NULL_TYPE = new NullType(this);


    /** Primitive type {@code boolean}. */
    public final JPrimitiveType BOOLEAN;
    /** Primitive type {@code char}. */
    public final JPrimitiveType CHAR;
    /** Primitive type {@code byte}. */
    public final JPrimitiveType BYTE;
    /** Primitive type {@code short}. */
    public final JPrimitiveType SHORT;
    /** Primitive type {@code int}. */
    public final JPrimitiveType INT;
    /** Primitive type {@code long}. */
    public final JPrimitiveType LONG;
    /** Primitive type {@code float}. */
    public final JPrimitiveType FLOAT;
    /** Primitive type {@code double}. */
    public final JPrimitiveType DOUBLE;

    /**
     * The set of all primitive types. See {@link #getPrimitive(PrimitiveTypeKind)}.
     */
    public final Set<JPrimitiveType> allPrimitives;
    private final Map<PrimitiveTypeKind, JPrimitiveType> primitivesByKind;

    /**
     * A constant to represent the normal absence of a type. The
     * primitive {@code void.class} represents that type, and this
     * is the return type of a void method.
     *
     * <p>Note that the type of the class literal {@code void.class}
     * is {@code Class<java.lang.Void>}, not NO_TYPE.
     *
     * <p>{@code java.lang.Void} is represented by {@link #BOXED_VOID}.
     * Note that {@code BOXED_VOID.unbox() != NO_TYPE}, {@code NO_TYPE.box() != BOXED_VOID}.
     *
     * <p>{@code NO_TYPE.isPrimitive()} returns false, even though
     * {@code void.class.isPrimitive()} returns true.
     */
    public final JTypeMirror NO_TYPE;

    /**
     * A constant to represent an unresolved type. This means, that resolution
     * was attempted but failed and shouldn't be tried again. The symbol
     * is a {@link JClassSymbol}.
     *
     * <p>Note that {@link TypeOps#isConvertible(JTypeMirror, JTypeMirror)}
     * considers this type a subtype of anything, even primitive types.
     */
    public final JTypeMirror UNKNOWN;

    /**
     * A constant to represent a typing error. This would have been
     * reported by a compiler, and is used to propagate errors.
     *
     * <p>Note that {@link TypeOps#isConvertible(JTypeMirror, JTypeMirror)}
     * considers this type a subtype of anything, even primitive types.
     */
    public final JTypeMirror ERROR;

    /**
     * Sentinel value for an unresolved method. This type corresponds to
     * a method declaration in the type {@link #UNKNOWN}, returning {@link #UNKNOWN}.
     */
    public final JMethodSig UNRESOLVED_METHOD = new UnresolvedMethodSig(this);

    /*
     * Common, non-special types.
     */

    /** The unbounded wildcard, "?". */
    public final JWildcardType UNBOUNDED_WILD;

    /** The interface Cloneable. This is included because it is a supertype of array types. */
    public final JClassType CLONEABLE;
    /** The interface Serializable. This is included because it is a supertype of array types. */
    public final JClassType SERIALIZABLE;

    /**
     * This is the boxed type of {@code Void.class}, not to be confused with
     * {@code void.class}, which in this framework is represented by
     * {@link #NO_TYPE}.
     *
     * <p>Note that {@code BOXED_VOID.unbox() != NO_TYPE}, {@code NO_TYPE.box() != BOXED_VOID}.
     */
    public final JClassType BOXED_VOID;


    /** Contains special types, that must be shared to be comparable by reference. */
    private final Map<JTypeDeclSymbol, JTypeMirror> sharedTypes;
    // test only
    final SymbolResolver resolver;

    /**
     * Builds a new type system. Its public fields will be initialized
     * with fresh types, unrelated to other types.
     *
     * @param bootstrapResourceLoader Classloader used to resolve class files
     *                                to populate the fields of the new type
     *                                system
     */
    public static TypeSystem usingClassLoaderClasspath(ClassLoader bootstrapResourceLoader) {
        return usingClasspath(Classpath.forClassLoader(bootstrapResourceLoader));
    }

    /**
     * Builds a new type system. Its public fields will be initialized
     * with fresh types, unrelated to other types.
     *
     * @param bootstrapResourceLoader Classpath used to resolve class files
     *                                to populate the fields of the new type
     *                                system
     */
    public static TypeSystem usingClasspath(Classpath bootstrapResourceLoader) {
        return new TypeSystem(ts -> new AsmSymbolResolver(ts, bootstrapResourceLoader));
    }

    /**
     * Builds a new type system. Its public fields will be initialized
     * with fresh types, unrelated to other types.
     *
     * @param symResolverMaker A function that creates a new symbol
     *                         resolver, which will be owned by the
     *                         new type system. Because of cyclic
     *                         dependencies, the new type system
     *                         is leaked before its initialization
     *                         completes, so fields of the type system
     *                         are unusable at that time.
     *                         The resolver is used to create some shared
     *                         types: {@link #OBJECT}, {@link #CLONEABLE},
     *                         {@link #SERIALIZABLE}, {@link #BOXED_VOID}.
     */
    public TypeSystem(Function<TypeSystem, ? extends SymbolResolver> symResolverMaker) {
        this.resolver = symResolverMaker.apply(this); // leak the this

        // initialize primitives. their constructor also initializes their box + box erasure

        BOOLEAN = createPrimitive(PrimitiveTypeKind.BOOLEAN, Boolean.class);
        CHAR = createPrimitive(PrimitiveTypeKind.CHAR, Character.class);
        BYTE = createPrimitive(PrimitiveTypeKind.BYTE, Byte.class);
        SHORT = createPrimitive(PrimitiveTypeKind.SHORT, Short.class);
        INT = createPrimitive(PrimitiveTypeKind.INT, Integer.class);
        LONG = createPrimitive(PrimitiveTypeKind.LONG, Long.class);
        FLOAT = createPrimitive(PrimitiveTypeKind.FLOAT, Float.class);
        DOUBLE = createPrimitive(PrimitiveTypeKind.DOUBLE, Double.class);

        BOOLEAN.superTypes = immutableSetOf(BOOLEAN);
        CHAR.superTypes = immutableSetOf(CHAR, INT, LONG, FLOAT, DOUBLE);
        BYTE.superTypes = immutableSetOf(BYTE, SHORT, INT, LONG, FLOAT, DOUBLE);
        SHORT.superTypes = immutableSetOf(SHORT, INT, LONG, FLOAT, DOUBLE);
        INT.superTypes = immutableSetOf(INT, LONG, FLOAT, DOUBLE);
        LONG.superTypes = immutableSetOf(LONG, FLOAT, DOUBLE);
        FLOAT.superTypes = immutableSetOf(FLOAT, DOUBLE);
        DOUBLE.superTypes = immutableSetOf(DOUBLE);

        this.allPrimitives = immutableSetOf(BOOLEAN, CHAR, BYTE, SHORT, INT, LONG, FLOAT, DOUBLE);
        this.primitivesByKind = new EnumMap<>(PrimitiveTypeKind.class);
        primitivesByKind.put(PrimitiveTypeKind.BOOLEAN, BOOLEAN);
        primitivesByKind.put(PrimitiveTypeKind.CHAR, CHAR);
        primitivesByKind.put(PrimitiveTypeKind.BYTE, BYTE);
        primitivesByKind.put(PrimitiveTypeKind.SHORT, SHORT);
        primitivesByKind.put(PrimitiveTypeKind.INT, INT);
        primitivesByKind.put(PrimitiveTypeKind.LONG, LONG);
        primitivesByKind.put(PrimitiveTypeKind.FLOAT, FLOAT);
        primitivesByKind.put(PrimitiveTypeKind.DOUBLE, DOUBLE);

        // note that those intentionally have names that are invalid as java identifiers
        // todo those special types should probably have a special implementation here,
        //  so as not to depend on UnresolvedClassStore
        UnresolvedClassStore unresolvedSyms = new UnresolvedClassStore(this);
        JClassSymbol unresolvedTypeSym = unresolvedSyms.makeUnresolvedReference("(*unknown*)", 0);
        UNKNOWN = new SentinelType(this, "(*unknown*)", unresolvedTypeSym);

        JClassSymbol errorTypeSym = unresolvedSyms.makeUnresolvedReference("(*error*)", 0);
        ERROR = new SentinelType(this, "(*error*)", errorTypeSym);

        JClassSymbol primitiveVoidSym = new VoidSymbol(this);
        NO_TYPE = new SentinelType(this, "void", primitiveVoidSym);

        // reuse instances for common types

        // this map is vital to preserve some of the invariants of
        // the framework, e.g., that primitive types are never represented
        // by a ClassType, or that OBJECT is unique

        // this is only appropriate for non-generic types

        Map<JClassSymbol, JTypeMirror> shared = new HashMap<>();

        OBJECT = addSpecial(Object.class, shared);
        SERIALIZABLE = addSpecial(Serializable.class, shared);
        CLONEABLE = addSpecial(Cloneable.class, shared);
        BOXED_VOID = addSpecial(Void.class, shared);

        shared.put(primitiveVoidSym, NO_TYPE);
        shared.put(unresolvedTypeSym, UNKNOWN);
        shared.put(errorTypeSym, ERROR);

        for (JPrimitiveType prim : allPrimitives) {
            // primitives have a special implementation for their box
            shared.put(prim.getSymbol(), prim);
            shared.put(prim.box().getSymbol(), prim.box());
        }

        // make it really untouchable
        this.sharedTypes = Collections.unmodifiableMap(new HashMap<>(shared));

        UNBOUNDED_WILD = new WildcardTypeImpl(this, true, OBJECT, HashTreePSet.empty());
    }

    /**
     * Returns the bootstrap symbol resolver. Concrete analysis passes
     * may decorate this with different resolvers.
     */
    public SymbolResolver bootstrapResolver() {
        return resolver;
    }

    // helpers for the constructor, cannot use typeOf, only for trusted types

    private JClassType addSpecial(Class<?> klass, Map<JClassSymbol, JTypeMirror> shared) {
        JClassSymbol sym = getBootStrapSymbol(klass);
        JClassType nonErased = new ClassTypeImpl(this, sym, HashTreePSet.empty());
        shared.put(sym, nonErased);
        return nonErased;
    }

    private JClassSymbol getBootStrapSymbol(Class<?> clazz) {
        AssertionUtil.requireParamNotNull("clazz", clazz);
        JClassSymbol sym = resolver.resolveClassFromBinaryName(clazz.getName());
        return Objects.requireNonNull(sym, "sym");
    }

    private @NonNull JPrimitiveType createPrimitive(PrimitiveTypeKind kind, Class<?> box) {
        return new JPrimitiveType(this, kind, new RealPrimitiveSymbol(this, kind), getBootStrapSymbol(box), HashTreePSet.empty());
    }


    // type creation routines

    /**
     * Returns the class symbol for the given reflected class. This asks
     * the classloader of this type system. Returns null if the parameter
     * is null, or the class is not available in the analysis classpath.
     *
     * @param clazz Class
     */
    public @Nullable JClassSymbol getClassSymbol(@Nullable Class<?> clazz) {
        if (clazz == null) {
            return null;
        } else if (clazz.isPrimitive()) {
            PrimitiveTypeKind kind = PrimitiveTypeKind.fromName(clazz.getName());
            if (kind == null) { // void
                return (JClassSymbol) NO_TYPE.getSymbol();
            }
            return getPrimitive(kind).getSymbol();
        } else if (clazz.isArray()) {
            return new ArraySymbolImpl(this, getClassSymbol(clazz.getComponentType()));
        }

        return resolver.resolveClassFromBinaryName(clazz.getName());
    }

    /**
     * Returns a symbol for the binary name. Returns null if the name is
     * null or the symbol is not found on the classpath. The class must
     * not be an array.
     *
     * @param binaryName Binary name
     *
     * @return A symbol, or null
     *
     * @throws IllegalArgumentException if the argument is not a binary name
     */
    public @Nullable JClassSymbol getClassSymbol(String binaryName) {
        return getClassSymbolImpl(binaryName, false);
    }

    /**
     * Returns a symbol for the canonical name. Returns null if the name is
     * null or the symbol is not found on the classpath. The class must
     * not be an array.
     *
     * <p>Canonical names separate nested classes with {@code .}
     * (periods) instead of {@code $} (dollars) as the JVM does. Users
     * usually use canonical names, but lookup is much more costly.
     *
     * @param canonicalName Canonical name
     *
     * @return A symbol, or null
     *
     * @throws IllegalArgumentException if the argument is not a binary name
     */
    public @Nullable JClassSymbol getClassSymbolFromCanonicalName(String canonicalName) {
        return getClassSymbolImpl(canonicalName, true);
    }

    private @Nullable JClassSymbol getClassSymbolImpl(String name, boolean isCanonical) {
        if (name == null) {
            return null;
        }
        if ("void".equals(name)) {
            return (JClassSymbol) NO_TYPE.getSymbol();
        }
        PrimitiveTypeKind kind = PrimitiveTypeKind.fromName(name);
        if (kind != null) { // void
            return getPrimitive(kind).getSymbol();
        }

        AssertionUtil.assertValidJavaBinaryNameNoArray(name);

        return isCanonical ? resolver.resolveClassFromCanonicalName(name)
                           : resolver.resolveClassFromBinaryName(name);
    }

    /**
     * Returns a type mirror for the given symbol. If the symbol declares
     * type parameters, then the resulting type is raw (differs from the
     * behaviour of {@link #declaration(JClassSymbol)}), meaning all its
     * supertypes are erased.
     *
     * <p>If the symbol is a {@link JTypeParameterSymbol type parameter},
     * returns a {@link JTypeVar}.
     *
     * <p>If the symbol is a {@link JClassSymbol}, then:
     * <ul>
     * <li>If it represents a primitive type, the corresponding {@link JPrimitiveType}
     * is returned (one of {@link #INT}, {@link #CHAR}, etc.).
     * <li>If it represents an array type, a new {@link JArrayType} is
     * returned. The component type will be built with a recursive call.
     * <li>If it represents a class or interface type, a {@link JClassType}
     * is returned.
     * <ul>
     *     <li>If the parameter {@code isErased} is true, and if the
     *     symbol declares type parameters, then it will be a
     *     {@linkplain JClassType#isRaw() raw type}. This means,
     *     which means all its generic supertypes are {@linkplain JClassType#hasErasedSuperTypes() erased}.
     *     <li>Otherwise, the generic supertypes are preserved. In particular,
     *     if the symbol declares type parameters itself, then it will
     *     be a {@linkplain JClassType#isGenericTypeDeclaration() generic type declaration}.
     * </ul>
     * If the symbol is a non-static member of another class, then the given
     * type's {@linkplain JClassType#getEnclosingType() enclosing type} is
     * created, applying the above rules about erasure recursively. A type
     * is either completely erased, or completely parameterized.
     * </li>
     * </ul>
     *
     * @param symbol   Symbol for the type declaration
     * @param isErased Whether the type should be consider erased, if it
     *                 represents a class or interface type. This does not
     *                 erase type variables, or array types for that matter.
     *
     * @return A type, or null if the symbol is null
     */
    public JTypeMirror typeOf(@Nullable JTypeDeclSymbol symbol, boolean isErased) {
        if (symbol == null) {
            return null;
        }

        // takes care of primitives, and constants like OBJECT or UNRESOLVED_TYPE
        JTypeMirror common = specialCache(symbol);
        if (common != null) {
            return common;
        }

        if (symbol instanceof JClassSymbol) {
            JClassSymbol classSym = (JClassSymbol) symbol;
            if (classSym.isArray()) {
                JTypeMirror component = typeOf(classSym.getArrayComponent(), isErased);
                assert component != null : "the symbol necessarily has an array component symbol";
                return arrayType(component, classSym);
            } else {
                return new ClassTypeImpl(this, classSym, emptyList(), isErased, HashTreePSet.empty());
            }
        } else if (symbol instanceof JTypeParameterSymbol) {
            return ((JTypeParameterSymbol) symbol).getTypeMirror();
        }
        throw AssertionUtil.shouldNotReachHere("Uncategorized type symbol " + symbol.getClass() + ": " + symbol);
    }

    // test only for now
    JClassType forceErase(JClassType t) {
        JClassType erasure = t.getErasure();
        if (erasure == t) {
            return new ErasedClassType(this, t.getSymbol(), t.getTypeAnnotations());
        }
        return erasure;
    }

    /**
     * Like {@link #typeOf(JTypeDeclSymbol, boolean)}, defaulting the
     * erased parameter to true. If the symbol is not generic,
     * the returned symbol is not actually raw.
     *
     * @param klass Symbol
     *
     * @return An erased class type
     */
    public JTypeMirror rawType(@Nullable JTypeDeclSymbol klass) {
        return typeOf(klass, true);
    }

    /**
     * Like {@link #typeOf(JTypeDeclSymbol, boolean)}, defaulting the
     * erased parameter to false. If the symbol is not generic,
     * the returned symbol is not actually a generic type declaration.
     *
     * @param klass Symbol
     *
     * @return An erased class type
     */
    public JTypeMirror declaration(@Nullable JClassSymbol klass) {
        return typeOf(klass, false);
    }

    /**
     * Produce a parameterized type with the given symbol and type arguments.
     * The type argument list must match the declared formal type parameters in
     * length. Non-generic symbols are accepted by this method, provided the
     * argument list is empty. If the symbol is unresolved, any type argument
     * list is accepted.
     *
     * <p>This method is equivalent to {@code rawType(klass).withTypeArguments(typeArgs)},
     * but that code would require a cast.
     *
     * @param klass    A symbol
     * @param typeArgs List of type arguments
     *
     * @throws IllegalArgumentException see {@link JClassType#withTypeArguments(List)}
     */
    // todo how does this behave with nested generic types
    public @NonNull JTypeMirror parameterise(@NonNull JClassSymbol klass, @NonNull List<? extends JTypeMirror> typeArgs) {
        if (typeArgs.isEmpty()) {
            return rawType(klass); // note this ensures that OBJECT and such is preserved
        }
        // if the type arguments are mismatched, the constructor will throw
        return new ClassTypeImpl(this, klass, CollectionUtil.defensiveUnmodifiableCopy(typeArgs), true, HashTreePSet.empty());
    }


    /**
     * Creates a new array type from an arbitrary element type.
     *
     * <pre>{@code
     * arrayType(T, 0)          = T
     * arrayType(T, 1)          = T[]
     * arrayType(T, 3)          = T[][][]
     * arrayType(T[], 2)        = T[][][]
     * }</pre>
     *
     * @param element       Element type
     * @param numDimensions Number of dimensions
     *
     * @return A new array type
     *
     * @throws IllegalArgumentException If numDimensions is negative
     * @throws IllegalArgumentException If the element type is a {@link JWildcardType},
     *                                  the null type, or {@link #NO_TYPE void}.
     * @throws NullPointerException     If the element type is null
     */
    public JTypeMirror arrayType(@NonNull JTypeMirror element, int numDimensions) {
        AssertionUtil.requireNonNegative("numDimensions", numDimensions);
        checkArrayElement(element); // note we throw even if numDimensions == 0

        if (numDimensions == 0) {
            return element;
        }

        JArrayType res = new JArrayType(this, element);
        while (--numDimensions > 0) {
            res = new JArrayType(this, res);
        }
        return res;
    }

    /**
     * Like {@link #arrayType(JTypeMirror, int)}, with one dimension.
     *
     * @param component Component type
     *
     * @return An array type
     *
     * @throws IllegalArgumentException If the element type is a {@link JWildcardType},
     *                                  the null type, or {@link #NO_TYPE void}.
     * @throws NullPointerException     If the element type is null
     */
    public JArrayType arrayType(@NonNull JTypeMirror component) {
        return arrayType(component, null);
    }

    /** Trusted constructor. */
    private JArrayType arrayType(@NonNull JTypeMirror component, @Nullable JClassSymbol symbol) {
        checkArrayElement(component);
        return new JArrayType(this, component, symbol, HashTreePSet.empty());
    }


    private void checkArrayElement(@NonNull JTypeMirror element) {
        AssertionUtil.requireParamNotNull("elementType", element);

        if (element instanceof JWildcardType
            || element == NULL_TYPE
            || element == NO_TYPE) {
            throw new IllegalArgumentException("The type < " + element + " > is not a valid array element type");
        }
    }

    public JMethodSig sigOf(JExecutableSymbol methodSym) {
        return sigOf(methodSym, Substitution.EMPTY);
    }

    public JMethodSig sigOf(JExecutableSymbol methodSym, Substitution subst) {
        JClassType klass = (JClassType) declaration(methodSym.getEnclosingClass());
        return new ClassMethodSigImpl(klass.subst(subst), methodSym);
    }

    public JVariableSig.FieldSig sigOf(JTypeMirror decl, JFieldSymbol fieldSym) {
        return JVariableSig.forField(decl, fieldSym);
    }

    public JVariableSig sigOf(JClassType decl, JLocalVariableSymbol fieldSym) {
        return JVariableSig.forLocal(decl, fieldSym);
    }

    public JVariableSig sigOf(JClassType decl, JFormalParamSymbol fieldSym) {
        return JVariableSig.forLocal(decl, fieldSym);
    }


    /**
     * Builds a wildcard type with a single bound.
     *
     * <pre>{@code
     *
     * wildcard(true, T)      = ? extends T
     * wildcard(false, T)     = ? super T
     * wildcard(true, OBJECT) = ?
     *
     * }</pre>
     *
     * @param isUpperBound If true, this is an "extends" wildcard, otherwise a "super"
     * @param bound        Bound of the wildcard
     *
     * @return A wildcard
     *
     * @throws NullPointerException     If the bound is null
     * @throws IllegalArgumentException If the bound is a primitive type,
     *                                  or a wildcard type
     * @throws IllegalArgumentException If the bound is OBJECT and this
     *                                  is a lower-bounded wildcard (? super Object)
     */
    public JWildcardType wildcard(boolean isUpperBound, @NonNull JTypeMirror bound) {
        Objects.requireNonNull(bound, "Argument shouldn't be null");
        if (bound.isPrimitive() || bound instanceof JWildcardType) {
            throw new IllegalArgumentException("<" + bound + "> cannot be a wildcard bound");
        }
        return isUpperBound && bound == OBJECT ? UNBOUNDED_WILD
                                               : new WildcardTypeImpl(this, isUpperBound, bound, HashTreePSet.empty());
    }

    /**
     * Maps a type decl symbol to its shared representation. Eg this
     * maps the symbol for {@code int.class} to {@link #INT}. Only
     * non-generic types are cached.
     */
    private @Nullable JTypeMirror specialCache(JTypeDeclSymbol raw) {
        return sharedTypes.get(raw);
    }


    /**
     * Gets the primitive type identified by the given kind.
     *
     * @param kind Kind of primitive type
     *
     * @return A primitive type
     *
     * @throws NullPointerException if kind is null
     */
    public @NonNull JPrimitiveType getPrimitive(@NonNull PrimitiveTypeKind kind) {
        AssertionUtil.requireParamNotNull("kind", kind);
        return primitivesByKind.get(kind);
    }

    /**
     * The least upper bound, or "lub", of a set of reference types is
     * a shared supertype that is more specific than any other shared
     * supertype (that is, no other shared supertype is a subtype of the
     * least upper bound).
     *
     * @throws IllegalArgumentException If types is empty
     * @throws NullPointerException     If types is null
     */
    public JTypeMirror lub(Collection<? extends JTypeMirror> types) {
        return Lub.lub(this, types);
    }

    /**
     * Returns the greatest lower bound of the given set of types.
     * This is defined in JLS§5.1.10 (Capture Conversion):
     *
     * <pre>
     * glb(V1,...,Vm) = V1 &amp; ... &amp; Vm
     * glb(V) = V
     * </pre>
     *
     * <p>This may alter the components, so that:
     * <ul>
     * <li>No intersection type is a component: {@code ((A & B) & C) = (A & (B & C)) = (A & B & C)}
     * <li>No two types in the intersection are subtypes of one another
     * (the intersection is minimal): {@code A <: B => (A & B) = A}, in particular, {@code (A & A) = A}
     * <li>The intersection has a single component that is a
     * class, array, or type variable. If all components are interfaces,
     * then that component is {@link #OBJECT}.
     * </ul>
     *
     * <p>If after these transformations, only a single component remains,
     * then that is the returned type. Otherwise a {@link JIntersectionType}
     * is created. Note that the intersection may be unsatisfiable (eg {@code A[] & Runnable}),
     * but we don't attempt to minimize this to {@link #NULL_TYPE}.
     *
     * <p>See also JLS§4.9 (Intersection types).
     *
     * @throws IllegalArgumentException If some component is not a class, interface, array, or type variable
     * @throws IllegalArgumentException If there is more than one minimal class or array type
     * @throws IllegalArgumentException If types is empty
     * @throws NullPointerException     If types is null
     */
    public JTypeMirror glb(Collection<? extends JTypeMirror> types) {
        return Lub.glb(this, types);
    }

    // package-private
    JClassType erasedType(@NonNull JClassSymbol symbol) {
        JTypeMirror t = specialCache(symbol);
        if (t != null) {
            return (JClassType) t.getErasure();
        } else {
            return new ErasedClassType(this, symbol, HashTreePSet.empty());
        }
    }


    /**
     * Returns a new type variable for the given symbol. This is only
     * intended to be used by the implementor of {@link JTypeParameterSymbol}.
     */
    public JTypeVar newTypeVar(JTypeParameterSymbol symbol) {
        // note: here we don't pass the symbol's annotations. These stay on
        // the symbol as they can be visually noisy since they would be
        // repeated at each use-site
        return new TypeVarImpl.RegularTypeVar(this, symbol, HashTreePSet.empty());
    }

    private static final class NullType implements JTypeMirror {

        private final TypeSystem ts;

        NullType(TypeSystem ts) {
            this.ts = ts;
        }

        @Override
        public JTypeMirror withAnnotations(PSet<SymAnnot> newTypeAnnots) {
            return this;
        }

        @Override
        public PSet<SymAnnot> getTypeAnnotations() {
            return HashTreePSet.empty();
        }

        @Override
        public JTypeMirror subst(Function<? super SubstVar, ? extends @NonNull JTypeMirror> subst) {
            return this;
        }

        @Override
        public TypeSystem getTypeSystem() {
            return ts;
        }

        @Override
        public boolean isBottom() {
            return true;
        }

        @Override
        public @Nullable JClassSymbol getSymbol() {
            return null;
        }

        @Override
        public <T, P> T acceptVisitor(JTypeVisitor<T, P> visitor, P p) {
            return visitor.visitNullType(this, p);
        }

        @Override
        public String toString() {
            return "null";
        }
    }
}
