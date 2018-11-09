/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.scopes;

import java.util.Optional;
import java.util.stream.Stream;

import net.sourceforge.pmd.lang.java.symbols.refs.JMethodReference;
import net.sourceforge.pmd.lang.java.symbols.refs.JSymbolicClassReference;
import net.sourceforge.pmd.lang.java.symbols.refs.JVarReference;
import net.sourceforge.pmd.lang.java.symbols.scopes.internal.ImportOnDemandScope;
import net.sourceforge.pmd.lang.java.symbols.scopes.internal.JavaLangScope;
import net.sourceforge.pmd.lang.java.symbols.scopes.internal.SamePackageScope;
import net.sourceforge.pmd.lang.java.symbols.scopes.internal.SingleImportScope;


/**
 * Scope defined by the declaration of a type, a variable, a method.
 * Keeps track of the types, values, and methods accessible from their
 * simple name in the extent of the scope.
 *
 * <p>Scopes are linked to their parent scope, and are only responsible for
 * the declarations occurring in their extent. When a scope can't find a
 * declaration, it delegates to its parent. This encodes shadowing and
 * hiding mechanisms transparently.
 *
 * <p>A class scope contains the field and method declarations of the class, and
 * is linked to an inherited scope that represents methods, types and values that
 * are inherited from its supertypes. It doesn't contain them directly.
 *
 *
 * <p>The following describes the most general form of the scope tree of a compilation unit.
 * Higher in the list means higher in the tree, i.e., lower precedence. Note that {@link ImportOnDemandScope}
 * has
 *
 * <ul>
 * <li> {@link JavaLangScope}
 * <li> {@link ImportOnDemandScope}: never shadow anything, is shadowed by everything (see javadoc for why it's not the root)
 * <li> {@link SamePackageScope}: shadow imports-on-demands, is shadowed by single imports and lower
 * <li> {@link SingleImportScope}: shadows all of the above, is shadowed by type definitions of this compilation unit
 * </ul>*
 *
 *
 *
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public interface JScope {

    /**
     * Returns the parent of this scope, that is, the scope that will be
     * delegated to if this scope doesn't find a declaration.
     *
     * @return a scope, or null if this is the {@linkplain JavaLangScope top level-scope}
     */
    JScope getParent();


    /**
     * Resolves the class referred to by this name. This must be a simple name.
     *
     * @param simpleName Simple name of the type to look for
     *
     * @return The class reference if it can be found, otherwise an empty optional
     */
    Optional<JSymbolicClassReference> resolveTypeName(String simpleName);


    /**
     * Finds all accessible methods that can be called with the given simple name
     * on an implicit receiver in this scope. Several methods may be found, because
     * they can have different arity and parameter types.
     *
     * @param simpleName Name
     *
     * @return An iterator enumerating methods with that name accessible
     * from the implicit receiver in this scope.
     */
    Stream<JMethodReference> resolveMethodName(String simpleName);


    /**
     * Finds the variable or field to which the given simple name refers in this scope.
     *
     * @param simpleName simple
     *
     * @return The reference to the variable if it can be found, otherwise an empty optional
     */
    Optional<JVarReference> resolveValueName(String simpleName);


}
