/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.apache.commons.lang3.ArrayUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.impl.javacc.AbstractJjtreeNode;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;
import net.sourceforge.pmd.lang.symboltable.Scope;

abstract class AbstractJavaNode extends AbstractJjtreeNode<AbstractJavaNode, JavaNode> implements JavaNode {

    private Scope scope;
    protected JSymbolTable symbolTable;
    private Comment comment;
    private ASTCompilationUnit root;

    AbstractJavaNode(int id) {
        super(id);
    }


    @Override
    public void jjtClose() {
        super.jjtClose();
        if (this instanceof LeftRecursiveNode) {
            enlargeLeft();
        }
    }

    @Override // override to make it accessible to tests that build nodes (which have been removed on java-grammar)
    protected void addChild(AbstractJavaNode child, int index) {
        super.addChild(child, index);
    }

    void setSymbolTable(JSymbolTable table) {
        this.symbolTable = table;
    }

    @Override
    @NonNull
    public JSymbolTable getSymbolTable() {
        if (symbolTable == null) {
            return getParent().getSymbolTable();
        }
        return symbolTable;
    }

    @Override
    public Scope getScope() {
        if (scope == null) {
            return getParent().getScope();
        }
        return scope;
    }

    @Override // override to make it accessible to parser
    protected void setImage(String image) {
        super.setImage(image);
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
            root = getParent().getRoot();
        }
        return root;
    }

    /**
     * Replaces the child at index idx with its own children.
     */
    void flatten(int idx) {

        AbstractJavaNode child = (AbstractJavaNode) getChild(idx);
        children = ArrayUtils.remove(children, idx);
        child.setParent(null);
        child.setChildIndex(-1);

        if (child.getNumChildren() > 0) {
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
        child.setChildIndex(index);
        child.setParent(this);

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
        if (getNumChildren() > 0) {
            enlargeLeft((AbstractJavaNode) getChild(0));
        }
    }

    private void enlargeLeft(AbstractJavaNode child) {
        JavaccToken thisFst = this.getFirstToken();
        JavaccToken childFst = child.getFirstToken();

        if (TokenUtils.isBefore(childFst, thisFst)) {
            this.setFirstToken(childFst);
        }
    }

    private void enlargeRight(AbstractJavaNode child) {
        JavaccToken thisLast = this.getLastToken();
        JavaccToken childLast = child.getLastToken();

        if (TokenUtils.isAfter(childLast, thisLast)) {
            this.setLastToken(childLast);
        }
    }

    /**
     * Updates the {@link #getIndexInParent()} of the children with their
     * real position, starting at [startIndex].
     */
    private void updateChildrenIndices(int startIndex) {
        if (startIndex < 0) {
            startIndex = 0;
        }
        for (int j = startIndex; j < getNumChildren(); j++) {
            children[j].setChildIndex(j); // shift the children to the right
            children[j].setParent(this);
        }
    }


    /**
     * Shift the start and end tokens by the given offsets.
     * @throws IllegalStateException if the right shift identifies
     * a token that is left of this node
     */
    void shiftTokens(int leftShift, int rightShift) {
        if (leftShift != 0) {
            setFirstToken(findTokenSiblingInThisNode(getFirstToken(), leftShift));
        }
        if (rightShift != 0) {
            setLastToken(findTokenSiblingInThisNode(getLastToken(), rightShift));
        }
    }

    private JavaccToken findTokenSiblingInThisNode(JavaccToken token, int shift) {
        if (shift == 0) {
            return token;
        } else if (shift < 0) {
            // expects a positive shift
            return TokenUtils.nthPrevious(getFirstToken(), token, -shift);
        } else {
            return TokenUtils.nthFollower(token, shift);
        }
    }


    void copyTextCoordinates(AbstractJavaNode copy) {
        setFirstToken(copy.getFirstToken());
        setLastToken(copy.getLastToken());
    }


    // assumes that the child has the same text bounds
    // as the old one. Used to replace an ambiguous name
    // with an unambiguous representation
    void replaceChildAt(int idx, AbstractJavaNode newChild) {

        // parent of the old child must not be reset to null
        // as chances are we're reusing it as a child of the
        // new child

        newChild.setParent(this);
        newChild.setChildIndex(idx);

        children[idx] = newChild;
    }

    @Override
    public final String getXPathNodeName() {
        return JavaParserImplTreeConstants.jjtNodeName[id];
    }
}
