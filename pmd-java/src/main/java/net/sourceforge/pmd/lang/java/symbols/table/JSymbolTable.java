/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table;

import java.util.Optional;
import java.util.stream.Stream;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.SymbolTableResolver;
import net.sourceforge.pmd.lang.java.symbols.refs.JCodeReference;
import net.sourceforge.pmd.lang.java.symbols.refs.JMethodReference;
import net.sourceforge.pmd.lang.java.symbols.refs.JSimpleTypeReference;
import net.sourceforge.pmd.lang.java.symbols.refs.JSymbolicClassReference;
import net.sourceforge.pmd.lang.java.symbols.refs.JTypeVariableReference;
import net.sourceforge.pmd.lang.java.symbols.refs.JVarReference;
import net.sourceforge.pmd.lang.java.symbols.table.internal.EmptySymbolTable;
import net.sourceforge.pmd.lang.java.symbols.table.internal.ImportOnDemandSymbolTable;
import net.sourceforge.pmd.lang.java.symbols.table.internal.JavaLangSymbolTable;
import net.sourceforge.pmd.lang.java.symbols.table.internal.SamePackageSymbolTable;
import net.sourceforge.pmd.lang.java.symbols.table.internal.SingleImportSymbolTable;
import net.sourceforge.pmd.lang.java.typeresolution.ClassTypeResolver;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.Scope;

