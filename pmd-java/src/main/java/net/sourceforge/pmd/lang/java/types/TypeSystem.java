/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import static java.util.Collections.emptyList;
import static net.sourceforge.pmd.util.CollectionUtil.immutableSetOf;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.internal.util.AssertionUtil;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JExecutableSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeParameterSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolResolver;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.SymbolFactory;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.asm.AsmSymbolResolver;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.reflect.ReflectedSymbols;
import net.sourceforge.pmd.lang.java.types.JPrimitiveType.PrimitiveTypeKind;
import net.sourceforge.pmd.lang.java.types.internal.infer.JInferenceVar;

/**
 * Root context object for type analysis. Type systems own a {@link SymbolFactory},
 * which creates and caches symbols. Methods of this class promote symbols
 * to types, and compose types together. {@link TypeOps} and {@link TypeConversion}
 * have some more operations on types.
 *
 * <p>Some special types are presented as constant fields, eg {@link #OBJECT}
 * or {@link #NULL_TYPE}. These are always comparable by reference.
 */
public final class TypeSystem {

    /** Top type of the reference type system. */
    public final JClassType OBJECT;

    /**
     * The bottom type of the reference type system. This is named
     * the <i>null type</i> in the JLS and is not denotable in Java
     * programs.
     *
     * <p>This implementation uses this as the type of the 'null' literal.
     */
    public final JTypeMirror NULL_TYPE = new NullType();


    // primitives
    public final JPrimitiveType BOOLEAN;
    public final JPrimitiveType CHAR;
    public final JPrimitiveType BYTE;
    public final JPrimitiveType SHORT;
    public final JPrimitiveType INT;
    public final JPrimitiveType LONG;
    public final JPrimitiveType FLOAT;
    public final JPrimitiveType DOUBLE;

    /**
     * The set of all primitive types. See {@link #getPrimitive(PrimitiveTypeKind)}.
     */
    public final Set<JPrimitiveType> allPrimitives;
    private final EnumMap<PrimitiveTypeKind, JPrimitiveType> primitivesByKind;

    /**
     * A constant to represent the normal absence of a type. The
     * primitive {@code void.class} represents that type, and this
     * is the return type of a void method.
     *
     * <p>Note that the type of the class literal {@code void.class}
     * is {@code Class<java.lang.Void>}, not NO_TYPE.
     */
    public final JTypeMirror NO_TYPE;

    /**
     * A constant to represent an unresolved type. This means, that resolution
     * was attempted but failed and shouldn't be tried again.
     */
    public final JTypeMirror UNRESOLVED_TYPE;

    /**
     * Sentinel value for an unresolved method. This type corresponds to
     * a method declaration in the type {@link #UNRESOLVED_TYPE},
     * returning {@link #UNRESOLVED_TYPE}.
     */
    // TODO it doesn't need to be declared in UNRESOLVED_TYPE
    public final JMethodSig UNRESOLVED_METHOD = new UnresolvedMethodSig(this);

    /**
     * A constant to represent a typing error. This would have been
     * reported by a compiler.
     */
    public final JTypeMirror ERROR_TYPE = new SentinelType(this, "/*error*/");

    /*
     * Common, non-special types.
     */

    /** The unbounded wildcard, "?". */
    public final JWildcardType UNBOUNDED_WILD;

    // array supertypes
    public final JClassType CLONEABLE;
    public final JClassType SERIALIZABLE;

    /**
     * This is the boxed type of {@code Void.class}, not to be confused with
     * {@code void.class}, which in this framework is represented by
     * {@link #NO_TYPE}.
     */
    public final JClassType BOXED_VOID;


    private final SymbolFactory symbolFactory;

    /** Contains special types, that must be shared to be comparable by reference. */
    private final Map<JTypeDeclSymbol, JTypeMirror> sharedTypes;
    // test only
    final AsmSymbolResolver resolver;

