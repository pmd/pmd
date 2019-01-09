/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table;

import java.util.Optional;
import java.util.stream.Stream;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.java.symbols.internal.JDeclarationSymbol;
import net.sourceforge.pmd.lang.java.symbols.internal.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.internal.JResolvableClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.internal.JSimpleTypeDeclarationSymbol;
import net.sourceforge.pmd.lang.java.symbols.internal.JTypeParameterSymbol;
import net.sourceforge.pmd.lang.java.symbols.internal.JValueSymbol;
import net.sourceforge.pmd.lang.java.typeresolution.ClassTypeResolver;

// @formatter:off
/**
 * A symbol table for a particular region of a Java program. Keeps track of the types,
 * values, and methods accessible from their simple name in their extent.
 *
 * <p>Each symbol table is linked to a parent table, and keeps track of a particular set
 * of declarations having the same relative precedence. When a symbol table is asked for
 * the meaning of a name in a particular syntactic context (type name, method name, value name),
 * it first determines if it tracks a declaration with a matching name.
 * <ul>
 *      <li>If there is one, it returns the {@link JDeclarationSymbol} representing the entity
 *          the name stands for in the given context;
 *      <li>If there is none, it asks the same question to its parent table recursively
 *          and returns that result.
 * </ul>
 * This allows directly encoding shadowing and hiding mechanisms in the parent-child
 * relationships.
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
 * data, and thus cannot really help type resolution, even if a good symbol table
 * would take the burden of resolving references off a type checker. The
 * shortcomings of the current symbol table make the current typeres duplicate
 * logic, and ultimately perform tasks that are not its responsibility,
 * which is probably why {@link ClassTypeResolver} is so huge and nasty.
 *
 * <p>Having an abstraction layer to unify them allows the AST analyses to
 * be complementary, and rely on each other, instead of being so self-reliant.
 * The abstraction provided by {@link JDeclarationSymbol} may in the future be used
 * to build global indices of analysed projects to implement multifile analysis.
 *
 * <p>The goals of this rewrite should be:
 * <ul>
 *      <li>To make our language analyses passes share information and be complementary
 *      <li>To have a symbol table that is precise and exhaustive enough that all rules can depend on it
 *      <li>To modularize the system so that it's more easily testable and documentable
 * </ul>
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
     * @return a symbol table, or null if this is the top-level symbol table
     */
    JSymbolTable getParent();


    /**
     * Resolves the type referred to by the given name. This must be a simple name,
     * ie, parameterized types and array types are not available. Primitive types are
     * also not considered because it's probably not useful.
     *
     * <p>The returned type reference may either be a {@link JResolvableClassSymbol}
     * or a {@link JTypeParameterSymbol}.
     *
     * @param simpleName Simple name of the type to look for
     *
     * @return The type reference if it can be found, otherwise an empty optional
     */
    Optional<? extends JSimpleTypeDeclarationSymbol<?>> resolveTypeName(String simpleName);


    /**
     * Finds the variable or field to which the given simple name refers in the scope of
     * this symbol table.
     *
     * @param simpleName simple name of the value to find
     *
     * @return The reference to the variable if it can be found, otherwise an empty optional
     */
    Optional<JValueSymbol> resolveValueName(String simpleName);


    /**
     * Finds all accessible methods that can be called with the given simple name
     * on an implicit receiver in the scope of this symbol table. The returned methods may
     * have different arity and parameter types.
     *
     * @param simpleName Simple name of the method
     *
     * @return A stream yielding all methods with the given name accessible and applicable to the
     * implicit receiver in the scope of this symbol table.
     */
    Stream<JMethodSymbol> resolveMethodName(String simpleName);

}
