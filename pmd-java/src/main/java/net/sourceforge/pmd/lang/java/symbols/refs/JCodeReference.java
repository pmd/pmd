/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.refs;

import java.util.Optional;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.symbols.scopes.JScope;
import net.sourceforge.pmd.lang.java.symbols.scopes.internal.JSourceFileScope;


/**
 * Represents a declaration. Abstracts over whether the declaration is in
 * the analysed file or not using reflection.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public interface JCodeReference<N extends Node> {


    /**
     * Gets the scope in which this declaration was brought into scope.
     * Eg. for a reference to an imported type, this will be a {@link JSourceFileScope},
     * for a reference to a local variable, this will be the scope in which it was declared.
     *
     * @return the declaration scope
     */
    JScope getDeclaringScope();


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


}
