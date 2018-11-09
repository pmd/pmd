/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.scopes;

import java.util.Iterator;
import java.util.Optional;

import net.sourceforge.pmd.lang.java.symbols.refs.JMethodReference;
import net.sourceforge.pmd.lang.java.symbols.refs.JSymbolicClassReference;
import net.sourceforge.pmd.lang.java.symbols.refs.JVarReference;
import net.sourceforge.pmd.lang.java.symbols.scopes.internal.JavaLangScope;


/**
 * Scope defined by the declaration of a type, a variable, a method.
 * Keeps track of the types, values, and methods accessible from their
 * simple name in the extent of the scope.
 *
 * <p>Scopes are linked to their parent scope, and are only responsible for
 * the declarations occurring in their extent. When a scope can't find a
 * declaration, it delegates to its parent. This encodes shadowing and
 * overriding mechanisms transparently.
 *
 * <p>A class scope contains the field and method declarations of the class, and
 * is linked to an inherited scope that represents methods, types and values that
 * are inherited from its supertypes. It doesn't contain them directly.
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
     * Resolves a method name, returning an iterator with the found
     * matching method references.
     *
     * @param simpleName Name
     *
     * @return An iterator enumerating methods with that name accessible
     * from the implicit receiver in this scope.
     */
    Iterator<JMethodReference> resolveMethodName(String simpleName);


    /**
     * Finds the variable or field that corresponds to the given simple name
     * in this scope.
     *
     * @param simpleName simple
     *
     * @return The reference to the variable if it can be found, otherwise an empty optional
     */
    Optional<JVarReference> resolveValueName(String simpleName);


}
