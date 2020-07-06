/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

// The AstVisitorBase of #2589 (simplified, keep the other instead)
public abstract class AstVisitorBase<P, R> implements AstVisitor<P, R> {

    protected R visitChildren(Node node, P data) {
        for (Node child : node.children()) {
            child.acceptVisitor(this, data);
        }
        return null;
    }

    @Override
    public R visitNode(Node node, P param) {
        return visitChildren(node, param);
    }

}