// @formatter:off
/**
 * A symbol table for a piece of Java code. Keeps track of the types, values, and
 * methods accessible from their simple name in their extent.
 *
 * <p>Each symbol table is linked to a parent table, and keeps track of a particular set
 * of declarations having the same relative precedence. When a symbol table is asked for
 * the meaning of a name in a particular syntactic context (type name, method name, value name),
 * it first determines if it has a declaration with a matching name.
 * <ul>
 *      <li>If there is one, it returns the {@link JCodeReference} representing the entity
 *          the name stands for in the given context
 *      <li>If there is none, it asks the same question to its parent table recursively
 *          and returns its result.
 * </ul>
 * This allows directly encoding shadowing and hiding mechanisms in the parent-child
 * relationships.
 *
 * <p>Each symbol table is only relevant to a set of program points, which it is said
 * to <i>dominate</i>. The set of program points a table dominates is referred-to
 * as the <i>scope</i> of that symbol table. The scope of a symbol table is a subset of
 * the scope of its parent. The declarations in a symbol table S are said to be
 * <i>in-scope</i> throughout the scope of S, which means they are accessible from their
 * simple name. (Although this terminology is based on JLS terminology, the JLS doesn't
 * care about symbol table implementations so that is not standard spec).
 *
 * <p>In PMD, program points are modelled as AST nodes. Each node has {@linkplain JavaNode#getSymbolTable() a reference}
 * to the innermost enclosing symbol table which dominates it. Since each symbol table
 * has a reference to its parent, an AST node has in fact a reference to a whole <i>table stack</i>.
 * These references are resolved by a {@link SymbolTableResolver} after parsing the file.
 *
 * <p>The following describes the most general form of the bottom part of any stack
 * (before the top-level type declaration), in increasing order of precedence:
 * <ul>
 *      <li>{@link EmptySymbolTable}: Contains nothing. This is the shared root of all symbol table stacks, for
 *          implementation simplicity.
 *      <li>{@link ImportOnDemandSymbolTable}: Types imported from a package or type by an import-on-demand,
 *          and static method or field names imported from a type by a static-import-on-demand;
 *      <li>{@link JavaLangSymbolTable}: Top-level types implicitly imported from {@literal java.lang};
 *      <li>{@link SamePackageSymbolTable}: Top-level types from the same package, which are implicitly imported
 *      <li>{@link SingleImportSymbolTable}: types imported by single-type-imports, and static methods and
 *          fields imported by a single-static-import.
 * </ul>
 * These dominate the whole compilation unit and thus are all linked to the {@link ASTCompilationUnit}.
 *
 * <h2>Main differences with the current symbol table framework</h2>
 *
 * <ul>
 *     <li>Symbol tables resolve names
 *     <li>Symbol tables don't index the AST like {@link Scope}s do.
 *     <li>Symbol tables don't store usages, a separate component could be used for that
 *     <li>{@link JCodeReference}s don't store a reference to their declaring SymbolTable
 *         like {@link NameDeclaration} does with {@link Scope}.
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
 *      <li>To make our language analyses passes share information and be complementary
 *      <li>To have a symbol table that is precise and exhaustive enough that all rules can depend on it
 *      <li>To modularize the system so that it's more easily testable and documentable
 * </ul>
 *
 *
 * <h2>TODO</h2>
 *
 * <p>The substack corresponding to a type declaration T will probably have the
 * following form:
 * <ul>
 *      <li> Superinterfaces: abstract and default methods inherited from
 *           the direct superinterfaces of T
 *      <li> Superclass: methods, fields, and member types inherited from
 *           the direct superclass of T
 *      <li> Member types: Member types of T (their names are shadowed by
 *           the type parameters of T)
 *      <li> Type parameters of T
 *      <li> T's static members: static methods, fields and types defined by T.
 *      <li> T's non-static members: static methods, fields and types defined by T.
 *      <li> Children symbol tables of T's body:
 *          <ul>
 *              <li>Each class initializer, lambda, anonymous class, member class,
 *                  constructor, or method declaration gets its own symbol table, which can
 *                  contain local classes, anonymous classes, etc, and the cycle continues
 *              <li>Static contexts (initializers, static methods, static nested classes)
 *                  have as parent the static members symbol table of T, because they can't access
 *                  T's non-static members. Non-static contexts are children to the non-static table,
 *                  which is itself child of the static table, so they can use all declarations.
 *          </ul>
 * </ul>
 *
 * <p>The trickiest point is the symbol tables for the supertypes of T. Obviously these also can inherit
 * members, and hence we'll have to resolve them recursively based on reflection data.
 *
 * <p>We can probably ruse to be able to share them across all analysed classes, but access control
 * will need to be taken into account. Probably we'll need two steps. Say we're building the symbol table
 * of a type T with supertype S:
 * <ul>
 *      <li>1. Resolve the declarations of S and organize them by access restrictions within a object shared
 *          across the analysis
 *      <li>2. Build a "view" of that shared object based on where T is (its package), which filters out
 *          inaccessible declarations from S, and add it to the inherited tables of T
 * </ul>
 * We'll probably need to proceed depth-first. If you're wondering about the performance costs of exploring
 * a whole type hierarchy, I'd say this is exactly what MissingOverrideRule does for now. It can then be
 * rewritten to make use of this framework.
 *
 * <p>Some other rules could directly use the symbol table stack, e.g. UnnecessaryQualifiedName, which could
 * simply be looking up whether a qualified name means the same as its simple name in the scope of
 * its JSymbolTable.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
// @formatter:on
@Experimental
public interface JSymbolTable {

    /**
     * Returns the parent of this table, that is, the symbol table that will be
     * delegated to if this table doesn't find a declaration.
     *
     * @return a symbol table, or null if this is the {@linkplain EmptySymbolTable top-level symbol table}
     */
    JSymbolTable getParent();

    // note that types and value names can be obscured, but that depends on the context
    // of the usage and is not relevant to the symbol table stack.


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
     * Finds the variable or field to which the given simple name refers in the scope of
     * this symbol table. TODO exclude non-static fields if we're in a static context, eg in a static nested class or in a static method.
     *
     * @param simpleName simple name of the value to find
     *
     * @return The reference to the variable if it can be found, otherwise an empty optional
     */
    Optional<JVarReference> resolveValueName(String simpleName);


    /**
     * Finds all accessible methods that can be called with the given simple name
     * on an implicit receiver in the scope of this symbol table. The returned methods may
     * have different arity and parameter types.
     *
     * <p>TODO We can probably encode the overriding and hiding rules for methods in
     * the symbol table stack. We could have a way to filter out the override-equivalent methods
     * from the stream so that the returned methods are preselected for type resolution to use.
     *
     * @param simpleName Simple name of the method
     *
     * @return An iterator enumerating methods with that name accessible an applicable to the implicit receiver in the scope of this symbol table.
     */
    Stream<JMethodReference> resolveMethodName(String simpleName);

}
