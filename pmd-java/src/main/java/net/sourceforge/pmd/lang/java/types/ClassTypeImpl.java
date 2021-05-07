/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import static net.sourceforge.pmd.util.CollectionUtil.map;

import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JExecutableSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.table.internal.SuperTypesEnumerator;
import net.sourceforge.pmd.lang.java.types.JVariableSig.FieldSig;
import net.sourceforge.pmd.util.CollectionUtil;

class ClassTypeImpl implements JClassType {

    private final @Nullable JClassType enclosingType;

    private final JClassSymbol symbol;
    private final TypeSystem ts;
    private final List<JTypeMirror> typeArgs;
    private final boolean isDecl;

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
     * @param isDecl   Choose a bias towards generic type declaration or raw
     *                 type. If the [rawType] param has no type parameters,
     *                 then this parameter makes *no difference*.
     *
     * @throws IllegalArgumentException if the typeArgs don't have the same length
     *                                  as the type's type parameters
     * @throws IllegalArgumentException if any type argument is of a primitive type.
     */
    ClassTypeImpl(TypeSystem ts, JClassSymbol symbol, List<JTypeMirror> typeArgs, boolean isDecl) {
        this(ts, null, symbol, typeArgs, isDecl);
    }

    private ClassTypeImpl(TypeSystem ts, JClassType enclosing, JClassSymbol symbol, List<JTypeMirror> typeArgs, boolean isDecl) {
        validateParams(enclosing, symbol, typeArgs);

        this.ts = ts;
        this.symbol = symbol;
        this.typeArgs = typeArgs;
        this.isDecl = isDecl;

        this.enclosingType = enclosing != null
                             ? enclosing
                             : makeEnclosingOf(symbol);
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

    static JTypeMirror eraseToRaw(JTypeMirror m, Substitution typeSubst) {
        if (TypeOps.mentionsAny(m, typeSubst.getMap().keySet())) {
            return m.getErasure();
        } else {
            // less brutal than erasure,
            // some parameterized types should be kept, if they don't depend
            // on type parameters of this method
            return m.subst(typeSubst);
        }
    }

    @Override
    public JClassType selectInner(JClassSymbol symbol, List<? extends JTypeMirror> targs) {
        return new ClassTypeImpl(ts,
                                 this,
                                 symbol,
                                 CollectionUtil.defensiveUnmodifiableCopy(targs),
                                 this.isDecl);
    }

    @Override
    public boolean isRaw() {
        return !isDecl && isGeneric() && typeArgs.isEmpty()
            || getEnclosingType() != null && getEnclosingType().isRaw();
    }

    @Override
    public boolean isGenericTypeDeclaration() {
        return isDecl && isGeneric() && typeArgs.isEmpty();
    }

    @Override
    public boolean isParameterizedType() {
        return isGeneric() && !typeArgs.isEmpty();
    }

    @Override
    public boolean isGeneric() {
        return symbol.isGeneric();
    }

    @Override
    public JClassType getGenericTypeDeclaration() {
        return isGeneric() ? withTypeArguments(getFormalTypeParams()) : this;
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

        return new ErasedClassType(ts, symbol);
    }

    @Override
    public JClassType withTypeArguments(List<? extends JTypeMirror> typeArgs) {
        if (enclosingType != null) {
            return enclosingType.selectInner(symbol, typeArgs);
        }

        int expected = symbol.getTypeParameterCount();
        if (expected == 0 && typeArgs.isEmpty() && this.typeArgs.isEmpty()) {
            return this; // non-generic
        }
        return new ClassTypeImpl(ts, symbol, CollectionUtil.defensiveUnmodifiableCopy(typeArgs), false);
    }

    @Override
    public @Nullable JClassType getSuperClass() {
        if (superClass == null) {
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
            return new ClassTypeImpl(ts, null, inner, Collections.emptyList(), true);
        } else {
            return selectInner(inner, Collections.emptyList());
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
                return new ClassTypeImpl(ts, null, declaredClass, Collections.emptyList(), true);
            } else {
                return selectInner(declaredClass, Collections.emptyList());
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


    public int getModifiers() {
        return symbol.getModifiers();
    }

    @Override
    public @NonNull JClassSymbol getSymbol() {
        return symbol;
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
            // fixme relax this
            //  This will throw if the symbol is unresolved and was
            //  resolved through AsmSymbolResolver (ie, a missing dependency
            //  in classpath, found in a signature of some ASM class symbol member).
            //  Currently the AST symbol impl tries to patch unresolved symbols by
            //  making the number of type params flexible. But this does not help
            //  the ASM implementation, and these errors are frequent if your classpath
            //  is missing something. We still want pmd to continue processing in this case.
            //    The best fix IMO is to admit malformed types provided they're unresolved.
            //  We'll have to abandon the assumption that every parameterized type for
            //  the same symbol has the same number of type params. And also, that the
            //  formal type parameter list always matches the type argument lists in length.
            //    Many places rely on this... For instance: TypeConversion#capture, TypeOps#isSameType, etc
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

}
