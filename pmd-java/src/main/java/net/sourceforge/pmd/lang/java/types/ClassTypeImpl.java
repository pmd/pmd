/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import static net.sourceforge.pmd.util.CollectionUtil.emptyList;
import static net.sourceforge.pmd.util.CollectionUtil.map;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.pcollections.HashTreePSet;
import org.pcollections.PSet;

import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JExecutableSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymAnnot;
import net.sourceforge.pmd.lang.java.symbols.table.internal.SuperTypesEnumerator;
import net.sourceforge.pmd.lang.java.types.JVariableSig.FieldSig;
import net.sourceforge.pmd.util.CollectionUtil;

class ClassTypeImpl implements JClassType {
    private final @Nullable JClassType enclosingType;

    private final JClassSymbol symbol;
    private final TypeSystem ts;
    private final List<JTypeMirror> typeArgs;
    private final PSet<SymAnnot> typeAnnotations;
    private final TypeGenericity genericity;

    private JClassType superClass;
    private List<JClassType> interfaces;

    private Substitution subst;

    // Cache the hash. Instances are super often put in sets or used as
    // map keys and hash computation is expensive since we recurse in the
    // type arguments. This was confirmed through profiling
    private int hash = 0;

    /**
     * @param symbol   Erased type
     * @param typeArgs Type arguments of this parameterization. If empty
     *                 and isDecl is false, this will represent a raw type.
     *                 If empty and isDecl is true, this will represent a
     *                 generic type declaration.
     * @param isRaw    Choose a bias towards generic type declaration or raw
     *                 type. If the [rawType] param has no type parameters,
     *                 then this parameter makes *no difference*.
     *
     * @throws IllegalArgumentException if the typeArgs don't have the same length
     *                                  as the type's type parameters
     * @throws IllegalArgumentException if any type argument is of a primitive type.
     */
    ClassTypeImpl(TypeSystem ts, JClassSymbol symbol, List<JTypeMirror> typeArgs, boolean isRaw, PSet<SymAnnot> typeAnnotations) {
        this(ts, null, symbol, typeArgs, typeAnnotations, isRaw);
    }

    private ClassTypeImpl(TypeSystem ts, JClassType enclosing, JClassSymbol symbol, List<JTypeMirror> typeArgs, PSet<SymAnnot> typeAnnotations, boolean isRaw) {
        this.typeAnnotations = typeAnnotations;
        validateParams(enclosing, symbol, typeArgs);

        this.ts = ts;
        this.symbol = symbol;
        this.typeArgs = typeArgs;

        this.enclosingType = enclosing != null
                             ? enclosing
                             : makeEnclosingOf(symbol);

        this.genericity = computeGenericity(isRaw);

    }

    // Special ctor for boxed primitives and other specials that are built
    // during initialization of TypeSystem. This cannot call JClassSymbol#isGeneric,
    // because that would trigger parsing of the class file while the type system is not ready
    protected ClassTypeImpl(TypeSystem ts, JClassSymbol symbol, PSet<SymAnnot> typeAnnotations) {
        this.typeAnnotations = typeAnnotations;
        this.ts = ts;
        this.symbol = symbol;
        this.typeArgs = emptyList();
        this.enclosingType = null;
        this.genericity = TypeGenericity.NON_GENERIC;
    }

    private @NonNull TypeGenericity computeGenericity(boolean isRaw) {
        boolean isGeneric = symbol.isGeneric();
        if (enclosingType != null && enclosingType.isRaw()) {
            return TypeGenericity.RAW;
        } else if (typeArgs.isEmpty()) {
            if (isGeneric && isRaw) {
                return TypeGenericity.RAW;
            } else if (isGeneric) {
                return TypeGenericity.GENERIC_TYPEDECL;
            } else {
                return TypeGenericity.NON_GENERIC;
            }
        } else {
            return TypeGenericity.GENERIC_PARAMETERIZED;
        }
    }

