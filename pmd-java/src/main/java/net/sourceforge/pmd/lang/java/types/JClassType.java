/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;

/**
 * Represents class and interface types, including functional interface
 * types. This interface can be thought of as a {@link JClassSymbol}
 * viewed under a given parameterization. Methods like {@link #streamMethods(Predicate)},
 * {@link #getField(String)} return signatures that are already
 * partially substituted. Eg the method {@code get(int)} for type
 * {@code List<String>} has return type {@code String}, not the
 * type var {@code T} or the erasure {@code Object}.
 *
 * <p>A class type may present its symbol under several views:
 * <ul>
 * <li>If the symbol is not generic, then it may be either
 * <i>{@linkplain #hasErasedSuperTypes() erased}</i> (where all supertypes are erased),
 * or not. Note that a non-erased type may have some erased supertypes,
 * see {@link #getErasure()}.
 * <li>If the symbol is generic, then the type could be in one of the
 * following configurations:
 * <ul>
 * <li><i>{@linkplain #isGenericTypeDeclaration() Generic declaration}</i>:
 * the type arguments are the formal type parameters. All enclosing types are
 * either non-generic or also generic type declarations. Eg {@code interface List<T> { .. } }.
 * <li><i>{@linkplain #isParameterizedType() Parameterized}</i>: the type
 * has type arguments. All enclosing types are either non-generic or
 * also parameterised. Eg {@code List<String>}.
 * <li><i>{@linkplain #isRaw() Raw}</i>: the type doesn't have type arguments,
 * it's considered {@linkplain #hasErasedSuperTypes() erased}, so all its supertypes are
 * also erased. All enclosing types are also erased. Eg {@code List}.
 * </ul>
 */
public interface JClassType extends JTypeMirror {

    // FIXME does the equals method heed all this? test that
    //  also rewrite the doc above


    @Override
    @NonNull
    JClassSymbol getSymbol();


    @Override
    default JClassType subst(Function<? super SubstVar, ? extends @NonNull JTypeMirror> fun) {
        JClassType encl = getEnclosingType();
        if (encl != null) {
            encl = encl.subst(fun);
        }

        List<JTypeMirror> targs = getTypeArgs();
        if (targs.isEmpty() && encl == getEnclosingType()) {
            return this;
        }
        List<JTypeMirror> newArgs = TypeOps.subst(targs, fun);
        if (newArgs == targs && encl == getEnclosingType()) {
            return this;
        }
        return encl != null ? encl.selectInner(getSymbol(), newArgs)
                            : withTypeArguments(newArgs);
    }


    /**
     * Returns true if this type is erased. In that case, all the generic
     * supertypes of this type are erased, type parameters are erased in
     * its field and method types. In particular, if this type declares
     * type parameters, then it is a {@linkplain #isRaw() raw type}.
     */
    boolean hasErasedSuperTypes();


    /**
     * Returns true if this type represents a raw type, ie a type whose
     * declaration is {@linkplain #isGeneric() generic}, but for which
     * no type arguments were provided. In that case the type arguments
     * are an empty list, and all supertypes are erased.
     *
     * <p>Raw types are convertible to any parameterized type of the
     * same family via unchecked conversion.
     */
    @Override
    boolean isRaw();


    /**
     * Returns true if this represents the *declaration* of a generic
     * class or interface and not some parameterization. This is the
     * "canonical" form of a parameterized type.
     *
     * <p>In that case, the {@link #getTypeArgs()} is the same as {@link #getFormalTypeParams()}.
     *
     * <p>The generic type declaration of a generic type may be obtained
     * with {@link #getGenericTypeDeclaration()}.
     */
    boolean isGenericTypeDeclaration();


    /**
     * If this type is generic, returns the type that represents its
     * generic type declaration. Otherwise, if this type is erased,
     * returns the non-erased version of this type.
     */
    JClassType getGenericTypeDeclaration();


    /**
     * Returns true if the symbol of this type declares some type
     * parameters. This is true also for erased types.
     *
     * <p>For example, {@code List}, {@code List<T>}, and {@code List<String>}
     * are generic, but {@code String} is not.
     */
    @Override
    boolean isGeneric();


    /**
     * Returns the type immediately enclosing this type. This may be null
     * if this is a top-level type.
     */
    @Nullable
    JClassType getEnclosingType();


    /**
     * A specific instantiation of the type variables in {@link #getFormalTypeParams()}.
     *
     * <p>If this type is not generic, or a raw type, returns an empty list.
     */
    List<JTypeMirror> getTypeArgs();


    /**
     * Returns the list of type variables declared by the generic type declaration.
     * These match {@link #getTypeArgs()} if this is a {@linkplain #isGenericTypeDeclaration() generic type
     * declaration},
     * which is distinct from a {@linkplain #isRaw() raw type}.
     *
     * <p>If this type is not generic, returns an empty list.
     */
    List<JTypeVar> getFormalTypeParams();


    /**
     * Returns the substitution mapping the formal type parameters of all
     * enclosing types to type arguments. If a type is raw, then its type
     * parameters are not part of the returned mapping. Note, that this
     * does not include type parameters of the supertypes.
     *
     * <p>If this type is erased, returns a substitution erasing all type
     * parameters.
     */
    Substitution getTypeParamSubst();


    /**
     * Select an enclosing type. This can only be called if the given
     * symbol represents a non-static member type of this type declaration.
     *
     * @param symbol Symbol for the inner type
     * @param targs  Type arguments of the inner type. If that is an empty
     *               list, and the given symbol is generic, then the inner
     *               type will be raw. In which case it's an error if this
     *               type is generic and is not raw
     *
     * @return A type for the inner type
     *
     * @throws NullPointerException     If one of the parameter is null
     * @throws IllegalArgumentException If the given symbol is static
     * @throws IllegalArgumentException If the symbol is not a member type
     *                                  of this type (local/anon classes don't work)
     * @throws IllegalArgumentException If the type arguments don't match the
     *                                  type parameters of the symbol (unless they're empty,
     *                                  in which case the selected type is raw)
     * @throws IllegalArgumentException If this type is raw and the inner type is not,
     *                                  or this type is parameterized and the inner type is not
     */
    JClassType selectInner(JClassSymbol symbol, List<JTypeMirror> targs);


    /**
     * Returns the generic superclass type. Returns null if this is
     * {@link TypeSystem#OBJECT Object}. Returns {@link TypeSystem#OBJECT}
     * if this is an interface type.
     */
    @Nullable JClassType getSuperClass();


    @Override
    @Nullable
    default JClassType getAsSuper(JClassSymbol symbol) {
        return (JClassType) JTypeMirror.super.getAsSuper(symbol);
    }


    @Override
    JClassType getErasure();


    /**
     * Returns the list of interface types directly implemented by this
     * type.
     */
    List<JClassType> getSuperInterfaces();


    /**
     * Returns another class type which has the same erasure, but new
     * type arguments.
     *
     * @param args Type arguments of the returned type. If empty, and
     *             this type is generic, returns a raw type.
     *
     * @throws IllegalArgumentException If the type argument list doesn't
     *                                  match the type parameters of this
     *                                  type in length
     */
    JClassType withTypeArguments(List<? extends JTypeMirror> args);


    @Override
    default <T, P> T acceptVisitor(JTypeVisitor<T, P> visitor, P p) {
        return visitor.visitClass(this, p);
    }


}
