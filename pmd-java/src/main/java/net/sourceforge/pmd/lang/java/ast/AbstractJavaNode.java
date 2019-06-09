/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.apache.commons.lang3.ArrayUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.ast.AbstractNode;
import net.sourceforge.pmd.lang.ast.GenericToken;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.impl.RichCharSequence;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;
import net.sourceforge.pmd.lang.symboltable.Scope;

abstract class AbstractJavaNode extends AbstractNode implements JavaNode {

    protected JavaParser parser;
    private Scope scope;
    private JSymbolTable symbolTable;
    private Comment comment;
    private ASTCompilationUnit root;
    private RichCharSequence text;

    AbstractJavaNode(int id) {
        super(id);
    }

    AbstractJavaNode(JavaParser parser, int id) {
        super(id);
        this.parser = parser;
    }

    @Override
    public int getBeginLine() {
        return jjtGetFirstToken().getBeginLine();
    }

    @Override
    public int getBeginColumn() {
        return jjtGetFirstToken().getBeginColumn();
    }

    @Override
    public int getEndLine() {
        return jjtGetLastToken().getEndLine();
    }

    @Override
    public int getEndColumn() {
        return jjtGetLastToken().getEndColumn();
    }

    @Override
    public void jjtClose() {
        if (this instanceof LeftRecursiveNode) {
            enlargeLeft();
        }
    }

    @Override
    public JavaNode jjtGetParent() {
        return (JavaNode) super.jjtGetParent();
    }

    @Override
    public JavaNode jjtGetChild(int index) {
        return (JavaNode) super.jjtGetChild(index);
    }


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


    void setSymbolTable(JSymbolTable table) {
        this.symbolTable = table;
    }

    @Override
    public JSymbolTable getSymbolTable() {
        return symbolTable;
    }

    @Override
    public Scope getScope() {
        if (scope == null) {
            return jjtGetParent().getScope();
        }
        return scope;
    }


    @Override
    public RichCharSequence getText() {
        if (text == null) {
            text = getRoot().getText().subSequence(getStartOffset(), getEndOffset());
        }
        return text;
    }

    @Override
    public int getStartOffset() {
        return jjtGetFirstToken().getStartDocumentOffset();
    }

    @Override
    public int getEndOffset() {
        return jjtGetLastToken().getEndDocumentOffset();
    }


    void setScope(Scope scope) {
        this.scope = scope;
    }

    void comment(Comment theComment) {
        comment = theComment;
    }


    @Override
    public Comment comment() {
        return comment;
    }

    @Override
    @NonNull
    public ASTCompilationUnit getRoot() {
        // storing a reference on each node ensures that each path is roamed
        // at most once.
        if (root == null) {
            root = jjtGetParent().getRoot();
        }
        return root;
    }

    /**
     * Replaces the child at index idx with its own children.
     */
    void flatten(int idx) {

        AbstractJavaNode child = (AbstractJavaNode) jjtGetChild(idx);
        children = ArrayUtils.remove(children, idx);
        child.jjtSetParent(null);
        child.jjtSetChildIndex(-1);

        if (child.jjtGetNumChildren() > 0) {
            children = ArrayUtils.insert(idx, children, child.children);
        }
        updateChildrenIndices(idx);
    }

    /**
     * Inserts a child at the given index, shifting other children without overwriting
     * them. The implementation of jjtAddChild in AbstractNode overwrites nodes, and
     * doesn't shrink or grow the initial array. That's probably unexpected and this should
     * be the standard implementation.
     *
     * The text bounds of this node are enlarged to contain the new child if need be.
     * Text bounds of the child should hence have been set before calling this method.
     * The designer relies on this invariant to perform the overlay (in UniformStyleCollection).
     *
     * @param child Child to add
     * @param index Index the child should have in the parent
     */
    // visible to parser only
    void insertChild(AbstractJavaNode child, int index, boolean overwrite) {
        // Allow to insert a child without overwriting
        // If the child is null, it is replaced. If it is not null, children are shifted
        if (index <= children.length) {
            if (overwrite || children[index] == null) {
                children[index] = child;
            } else {
                children = ArrayUtils.insert(index, children, child);
            }
        }
        child.jjtSetChildIndex(index);
        child.jjtSetParent(this);

        updateChildrenIndices(index);

        // The text coordinates of this node will be enlarged with those of the child

        enlargeOnInsert(index, child);
    }

    private void enlargeOnInsert(int index, AbstractJavaNode child) {
        if (index == 0) {
            enlargeLeft(child);
        }
        if (index == children.length - 1) {
            enlargeRight(child);
        }
    }

    private void enlargeLeft() {
        if (jjtGetNumChildren() > 0) {
            enlargeLeft((AbstractJavaNode) jjtGetChild(0));
        }
    }

    private void enlargeLeft(AbstractJavaNode child) {
        GenericToken thisFst = jjtGetFirstToken();
        GenericToken childFst = child.jjtGetFirstToken();

        if (TokenUtils.isBefore(childFst, thisFst)) {
            jjtSetFirstToken(childFst);
        }
    }

    private void enlargeRight(AbstractJavaNode child) {
        GenericToken thisLast = jjtGetLastToken();
        GenericToken childLast = child.jjtGetLastToken();

        if (TokenUtils.isAfter(childLast, thisLast)) {
            jjtSetLastToken(childLast);
        }
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
            children[j].jjtSetParent(this);
        }
    }


    /**
     * Shift the start and end tokens by the given offsets.
     * @throws IllegalStateException if the right shift identifies
     * a token that is left of this node
     */
    void shiftTokens(int leftShift, int rightShift) {
        if (leftShift != 0) {
            jjtSetFirstToken(findTokenSiblingInThisNode(jjtGetFirstToken(), leftShift));
        }
        if (rightShift != 0) {
            jjtSetLastToken(findTokenSiblingInThisNode(jjtGetLastToken(), rightShift));
        }
    }

    private GenericToken findTokenSiblingInThisNode(GenericToken token, int shift) {
        if (shift == 0) {
            return token;
        } else if (shift < 0) {
            // expects a positive shift
            return TokenUtils.nthPrevious(jjtGetFirstToken(), token, -shift);
        } else {
            return TokenUtils.nthFollower(token, shift);
        }
    }


    void copyTextCoordinates(AbstractJavaNode copy) {
        jjtSetFirstToken(copy.jjtGetFirstToken());
        jjtSetLastToken(copy.jjtGetLastToken());
    }


    // assumes that the child has the same text bounds
    // as the old one. Used to replace an ambiguous name
    // with an unambiguous representation
    void replaceChildAt(int idx, JavaNode newChild) {

        // parent of the old child must not be reset to null
        // as chances are we're reusing it as a child of the
        // new child

        newChild.jjtSetParent(this);
        newChild.jjtSetChildIndex(idx);

        children[idx] = newChild;
    }

    @Override
    public String getXPathNodeName() {
        return JavaParserTreeConstants.jjtNodeName[id];
    }
}
