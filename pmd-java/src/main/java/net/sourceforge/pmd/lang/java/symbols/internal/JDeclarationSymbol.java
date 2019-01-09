/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal;

import java.util.Optional;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.JLocalVariableSymbolImpl;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;


/**
 * Represents a declaration that can be referred to by simple name. Abstracts over
 * whether the declaration is in the analysed file or not, using reflection when it's not.
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
 * <h1>About global caching</h1>
 *
 * <p>TODO implement sharing of reflectively found code references across the analysed project
 * It would be sufficient to cache JClassSymbols, since from there, all their members would
 * be cached too. {@link JLocalVariableSymbolImpl} doesn't need to be cached since you can't refer to
 * it from another file.
 *
 * <p>That global cache could be used as a basis for multifile analysis, probably whose logic can probably
 * be merged with {@link net.sourceforge.pmd.lang.java.multifile.ProjectMirror}.
 *
 *
 * <p>TODO with global caching, use a (Weak|SoftReference) on the node to avoid memory leaks.
 * Also, {@link Lazy} may cause memory leaks by holding strong references to nodes in the lambdas.
 * This is no problem for now, because without global caching, symbols referring to the
 * same entity are duplicated across analysed classes, and only the symbols created in the
 * class where they're defined hold a node. So the symbols are garbage collected with the
 * AST anyway.
 *
 * A global caching will probably be enough to mitigate the cost of creating symbols,
 * and we can make those potential memory leaks strict.
 *
 * In the current state of affairs (no persistent analysis cache, incremental analysis),
 * a global cache would *heavily* use reflection. So analysis without auxclasspath will be
 * severely limited (like now tbh). There would be no access to classes that are in the
 * analysed project which lack compiled classes.
 *
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
     * when unqualified, eg the simple name of the class, or the name of the method.
     *
     * @return the simple name
     */
    String getSimpleName();

    // TODO annotations could be added to the API if we publish it

    // TODO tests

    // TODO add type information when TypeDefinitions are reviewed
    // We should be able to create a type definition from a java.lang.reflect.Type,
    // paying attention to type variables of enclosing methods and types.
    // We should also be able to do so from an ASTType, with support from a JSymbolTable.
}