    private JClassType makeEnclosingOf(JClassSymbol sym) {
        if (Modifier.isStatic(sym.getModifiers())) {
            return null;
        }

        JClassSymbol enclosing = sym.getEnclosingClass();
        if (enclosing == null) {
            return null;
        }
        // this means, that all enclosing types of a raw type are erased.
        if (this.hasErasedSuperTypes()) {
            return ts.erasedType(enclosing);
        }
        return (JClassType) ts.typeOf(enclosing, false);
    }

    @Override
    public TypeSystem getTypeSystem() {
        return ts;
    }

    @Override
    public PSet<SymAnnot> getTypeAnnotations() {
        return typeAnnotations;
    }

    @Override
    public JClassType withAnnotations(PSet<SymAnnot> newTypeAnnots) {
        if (newTypeAnnots.isEmpty() && this.typeAnnotations.isEmpty()) {
            return this;
        }
        return new ClassTypeImpl(ts, enclosingType, symbol, typeArgs, newTypeAnnots, isRaw());
    }

    @Override
    public List<JTypeVar> getFormalTypeParams() {
        return symbol.getTypeParameters();
    }

    @Override
    public Substitution getTypeParamSubst() {
        if (subst == null) {
            Substitution enclSubst = getEnclosingType() == null
                                     ? Substitution.EMPTY
                                     : getEnclosingType().getTypeParamSubst();
            subst = enclSubst.andThen(localSubst());
        }
        return subst;
    }

    private Substitution localSubst() {
        if (hasErasedSuperTypes()) {
            return Substitution.erasing(getFormalTypeParams());
        } else if (isGenericTypeDeclaration() || !isGeneric()) {
            return Substitution.EMPTY;
        } else {
            return Substitution.mapping(getFormalTypeParams(), getTypeArgs());
        }
    }

    /**
     * Given a type appearing in a member, and the given substitution
     */
    static JTypeMirror eraseToRaw(JTypeMirror t, Substitution typeSubst) {
        if (TypeOps.mentionsAny(t, typeSubst.getMap().keySet())) {
            return t.getErasure();
        } else {
            // This type does not depend on any of the type variables to erase.
            return t;
        }
    }

    /**
     * Given a type appearing in a member of the given owner, erase
     * the member type if the owner is raw. The type needs not be erased
     * if it is generic but does not mention any of the type variables
     * to erase.
     */
    static JTypeMirror maybeEraseMemberType(JClassType owner, JTypeMirror t) {
        if (owner.isRaw() && TypeOps.mentionsAny(t, owner.getTypeParamSubst().getMap().keySet())) {
            return t.getErasure();
        } else {
            // This type does not depend on any of the type variables to erase.
            return t;
        }
    }

    static List<JTypeMirror> maybeEraseMemberType(JClassType owner, List<JTypeMirror> ts) {
        if (owner.isRaw()) {
            return map(ts, t -> maybeEraseMemberType(owner, t));
        } else {
            // This type does not depend on any of the type variables to erase.
            return ts;
        }
    }

    @Override
    public final JClassType selectInner(JClassSymbol symbol, List<? extends JTypeMirror> targs, PSet<SymAnnot> typeAnnotations) {
        return new ClassTypeImpl(ts,
                                 this,
                                 symbol,
                                 CollectionUtil.defensiveUnmodifiableCopy(targs),
                                 typeAnnotations,
                                 isRaw());
    }

    @Override
    public final boolean isRaw() {
        return genericity == TypeGenericity.RAW;
    }

    @Override
    public final boolean isGenericTypeDeclaration() {
        return genericity == TypeGenericity.GENERIC_TYPEDECL;
    }

    @Override
    public final boolean isParameterizedType() {
        return genericity == TypeGenericity.GENERIC_PARAMETERIZED;
    }

    @Override
    public final boolean isGeneric() {
        return genericity != TypeGenericity.NON_GENERIC;
    }