    /**
     * Builds a new type system. Its public fields will be initialized
     * with fresh types, unrelated to other types.
     *
     * @param bootstrapResourceLoader Classloader used to resolve class files
     *                                to populate the fields of the new type
     *                                system
     */
    public TypeSystem(ClassLoader bootstrapResourceLoader) {
        this.resolver = new AsmSymbolResolver(this, bootstrapResourceLoader);
        this.symbolFactory = new SymbolFactory(this);

        // initialize primitives. their constructor also initializes their box + box erasure

        BOOLEAN = createPrimitive(PrimitiveTypeKind.BOOLEAN, Boolean.class);
        CHAR = createPrimitive(PrimitiveTypeKind.CHAR, Character.class);
        BYTE = createPrimitive(PrimitiveTypeKind.BYTE, Byte.class);
        SHORT = createPrimitive(PrimitiveTypeKind.SHORT, Short.class);
        INT = createPrimitive(PrimitiveTypeKind.INT, Integer.class);
        LONG = createPrimitive(PrimitiveTypeKind.LONG, Long.class);
        FLOAT = createPrimitive(PrimitiveTypeKind.FLOAT, Float.class);
        DOUBLE = createPrimitive(PrimitiveTypeKind.DOUBLE, Double.class);

        // this relies on the fact that setOf always returns immutable sets
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

        JClassSymbol unresolvedTypeSym = symbolFactory.makeUnresolvedReference("/*unresolved*/", 0);
        UNRESOLVED_TYPE = new SentinelType(this, "/*unresolved*/", unresolvedTypeSym);

        JClassSymbol primitiveVoidSym = ReflectedSymbols.getClassSymbol(symbolFactory, void.class);
        assert primitiveVoidSym != null : "void";
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
        shared.put(unresolvedTypeSym, UNRESOLVED_TYPE);

        for (JPrimitiveType prim : allPrimitives) {
            // primitives have a special implementation for their box
            shared.put(prim.getSymbol(), prim);
            shared.put(prim.box().getSymbol(), prim.box());
        }

        // make it really untouchable
        this.sharedTypes = Collections.unmodifiableMap(new HashMap<>(shared));

        UNBOUNDED_WILD = new WildcardTypeImpl(this, true, OBJECT);
    }

    @SuppressWarnings("IncompleteCopyConstructor")
    private TypeSystem(TypeSystem other) {
        // create a new symbol factory, with an independent cache.
        this.symbolFactory = new SymbolFactory(this);

        this.resolver = other.resolver;
        this.sharedTypes = other.sharedTypes;

        this.OBJECT = other.OBJECT;
        this.BOOLEAN = other.BOOLEAN;
        this.CHAR = other.CHAR;
        this.BYTE = other.BYTE;
        this.SHORT = other.SHORT;
        this.INT = other.INT;
        this.LONG = other.LONG;
        this.FLOAT = other.FLOAT;
        this.DOUBLE = other.DOUBLE;
        this.NO_TYPE = other.NO_TYPE;
        this.allPrimitives = other.allPrimitives;
        this.primitivesByKind = other.primitivesByKind;

        this.UNRESOLVED_TYPE = other.UNRESOLVED_TYPE;
        this.UNBOUNDED_WILD = other.UNBOUNDED_WILD;
        this.CLONEABLE = other.CLONEABLE;
        this.SERIALIZABLE = other.SERIALIZABLE;
        this.BOXED_VOID = other.BOXED_VOID;
    }

    /**
     * Returns a new, distinct type system, which caches symbols independently
     * of this one. Constants such as {@link #INT} or {@link #OBJECT} are
     * shared between this type system and all the sub-systems it spawns.
     *
     * <p>This provides a simple mechanism to localize cached symbols.
     * Each file gets its own "scope", which, when garbage-collected,
     * reclaims all symbols cached for the file. This also avoids concurrency
     * issues.
     *
     * @return A new type system, based on this one.
     */
    public TypeSystem newScope() {
        return new TypeSystem(this);
    }

