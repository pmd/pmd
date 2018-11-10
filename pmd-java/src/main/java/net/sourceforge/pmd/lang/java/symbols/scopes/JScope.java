/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.scopes;

import java.util.Optional;
import java.util.stream.Stream;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.java.symbols.refs.JCodeReference;
import net.sourceforge.pmd.lang.java.symbols.refs.JMethodReference;
import net.sourceforge.pmd.lang.java.symbols.refs.JSimpleTypeReference;
import net.sourceforge.pmd.lang.java.symbols.refs.JSymbolicClassReference;
import net.sourceforge.pmd.lang.java.symbols.refs.JTypeVariableReference;
import net.sourceforge.pmd.lang.java.symbols.refs.JVarReference;
import net.sourceforge.pmd.lang.java.symbols.scopes.internal.ImportOnDemandScope;
import net.sourceforge.pmd.lang.java.symbols.scopes.internal.JavaLangScope;
import net.sourceforge.pmd.lang.java.symbols.scopes.internal.SamePackageScope;
import net.sourceforge.pmd.lang.java.symbols.scopes.internal.SingleImportScope;


/**
 * A symbol table for a piece of Java code. Keeps track of the types, values, and
 * methods accessible from their simple name in the extent of the scope.
 *
 * <p>Each scope is linked to a parent scope, and keeps track of a particular set
 * of declarations having the same relative precedence. When a scope is asked for
 * the meaning of a name in a particular context (type name, method name, value name),
 * it first determines if it has a declaration with a matching name.
 * <ul>
 *     <li>If there is one, it returns the {@link JCodeReference} representing the entity
 *      the name stands for in the given context
 *     <li>If there is none, it asks the same question to the parent scope recursively
 *      and returns its result.
 * </ul>
 * This allows directly encoding shadowing and hiding mechanisms in the parent-child
 * relationships.
 *
 * <p>Each compilation unit defines a tree of scopes, somewhat corresponding to the
 * logical structure of the compilation unit. The following describes the most general
 * form of the upper part of the scope tree (before the first type declaration), in increasing
 * order of precedence:
 * <ul>
 *     <li> {@link ImportOnDemandScope}: Types imported from a package or type by an import-on-demand,
 *     and static method or field names imported from a type by a static-import-on-demand;
 *     <li> {@link JavaLangScope}: Top-level types implicitly imported from {@literal java.lang};
 *     <li> {@link SamePackageScope}: Top-level types from the same package, which are implicitly imported
 *     <li> {@link SingleImportScope}: types imported by single-type-imports, and static methods and
 *     fields imported by a single-static-import.
 * </ul>
 *
 * <p>Note that the root of the tree is actually {@link JavaLangScope} for all compilation units, because
 * we use a singleton to avoid duplicating it. The above precedence order is nevertheless respected.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
@Experimental
public interface JScope {

    /**
     * Returns the parent of this scope, that is, the scope that will be
     * delegated to if this scope doesn't find a declaration.
     *
     * @return a scope, or null if this is the {@linkplain JavaLangScope top level-scope}
     */
    JScope getParent();


    /**
     * Resolves the type referred to by the given name. This must be a simple name,
     * ie, parameterized types and array types are not available. Primitive types are
     * also not considered because it's probably not useful.
     *
     * <p>The returned type reference may either be a {@link JSymbolicClassReference}
     * or a {@link JTypeVariableReference}.
     *
     * @param simpleName Simple name of the type to look for
     *
     * @return The type reference if it can be found, otherwise an empty optional
     */
    Optional<? extends JSimpleTypeReference<?>> resolveTypeName(String simpleName);


    /**
     * Finds all accessible methods that can be called with the given simple name
     * on an implicit receiver in this scope. The returned methods may have different
     * arity and parameter types.
     *
     * @param simpleName Simple name of the method
     *
     * @return An iterator enumerating methods with that name accessible an applicable to the implicit receiver in this scope.
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