    @Override
    public JClassType getGenericTypeDeclaration() {
        if (isGenericTypeDeclaration() || !isGeneric()) {
            return this;
        }
        return new ClassTypeImpl(ts, symbol, emptyList(), false, typeAnnotations);
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<JTypeMirror> getTypeArgs() {
        return isGenericTypeDeclaration() ? (List) getFormalTypeParams() : typeArgs;
    }

    @Override
    public @Nullable JClassType getEnclosingType() {
        return enclosingType;
    }

    @Override
    public boolean hasErasedSuperTypes() {
        return isRaw();
    }

    @Override
    public JClassType getErasure() {
        if ((!isGeneric() || isRaw()) && enclosingType == null) {
            return this;
        }

        return new ErasedClassType(ts, symbol, typeAnnotations);
    }

    @Override
    public JClassType withTypeArguments(List<? extends JTypeMirror> typeArgs) {
        if (enclosingType != null) {
            return enclosingType.selectInner(this.symbol, typeArgs, this.typeAnnotations);
        }

        int expected = symbol.getTypeParameterCount();
        if (expected == 0 && typeArgs.isEmpty() && this.typeArgs.isEmpty()) {
            return this; // non-generic
        }
        return new ClassTypeImpl(ts, symbol, CollectionUtil.defensiveUnmodifiableCopy(typeArgs), true, typeAnnotations);
    }

    @Override
    public @Nullable JClassType getSuperClass() {
        if (superClass == null && !isTop()) {
            if (hasErasedSuperTypes()) {
                superClass = ts.erasedType(symbol.getSuperclass());
            } else {
                superClass = symbol.getSuperclassType(getTypeParamSubst());
            }
        }
        return superClass;
    }

    @Override
    public List<JClassType> getSuperInterfaces() {
        if (interfaces == null) {
            if (hasErasedSuperTypes()) {
                interfaces = map(symbol.getSuperInterfaces(), ts::erasedType);
            } else {
                interfaces = symbol.getSuperInterfaceTypes(getTypeParamSubst());
            }
        }
        return interfaces;
    }

    @Override
    public List<FieldSig> getDeclaredFields() {
        return CollectionUtil.map(symbol.getDeclaredFields(), it -> JVariableSig.forField(this, it));
    }

    @Override
    public List<JClassType> getDeclaredClasses() {
        return CollectionUtil.map(symbol.getDeclaredClasses(), this::getDeclaredClass);
    }

    private JClassType getDeclaredClass(JClassSymbol inner) {
        if (Modifier.isStatic(inner.getModifiers())) {
            return new ClassTypeImpl(ts, null, inner, emptyList(), typeAnnotations, isRaw());
        } else {
            return selectInner(inner, emptyList());
        }
    }

    @Override
    public @Nullable FieldSig getDeclaredField(String simpleName) {
        @Nullable JFieldSymbol declaredField = symbol.getDeclaredField(simpleName);
        if (declaredField != null) {
            return JVariableSig.forField(this, declaredField);
        }
        return null;
    }

    @Override
    public @Nullable JClassType getDeclaredClass(String simpleName) {
        JClassSymbol declaredClass = symbol.getDeclaredClass(simpleName);
        if (declaredClass != null) {
            if (Modifier.isStatic(declaredClass.getModifiers())) {
                return new ClassTypeImpl(ts, null, declaredClass, emptyList(), HashTreePSet.empty(), isRaw());
            } else {
                return selectInner(declaredClass, emptyList());
            }
        }
        return null;
    }

    @Override
    public List<JMethodSig> getConstructors() {
        return map(
            symbol.getConstructors(),
            it -> new ClassMethodSigImpl(this, it)
        );
    }

    @Override
    public Stream<JMethodSig> streamMethods(Predicate<? super JMethodSymbol> prefilter) {
        return SuperTypesEnumerator.ALL_SUPERTYPES_INCLUDING_SELF.stream(this)
                                                                 .flatMap(sup -> sup.streamDeclaredMethods(prefilter));
    }

    @Override
    public Stream<JMethodSig> streamDeclaredMethods(Predicate<? super JMethodSymbol> prefilter) {
        return getSymbol().getDeclaredMethods().stream()
                          .filter(prefilter)
                          .map(m -> new ClassMethodSigImpl(this, m));
    }

    @Override
    public @Nullable JMethodSig getDeclaredMethod(JExecutableSymbol sym) {
        if (sym.getEnclosingClass().equals(getSymbol())) {
            return new ClassMethodSigImpl(this, sym);
        }
        return null;
    }


    @Override
    public final @NonNull JClassSymbol getSymbol() {
        return symbol;
    }

    @Override
    public final boolean isTop() {
        return this.getSymbol().equals(ts.OBJECT.getSymbol()); // NOPMD CompareObjectsWithEquals
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof JClassType)) {
            return false;
        }

