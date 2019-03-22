/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.apache.commons.lang3.ArrayUtils;

import net.sourceforge.pmd.lang.ast.AbstractNode;
import net.sourceforge.pmd.lang.symboltable.Scope;


public abstract class AbstractJavaNode extends AbstractNode implements JavaNode {

    protected JavaParser parser;
    private Scope scope;
    private Comment comment;


    public AbstractJavaNode(int id) {
        super(id);
    }


    public AbstractJavaNode(JavaParser parser, int id) {
        super(id);
        this.parser = parser;
    }


    @Override
    public void jjtOpen() {
        if (beginLine == -1 && parser.token.next != null) {
            beginLine = parser.token.next.beginLine;
            beginColumn = parser.token.next.beginColumn;
        }
    }


    @Override
    public void jjtClose() {
        if (beginLine == -1 && children.length == 0) {
            beginColumn = parser.token.beginColumn;
        }
        if (beginLine == -1) {
            beginLine = parser.token.beginLine;
        }
        endLine = parser.token.endLine;
        endColumn = parser.token.endColumn;
    }


    /**
     * Accept the visitor. *
     */
    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    @Override
    public <T> void jjtAccept(SideEffectingVisitor<T> visitor, T data) {
        visitor.visit(this, data);
    }


    /**
     * Accept the visitor. *
     */
    @Override
    public Object childrenAccept(JavaParserVisitor visitor, Object data) {
        for (Node child : children) {
            ((JavaNode) child).jjtAccept(visitor, data);
        }

        return data;
    }


    @Override
    public <T> void childrenAccept(SideEffectingVisitor<T> visitor, T data) {
        for (Node child : children) {
            ((JavaNode) child).jjtAccept(visitor, data);
        }

    }


    @Override
    public Scope getScope() {
        if (scope == null) {
            return ((JavaNode) parent).getScope();
        }
        return scope;
    }


    @Override
    public void setScope(Scope scope) {
        this.scope = scope;
    }


    public void comment(Comment theComment) {
        comment = theComment;
    }


    public Comment comment() {
        return comment;
    }


    /**
     * Inserts some children at the given index, shifting other children without overwriting
     * them. The implementation of jjtAddChild in AbstractNode overwrites nodes, and
     * doesn't shrink or grow the initial array. That's probably unexpected and this should
     * be the standard implementation.
     *
     * The text bounds of this node are enlarged to contain the new children if need be.
     * Text bounds of the child should hence have been set before calling this method.
     * The designer relies on this invariant to perform the overlay (in UniformStyleCollection).
     *
     * @param newChildren Children to add
     * @param index       Index the child should have in the parent
     */
    // visible to parser only
    private void insertChildren(int index, AbstractJavaNode... newChildren) {
        // Allow to insert a child at random index without overwriting
        // If the child is null, it is replaced.
        if (newChildren.length == 0) {
            return;
        } else if (ArrayUtils.contains(newChildren, null)) {
            throw new IllegalArgumentException("Null child?");
        } else if (children == null && index == 0) {
            children = newChildren;
        } else if (children != null && index < children.length) {
            children = ArrayUtils.insert(index, children, newChildren);
        } else {
            throw new ArrayIndexOutOfBoundsException(index);
        }

        updateChildrenIndices(index);

        // The text coordinates of this node will be enlarged with those of the child

        if (index == 0) {
            AbstractJavaNode child = (AbstractJavaNode) children[0];
            if (this.beginLine > child.beginLine) {
                this.firstToken = child.firstToken;
                this.beginLine = child.beginLine;
                this.beginColumn = child.beginColumn;
            } else if (this.beginLine == child.beginLine
                && this.beginColumn > child.beginColumn) {
                this.firstToken = child.firstToken;
                this.beginColumn = child.beginColumn;
            }
        }

        if (index + newChildren.length == children.length) {
            AbstractJavaNode child = (AbstractJavaNode) children[children.length - 1];
            if (this.endLine < child.endLine) {
                this.lastToken = child.lastToken;
                this.endLine = child.endLine;
                this.endColumn = child.endColumn;
            } else if (this.endLine == child.endLine
                && this.endColumn < child.endColumn) {
                this.lastToken = child.lastToken;
                this.endColumn = child.endColumn;
            }
        }
    }

    /** @see #insertChild(AbstractJavaNode, int) */
    // visible to parser only
    void insertChild(AbstractJavaNode child, int index) {
        insertChildren(index, child);
    }

    /**
     * Updates the {@link #jjtGetChildIndex()} of the children with their
     * real position, starting at [startIndex].
     */
    private void updateChildrenIndices(int startIndex) {
        if (startIndex < 0) {
            startIndex = 0;
        }
        for (int j = startIndex; j < jjtGetNumChildren(); j++) {
            children[j].jjtSetChildIndex(j); // shift the children to the right
        }
    }


    /**
     * Shifts the begin and end columns of this node by the given offsets.
     */
    void shiftColumns(int beginShift, int endShift) {
        this.beginColumn += beginShift;
        this.endColumn += endShift;

        // TODO change the tokens. We need to link index
        //  the tokens probably...
    }

    void copyTextCoordinates(AbstractJavaNode copy) {
        this.beginLine = copy.getBeginLine();
        this.beginColumn = copy.getBeginColumn();
        this.endLine = copy.getEndLine();
        this.endColumn = copy.getEndColumn();
        this.firstToken = copy.jjtGetFirstToken();
        this.lastToken = copy.jjtGetLastToken();
    }


    // assumes that the child has the same text bounds
    // as the old one. Used to replace an ambiguous name
    // with an unambiguous representation
    void replaceChildAt(int idx, AbstractJavaNode newChild) {

        AbstractJavaNode oldChild = (AbstractJavaNode) children[idx];

        // parent of the old child must not be reset to null
        // as chances are we're reusing it as a child of the
        // new child

        newChild.copyTextCoordinates(oldChild);
        newChild.jjtSetParent(this);
        newChild.jjtSetChildIndex(idx);

        children[idx] = newChild;
    }


    @Override
    public final String getXPathNodeName() {
        return JavaParserTreeConstants.jjtNodeName[id];
    }
}
