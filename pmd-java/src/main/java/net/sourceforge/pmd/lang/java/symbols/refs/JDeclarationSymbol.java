/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.refs;

import java.util.Optional;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;


/**
 * Represents a declaration. Abstracts over whether the declaration is in
 * the analysed file or not, using reflection when it's not.
 *
 * <p>This type hierarchy is probably not directly relevant to users writing
 * rules. It's mostly intended to unify the representation of type resolution
 * and symbol analysis. At least for now it's internal.
 *
 * <p>SymbolDeclarations have no reference to the scope they were found in, because
 * that would tie the code reference to the analysed file, preventing the garbage
 * collection of scopes and nodes. This is a major difference with {@link NameDeclaration}.
 * The declaring scope would also vary from file to file. E.g.
 *
 * <pre>
 * class Foo {
 *     public int foo;
 *     // here the declaring scope of Foo#foo would be the class scope of this file
 * }
 *
 * class Bar extends Foo {
 *     // here the declaring scope of Foo#foo would be the inherited scope from Foo
 * }
 * </pre>
 *
 * <p>By storing no reference, we ensure that code references can be shared across the
 * analysed project, allowing reflective resolution to be only done once.
 *
 * <p>TODO implement sharing of reflectively found code references across the analysed project
 * <p>References bound to an AST node cannot be shared though, unless we use a SoftReference somehow.
 *
 * @param <N> Type of AST node that can represent this type of declaration
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
@Experimental
@InternalApi
public interface JDeclarationSymbol<N extends Node> {


    /**
     * Returns the node corresponding to this declaration, if it exists.
     * Some references are references to pieces of source outside of the
     * analysed file and as such, their AST isn't available.
     *
     * @return the AST node representing the declaration, or an empty optional if it doesn't exist
     */
    Optional<N> getBoundNode();


    /**
     * Gets the simple name with which this declaration may be referred to
     * when unqualified, eg the simple name of the class or name of the method.
     *
     * @return the simple name
     */
    String getSimpleName();

    // TODO add type information when TypeDefinitions are reviewed
    // We should be able to create a type definition from a java.lang.reflect.Type,
    // paying attention to type variables of enclosing methods and types.
    // We should also be able to do so from an ASTType, with support from a JScope.
}
