/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.ast.AbstractNode;
import net.sourceforge.pmd.lang.ast.Node;
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


    // insert a child at a given index, shifting other children if need be
    // The implementation of jjtAddChild in AbstractNode overwrites nodes
    // -> probably unexpected and to be changed
    // visible to parser only
    void insertChild(JavaNode child, int index, boolean expandTextSpan) {
        // Allow to insert a child at random index without overwriting
        // If the child is null, it is replaced. If it is not null, children are shifted
        if (children != null && index < children.length && children[index] != null) {
            Node[] newChildren = new Node[children.length + 1];

            // toShift nodes are to the right of the insertion index
            int toShift = children.length - index;

            // copy the nodes before
            System.arraycopy(children, 0, newChildren, 0, index);

            // copy the nodes after
            System.arraycopy(children, index, newChildren, index + 1, toShift);
            children = newChildren;
        }
        super.jjtAddChild(child, index);
        child.jjtSetParent(this);

        // The text coordinates of this node will be enlarged with those of the child 
        if (expandTextSpan) {
            AbstractJavaNode childImpl = (AbstractJavaNode) child;

            if (this.beginLine > childImpl.beginLine) {
                this.beginLine = childImpl.beginLine;
                this.beginColumn = childImpl.beginColumn;
            } else if (this.beginLine == childImpl.beginLine
                    && this.beginColumn > childImpl.beginColumn) {
                this.beginColumn = childImpl.beginColumn;
            }

            if (this.endLine < childImpl.endLine) {
                this.endLine = childImpl.endLine;
                this.endColumn = childImpl.endColumn;
            } else if (this.endLine == childImpl.endLine
                    && this.endColumn < childImpl.endColumn) {
                this.endColumn = childImpl.endColumn;
            }

            // TODO tokens
        }
    }


    @Override
    public final String getXPathNodeName() {
        return JavaParserTreeConstants.jjtNodeName[id];
    }
}
