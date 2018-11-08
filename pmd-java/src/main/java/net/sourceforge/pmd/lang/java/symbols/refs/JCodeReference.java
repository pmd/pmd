/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.refs;

import java.util.Optional;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.symbols.scopes.JScope;


/**
 * Represents a reference to a code element.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public interface JCodeReference<N extends Node> {


    /**
     * Gets the scope representing the thing that was declared.
     * @return
     */
    JScope getDeclaringScope();


    Optional<N> getBoundNode();


    String getSimpleName();


}
