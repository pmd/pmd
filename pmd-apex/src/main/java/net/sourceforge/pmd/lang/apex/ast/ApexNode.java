/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.lang.ast.Node;

import apex.jorje.semantic.ast.AstNode;

public interface ApexNode<T extends AstNode> extends Node {

    /**
     * Accept the visitor. *
     */
    Object jjtAccept(ApexParserVisitor visitor, Object data);

    /**
     * Accept the visitor. *
     */
    Object childrenAccept(ApexParserVisitor visitor, Object data);

    /**
     * Get the underlying AST node.
     */
    T getNode();
}
