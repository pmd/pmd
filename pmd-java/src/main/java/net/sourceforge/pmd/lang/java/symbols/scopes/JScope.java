/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.scopes;

import java.util.Optional;
import java.util.stream.Stream;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.symbols.refs.JCodeReference;
import net.sourceforge.pmd.lang.java.symbols.refs.JMethodReference;
import net.sourceforge.pmd.lang.java.symbols.refs.JSimpleTypeReference;
import net.sourceforge.pmd.lang.java.symbols.refs.JSymbolicClassReference;
import net.sourceforge.pmd.lang.java.symbols.refs.JTypeVariableReference;
import net.sourceforge.pmd.lang.java.symbols.refs.JVarReference;
import net.sourceforge.pmd.lang.java.symbols.scopes.internal.EmptyScope;
import net.sourceforge.pmd.lang.java.symbols.scopes.internal.ImportOnDemandScope;
import net.sourceforge.pmd.lang.java.symbols.scopes.internal.JavaLangScope;
import net.sourceforge.pmd.lang.java.symbols.scopes.internal.SamePackageScope;
import net.sourceforge.pmd.lang.java.symbols.scopes.internal.SingleImportScope;
import net.sourceforge.pmd.lang.java.typeresolution.ClassTypeResolver;


/**
 * A symbol table for a piece of Java code. Keeps track of the types, values, and
 * methods accessible from their simple name in their extent.
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
 * <p>Each compilation unit defines a set of stacks of scopes, somewhat
 * corresponding to the logical structure of the compilation unit. Each program
 * point in the compilation unit (AST node) has {@linkplain JavaNode#getSymbolTable() a reference}
 * to the innermost enclosing scope, which is the top of its local stack.
 * The following describes the most general form of the bottom part of the
 * stack (before the first type declaration), in increasing order of precedence:
 * <ul>
 *     <li> {@link EmptyScope}: Contains nothing. This is the shared root of all scope stacks, for
 *     implementation simplicity.
 *     <li> {@link ImportOnDemandScope}: Types imported from a package or type by an import-on-demand,
 *     and static method or field names imported from a type by a static-import-on-demand;
 *     <li> {@link JavaLangScope}: Top-level types implicitly imported from {@literal java.lang};
 *     <li> {@link SamePackageScope}: Top-level types from the same package, which are implicitly imported
 *     <li> {@link SingleImportScope}: types imported by single-type-imports, and static methods and
 *     fields imported by a single-static-import.
 * </ul>
 *
 * <h2>Why not keep the current symbol table</h2>
 *
 * <p>The current symbol table framework was not built with the same goals in mind.
 * It indexes the AST to reduce it to a simpler representation, which is mostly
 * shortcuts to nodes. That representation hasn't proved very useful in rules,
 * which mostly only use it to resolve variable accesses.
 *
 * <p>The biggest issue is that it was not designed to abstract over whether we
 * have a node to represent a declaration or not. It can't work on reflection
 * data, and thus cannot really help type resolution, even a good symbol table
 * would take the burden of resolving references off a type checker. The
 * shortcomings of the current symbol table make the current typeres duplicate
 * logic, and ultimately perform tasks that are not its responsibility,
 * which is probably why {@link ClassTypeResolver} is so huge and nasty.
 *
 * <p>Having an abstraction layer to unify them would allow the AST analyses to
 * be complementary, and rely on each other, instead of being so self-reliant.
 * The abstraction provided by {@link JCodeReference} may in the future be used
 * to build global indices of analysed projects to implement multifile analysis.
 *
 * <p>The goals of this rewrite should be:
 * <ul>
 *     <li>To make our language analyses share information and be complementary
 *     <li>To have a symbol table that is precise and exhaustive enough that all
 *     rules can depend on it
 *     <li>To modularize the system so that it's more easily testable and documentable
 * </ul>
 *
 * <h2>TODO</h2>
 *
 * <p>The substack corresponding to a type declaration T will probably have the
 * following form:
 * <ul>
 *     <li> Superinterfaces scope: abstract and default methods inherited from
 *     the direct superinterfaces of T
 *     <li> Superclass scope: methods, fields, and member types inherited from
 *     the direct superclass of T
 *     <li> Member types scopes: Member types of T (their names are shadowed by
 *     the type parameters of T)
 *     <li> Type parameter scope: type parameters of T
 *     <li> Scope of T's body: methods and fields defined on T
 *     <ul>
 *         <li>Each class initializer, lambda, anonymous class, member class,
 *         constructor, or method declaration gets its own scope, which can
 *         contain local classes, anonymous classes, etc, and the cycle continues
 *     </ul>
 * </ul>
 *
 * <p>The tricky point is the scopes for the supertypes of T. Obviously these also can inherit members,
 * and hence we'll have to resolve them recursively based on reflection data.
 *
 * <p>We can probably ruse to be able to share them across all analysed classes, but access control
 * will need to be taken into account. Probably we'll need two steps. Say we're building the scopes
 * of a type T with supertype S:
 * <ul>
 *     <li>1. Resolve the declarations of S and organize them by access restrictions within a object shared
 *     across the analysis
 *     <li>2. Build a "view" of that shared object based on where T is (its package), which filters out
 *     inaccessible declarations from S, and add it to the inherited scopes of T
 * </ul>
 * We'll probably need to proceed depth-first. If you're wondering about the performance costs of exploring
 * a whole type hierarchy, I'd say this is exactly what MissingOverrideRule does for now. It can then be
 * rewritten to make use of this framework.
 *
 * <p>Some other rules could directly use the scope stack, e.g. UnnecessaryQualifiedName, which could
 * simply be looking up whether a qualified name means the same as its simple name in the context of
 * its JScope.
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
     * @return a symbol scope, or null if this is the {@linkplain JavaLangScope top level-scope}
     */
    JScope getParent();

    // note that types and value names can be obscured, but that depends on the context
    // of the usage and is not relevant to the scope stack.

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
     * Finds the variable or field to which the given simple name refers in this scope.
     *
     * @param simpleName simple name of the value to find
     *
     * @return The reference to the variable if it can be found, otherwise an empty optional
     */
    Optional<JVarReference> resolveValueName(String simpleName);


    /**
     * Finds all accessible methods that can be called with the given simple name
     * on an implicit receiver in the scope of this scope. The returned methods may
     * have different arity and parameter types.
     *
     * <p>TODO We can probably encode the overriding and hiding rules for methods in
     * the scope stack. We could have a way to filter out the override-equivalent methods
     * from the stream so that the returned methods are preselected for type resolution to use.
     *
     * @param simpleName Simple name of the method
     *
     * @return An iterator enumerating methods with that name accessible an applicable to the implicit receiver in this scope.
     */
    Stream<JMethodReference> resolveMethodName(String simpleName);

}
