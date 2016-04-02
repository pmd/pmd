/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.data.Loc;
import apex.jorje.data.Loc.RealLoc;
import apex.jorje.semantic.ast.AstNode;
import apex.jorje.semantic.exception.UnexpectedCodePathException;
import net.sourceforge.pmd.lang.ast.AbstractNode;
import net.sourceforge.pmd.lang.ast.Node;

public abstract class AbstractApexNode<T extends AstNode> extends AbstractNode implements ApexNode<T> {

    protected final T node;

    public AbstractApexNode(T node) {
        super(node.getClass().hashCode());
        this.node = node;
    }

    /**
     * Accept the visitor. *
     */
    public Object childrenAccept(ApexParserVisitor visitor, Object data) {
        if (children != null) {
            for (int i = 0; i < children.length; ++i) {
                @SuppressWarnings("unchecked")
                // we know that the children here are all ApexNodes
                ApexNode<T> apexNode = (ApexNode<T>) children[i];
                apexNode.jjtAccept(visitor, data);
            }
        }
        return data;
    }

    public T getNode() {
        return node;
    }

    @Override
    public int getBeginLine() {
        if (hasRealLoc()) {
            return ((RealLoc) node.getLoc()).line;
        } else {
            return parent.getBeginLine();
        }
    }

    @Override
    public int getEndLine() {
        // Takes the begin line of the next sibling or the end line of the parent node

        Node nextSibling = getNextSiblingWithRealLoc();
        if (nextSibling != null) {
            return nextSibling.getBeginLine();
        } else {
            return parent.getEndLine();
        }
    }

    @SuppressWarnings("unchecked") // all nodes are subclasses of AbstractApexNode
    private Node getNextSiblingWithRealLoc() {
        AbstractApexNode<? extends AstNode> nextSibling = (AbstractApexNode<? extends AstNode>)getNextSibling();
        while (nextSibling != null && !nextSibling.hasRealLoc()) {
            nextSibling = (AbstractApexNode<? extends AstNode>)nextSibling.getNextSibling();
        }
        return nextSibling;
    }

    private boolean hasRealLoc() {
        try {
            Loc loc = node.getLoc();
            return loc instanceof RealLoc;
        } catch (UnexpectedCodePathException e) {
            return false;
        } catch (IndexOutOfBoundsException e) {
            // bug in apex-jorje? happens on some ReferenceExpression nodes
            return false;
        }
    }

    private Node getNextSibling() {
        int thisChildIndex = 0;
        for (int i = 0; i < parent.jjtGetNumChildren(); i++) {
            if (this == parent.jjtGetChild(i)) {
                thisChildIndex = i;
                break;
            }
        }
        int nextSiblingIndex = thisChildIndex + 1;
        if (parent.jjtGetNumChildren() > nextSiblingIndex) {
            return parent.jjtGetChild(nextSiblingIndex);
        }
        return null;
    }

    @Override
    public int getBeginColumn() {
        if (hasRealLoc()) {
            return ((RealLoc) node.getLoc()).column;
        } else {
            return parent.getBeginColumn();
        }
    }

    @Override
    public int getEndColumn() {
        // take the begin column of the next sibling or the end column of the parent

        Node nextSibling = getNextSiblingWithRealLoc();
        if (nextSibling != null) {
            return nextSibling.getBeginColumn();
        } else {
            return jjtGetParent().getEndColumn();
        }
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName().replaceFirst("^AST", "");
    }
}
