/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal;

import java.util.Objects;
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


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AbstractDeclarationSymbol<?> that = (AbstractDeclarationSymbol<?>) o;
        if (!simpleName.equals(that.simpleName)) {
            return false;
        }

        // if either of the nodes is null, node equality is not checked
        // this enforces equivalence between a symbol obtained from reflection or st else
        // and a symbol obtained from an AST, provided they represent the same code entity
        return (boundNode == null || that.boundNode == null)
            || Objects.equals(boundNode, that.boundNode);
    }


    @Override
    public int hashCode() {
        return Objects.hash(simpleName, boundNode);
    }
}
