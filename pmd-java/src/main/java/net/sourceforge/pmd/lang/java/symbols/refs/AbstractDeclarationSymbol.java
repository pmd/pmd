/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.refs;

import java.util.Optional;

import net.sourceforge.pmd.lang.ast.Node;


/**
 * Base class for {@link JDeclarationSymbol}.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
abstract class AbstractDeclarationSymbol<N extends Node> implements JDeclarationSymbol<N> {

    private final String simpleName;
    private N boundNode;


    AbstractDeclarationSymbol(String simpleName) {
        this.simpleName = simpleName;
    }


    AbstractDeclarationSymbol(N node, String simpleName) {
        this(simpleName);
        this.boundNode = node;
    }


    @Override
    public final Optional<N> getBoundNode() {
        return Optional.ofNullable(boundNode);
    }


    @Override
    public String getSimpleName() {
        return simpleName;
    }


    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + getSimpleName() + ")";
    }
}
