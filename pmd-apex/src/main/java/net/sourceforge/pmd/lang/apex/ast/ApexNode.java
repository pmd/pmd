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
     * Get the underlying Rhino AST node.
     */
    T getNode();
    
    /**
     * Get the JsDoc associated with the given node.  If there is no JsDoc on
     * this node, it may be associated with a parent node, on more representative
     * of the entire expression containing this node.
     * @return The JsDoc comment for the node, may be <code>null</code>.
     */
    String getJsDoc();

    boolean hasSideEffects();
}
