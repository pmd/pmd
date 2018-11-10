/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.refs;

import java.util.Optional;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.symbols.scopes.JSymbolTable;


/**
 * Base class for code references.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
abstract class AbstractCodeReference<N extends Node> implements JCodeReference<N> {

    private final JSymbolTable declaringScope;
    private final String simpleName;
    private N boundNode;


    AbstractCodeReference(JSymbolTable declaringScope, String simpleName) {
        this.declaringScope = declaringScope;
        this.simpleName = simpleName;
    }


    AbstractCodeReference(JSymbolTable declaringScope, N node, String simpleName) {
        this(declaringScope, simpleName);
        this.boundNode = node;
    }


    @Override
    public final JSymbolTable getDeclaringScope() {
        return declaringScope;
    }


    @Override
    public final Optional<N> getBoundNode() {
        return Optional.ofNullable(boundNode);
    }


    @Override
    public String getSimpleName() {
        return simpleName;
    }

}