        JClassType that = (JClassType) o;
        return TypeOps.isSameType(this, that);
    }

    @Override
    public int hashCode() {
        if (hash == 0) { // hash collision is harmless
            hash = Objects.hash(typeArgs, symbol);
        }
        return hash;
    }

    @Override
    public String toString() {
        return TypePrettyPrint.prettyPrint(this);
    }

    private static void validateParams(JClassType enclosing, JClassSymbol symbol, List<JTypeMirror> typeArgs) {
        Objects.requireNonNull(symbol, "Symbol shouldn't be null");

        checkUserEnclosingTypeIsOk(enclosing, symbol);

        if (!typeArgsAreOk(symbol, typeArgs)) {
            throw invalidTypeArgs(symbol, typeArgs);
        }

        for (JTypeMirror arg : typeArgs) {
            checkTypeArg(symbol, typeArgs, arg);
        }
    }

    protected static @NonNull IllegalArgumentException invalidTypeArgs(JClassSymbol symbol, List<? extends JTypeMirror> typeArgs) {
        return new IllegalArgumentException("Cannot parameterize " + symbol + " with " + typeArgs
                                                + ", expecting  " + symbol.getTypeParameterCount()
                                                + " type arguments");
    }

    private static void checkTypeArg(JClassSymbol symbol, List<JTypeMirror> typeArgs, JTypeMirror arg) {
        if (arg == null) {
            throw new IllegalArgumentException("Null type argument for " + symbol + " in " + typeArgs);
        } else if (arg.isPrimitive()) {
            throw new IllegalArgumentException("Primitive type argument for " + symbol + " in " + typeArgs);
        }
    }

    private static boolean typeArgsAreOk(JClassSymbol symbol, List<? extends JTypeMirror> typeArgs) {
        return typeArgs.isEmpty() // always ok (raw/ decl/ non-generic)
            || symbol.isUnresolved()
            || symbol.getTypeParameterCount() == typeArgs.size();
    }

    private static void checkUserEnclosingTypeIsOk(@Nullable JClassType enclosing, JClassSymbol symbol) {
        // TODO all enclosing types should be raw, or all should be parameterized

        if (symbol.isUnresolved() || enclosing != null && enclosing.getSymbol().isUnresolved()) {
            return;
        }

        if (enclosing != null) {
            if (Modifier.isStatic(symbol.getModifiers())) {
                throw new IllegalArgumentException("Cannot select *static* type " + symbol + " inside " + enclosing);
            } else if (!enclosing.getSymbol().equals(symbol.getEnclosingClass())) {
                throw new IllegalArgumentException("Cannot select type " + symbol + " inside " + enclosing);
            }
        }
    }

    private enum TypeGenericity {
        RAW,
        GENERIC_TYPEDECL,
        GENERIC_PARAMETERIZED,
        NON_GENERIC
    }
}