    /**
     * Returns the symbol factory associated with this type system.
     * This is internal API, symbols are low-level abstractions that
     * should not be created manually.
     */
    @InternalApi
    public SymbolFactory symbols() {
        return symbolFactory;
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
        JClassType nonErased = new ClassTypeImpl(this, sym, emptyList(), false);
        shared.put(sym, nonErased);
        return nonErased;
    }

    private JClassSymbol getBootStrapSymbol(Class<?> clazz) {
        AssertionUtil.requireParamNotNull("clazz", clazz);
        JClassSymbol sym = resolver.resolveClassFromBinaryName(clazz.getName());
        return Objects.requireNonNull(sym, "sym");
    }

    @NonNull
    private JPrimitiveType createPrimitive(PrimitiveTypeKind kind, Class<?> box) {
        return new JPrimitiveType(this, kind, new PrimitiveSymbol(this, kind), getBootStrapSymbol(box));
    }


    // type creation routines

    /**
     * Returns the class symbol for the given reflected class. This asks
     * the classloader of this type system, but never returns null. Returns
     * null if the parameter is null.
     *
     * @param clazz Class
     */
    @Nullable
    public JClassSymbol getClassSymbol(@Nullable Class<?> clazz) {
        if (clazz == null) {
            return null;
        } else if (clazz.isPrimitive()) {
            PrimitiveTypeKind kind = PrimitiveTypeKind.fromName(clazz.getName());
            if (kind == null) { // void
                return (JClassSymbol) NO_TYPE.getSymbol();
            }
            return getPrimitive(kind).getSymbol();
        } else if (clazz.isArray()) {
            return symbols().makeArraySymbol(getClassSymbol(clazz.getComponentType()));
        }

        JClassSymbol classLoaderPreferred = resolver.resolveClassFromBinaryName(clazz.getName());
        if (classLoaderPreferred != null) {
            return classLoaderPreferred;
        }
        return ReflectedSymbols.getClassSymbol(symbols(), clazz);
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
     * returned. Note that the component type will always be erased;
     * creating a generic array type should instead be done with
     * {@link #arrayType(JTypeMirror, int)}.
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
     * @throws NullPointerException if the symbol is null
     */
    public JTypeMirror typeOf(JTypeDeclSymbol symbol, boolean isErased) {
        Objects.requireNonNull(symbol, "Argument shouldn't be null");

        // takes care of primitives, and constants like OBJECT or UNRESOLVED_TYPE
        JTypeMirror common = specialCache(symbol);
        if (common != null) {
            return common;
        }

        if (symbol instanceof JClassSymbol) {
            JClassSymbol classSym = (JClassSymbol) symbol;
            if (classSym.isArray()) {
                // generic array types are represented by a special
                // type in the j.l.reflect API, so the component is
                // also raw
                // fixme this is wrong:
                //  genArr = ts.array(tvar, 1);
                //  ts.typeOf(genArr.symbol(), false) != genArr
                JTypeMirror component = rawType(classSym.getArrayComponent());
                return arrayType(component, 1);
            } else {
                return new ClassTypeImpl(this, classSym, emptyList(), !isErased);
            }
        } else if (symbol instanceof JTypeParameterSymbol) {
            return ((JTypeParameterSymbol) symbol).getTypeMirror();
        }
        throw new AssertionError("Uncategorized type symbol " + symbol.getClass() + ": " + symbol);
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
    public JTypeMirror rawType(JTypeDeclSymbol klass) {
        return typeOf(klass, true);
    }

    public JTypeMirror declaration(JClassSymbol klass) {
        return typeOf(klass, false);
    }


    @NonNull
    public JTypeMirror parameterise(JClassSymbol klass, List<? extends JTypeMirror> typeArgs) {
        Objects.requireNonNull(klass, "Null class symbol");
        Objects.requireNonNull(typeArgs, "Null type arguments, use an empty list!");

        if (!klass.isUnresolved() && !typeArgs.isEmpty() && klass.getTypeParameterCount() != typeArgs.size()) {
            throw new IllegalArgumentException("Cannot parameterize " + klass + " with " + typeArgs);
        } else if (typeArgs.isEmpty()) {
            return rawType(klass);
        }

        // if the type arguments are mismatched, the constructor will throw
        return new ClassTypeImpl(this, klass, new ArrayList<>(typeArgs), false);
    }


    /**
     * Creates a new array type from an arbitrary element type.
     *
     * <pre>{@code
     * arrayType(T, 0)          = T
     * arrayType(T, 1)          = T[]
     * arrayType(T, 3)          = T[][][]
     * arrayType(T[], 2)        = T[][][]
     * arrayType(ERROR_TYPE, _) = ERROR_TYPE
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
        AssertionUtil.requireParamNotNull("elementType", element);

        if (element instanceof JWildcardType
            || element == NULL_TYPE
            || element == NO_TYPE) {
            throw new IllegalArgumentException("The type < " + element + " > is not a valid array element type ");
        }

        if (numDimensions == 0 || element == ERROR_TYPE) {
            return element;
        }

        JArrayType res = new ArrayTypeImpl(this, element);
        while (--numDimensions > 0) {
            res = new ArrayTypeImpl(this, res);
        }
        return res;
    }

    public JMethodSig sigOf(JExecutableSymbol methodSym) {
        return sigOf(methodSym, Substitution.EMPTY);
    }

    public JMethodSig sigOf(JExecutableSymbol methodSym, Substitution subst) {
        JClassType klass = (JClassType) declaration(methodSym.getEnclosingClass());
        return new ClassMethodSigImpl(klass.subst(subst), methodSym);
    }

    /**
     * Builds an intersection type for the specified component types.
     * This does not necessarily return a {@link JIntersectionType}.
     *
     * @param types Types to intersect
     *
     * @return An intersection type
     *
     * @throws NullPointerException     If the collection is null
     * @throws IllegalArgumentException If the collection is empty
     */
    public JTypeMirror intersect(Collection<? extends JTypeMirror> types) {
        if (types.isEmpty()) {
            throw new IllegalArgumentException("Cannot intersect zero types");
        } else if (types.size() == 1) {
            return types.iterator().next();
        }
        return new IntersectionTypeImpl(this, new ArrayList<>(types));
    }


    /**
     * Builds a wildcard type with a single bound.
     *
     * <pre>{@code
     *
     * wildcard(true, T)      = ? extends T
     * wildcard(false, T)     = ? super T
     * wildcard(true, OBJECT) = ?
     * wildcard(_, ERROR_TYPE) = ERROR_TYPE
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
    public JTypeMirror wildcard(boolean isUpperBound, @NonNull JTypeMirror bound) {
        Objects.requireNonNull(bound, "Argument shouldn't be null");
        if (bound == ERROR_TYPE) {
            return bound;
        }
        if (bound.isPrimitive() || bound instanceof JWildcardType) {
            throw new IllegalArgumentException("<" + bound + "> cannot be a wildcard bound");
        }
        return isUpperBound && bound == OBJECT ? UNBOUNDED_WILD
                                               : new WildcardTypeImpl(this, isUpperBound, bound);
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
    @NonNull
    public JPrimitiveType getPrimitive(@NonNull PrimitiveTypeKind kind) {
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
     * <blockquote>
     * glb(V1,...,Vm) = V1 & ... & Vm
     * glb(V) = V
     * </blockquote>
     *
     * @throws IllegalArgumentException If some component is not a class, array, or wildcard type
     * @throws IllegalArgumentException If there is more than one minimal class or array type
     * @throws IllegalArgumentException If types is empty
     * @throws NullPointerException     If types is null
     */
    public JTypeMirror glb(Collection<? extends JTypeMirror> types) {
        if (types.isEmpty()) {
            throw new IllegalArgumentException("Cannot compute GLB of empty set");
        }


        ArrayList<JTypeMirror> list = new ArrayList<>(types.size());

        for (JTypeMirror type : types) {
            // flatten intersections: (A & (B & C)) => (A & B & C)
            if (type instanceof JIntersectionType) {
                list.addAll(((JIntersectionType) type).getComponents());
            } else {
                list.add(type);
            }
        }


        JTypeMirror ck = OBJECT; // Ck is a class type

        for (ListIterator<JTypeMirror> iterator = list.listIterator(); iterator.hasNext(); ) {
            JTypeMirror ci = iterator.next();

            if (ci.isPrimitive() || ci instanceof JWildcardType || ci instanceof JIntersectionType) {
                throw new IllegalArgumentException("Bad intersection type component: " + ci + " in " + types);
            }

            if (!isPossiblyAnInterface(ci)) {
                // either Ci is an array, or Ci is a class
                // Ci is not unresolved

                if (ci.isSubtypeOf(ck)) {
                    ck = ci; // Ci is more specific than Ck
                    iterator.remove(); // remove bound
                } else if (ck.isSubtypeOf(ci)) {
                    // then our Ck is already more specific than Ci
                    iterator.remove();
                } else {
                    throw new IllegalArgumentException(
                        "Bad intersection, unrelated class types " + ci + " and " + ck + " in " + types);
                }
            } else if (!(ci instanceof JInferenceVar) && ck.isSubtypeOf(ci)) {
                // then our Ck is already more specific than Ci
                iterator.remove();
            }
        }

        if (list.isEmpty()) {
            return ck;
        }

        if (ck != OBJECT) {
            // readd ck as first component
            list.add(0, ck);
        }

        if (list.size() == 1) {
            return list.get(0);
        }

        if (ck instanceof JTypeVar) {
            return new IntersectionTypeImpl(this, list);
        }

        // We assume there cannot be an array type here. Why?
        // In well-formed java programs an array type in a GLB can only occur in the following situation
        //
        // class C<T extends B1 & .. & Bn>      // nota: the Bi cannot be array types
        //
        // Somewhere: C<? extends Arr[]>

        // And capture would merge the bounds of the wildcard and of the tvar
        // into Arr[] & B1 & .. & Bn
        // Now the C<? ...> would only typecheck if Arr[] <: Bi forall i
        // (Note that this means, that Bi in { Serializable, Cloneable, Object })

        // This means, that the loop above would find Ck = Arr[], and delete all Bi, since Ck <: Bi
        // So in the end, we would return Arr[] alone, not create an intersection
        // TODO this is order dependent: Arr[] & Serializable is ok, but Serializable & Arr[] is not
        //   Possibly use TypeOps::mostSpecific to merge them
        assert ck instanceof JClassType : "Weird intersection involving multiple array types? " + list;

        return new IntersectionTypeImpl.MinimalIntersection(this, (JClassType) ck, list);
    }


    private boolean isPossiblyAnInterface(JTypeMirror ci) {
        return ci.isInterface()
            || ci instanceof JInferenceVar
            || (ci.getSymbol() != null && ci.getSymbol().isUnresolved());
    }

    // package-private
    JClassType erasedType(JClassSymbol symbol) {
        JTypeMirror t = specialCache(symbol);
        if (t != null) {
            return (JClassType) t.getErasure();
        } else {
            return new ErasedClassType(this, symbol);
        }
    }


    /**
     * Returns a new type variable for the given symbol. This is only
     * intended to be used by the implementor of {@link JTypeParameterSymbol}.
     */
    public JTypeVar.FreshTypeVar newTypeVar(JTypeParameterSymbol symbol) {
        return new TypeVarImpl(this, symbol);
    }

    private class NullType implements JTypeMirror {

        @Override
        public JTypeMirror subst(Function<? super SubstVar, ? extends @NonNull JTypeMirror> subst) {
            return this;
        }

        @Override
        public TypeSystem getTypeSystem() {
            return TypeSystem.this;
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
