/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.AstNode;

import net.sourceforge.pmd.lang.ast.impl.GenericNode;

public interface EcmascriptNode<T extends AstNode> extends GenericNode<EcmascriptNode<?>> {


    /**
     * Get the underlying Rhino AST node.
     * @deprecated The underlying Rhino node should not be used directly.
     */
    @Deprecated
    T getNode();

    /**
     * Get the JsDoc associated with the given node. If there is no JsDoc on
     * this node, it may be associated with a parent node, on more
     * representative of the entire expression containing this node.
     *
     * @return The JsDoc comment for the node, may be <code>null</code>.
     */
    String getJsDoc();

    boolean hasSideEffects();
}
