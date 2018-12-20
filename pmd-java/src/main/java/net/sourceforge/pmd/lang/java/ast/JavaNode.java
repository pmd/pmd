/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;
import net.sourceforge.pmd.lang.symboltable.Scope;
import net.sourceforge.pmd.lang.symboltable.ScopedNode;


/**
 * Root interface for all Nodes of the Java AST.
 */
public interface JavaNode extends ScopedNode {

    /**
     * Calls back the visitor's visit method corresponding to the runtime type of this Node.
     *
     * @param visitor Visitor to dispatch
     * @param data    Visit data
     */
    Object jjtAccept(JavaParserVisitor visitor, Object data);


    /**
     * Dispatches the given visitor to the children of this node. This is the default implementation
     * of {@link JavaParserVisitor#visit(JavaNode, Object)}, to which all other default
     * implementations for visit methods delegate. Unless visit methods are overridden without calling
     * {@code super.visit}, the visitor performs a depth-first tree walk.
     *
     * <p>The return value of the visit methods called on children are ignored.
     *
     * @param visitor Visitor to dispatch
     * @param data    Visit data
     */
    Object childrenAccept(JavaParserVisitor visitor, Object data);


    /**
     * Calls back the visitor's visit method corresponding to the runtime type of this Node.
     *
     * @param visitor Visitor to dispatch
     * @param data    Visit data
     * @param <T>     Type of data
     */
    <T> void jjtAccept(SideEffectingVisitor<T> visitor, T data);


    /**
     * Dispatches the given visitor to the children of this node. This is the default implementation
     * of {@link SideEffectingVisitor#visit(JavaNode, Object)}, to which all other default
     * implementations for visit methods delegate. Unless visit methods are overridden without calling
     * {@code super.visit}, the visitor performs a depth-first tree walk.
     *
     * @param visitor Visitor to dispatch
     * @param data    Visit data
     * @param <T>     Type of data
     */
    <T> void childrenAccept(SideEffectingVisitor<T> visitor, T data);


    /**
     * Gets the symbol table keeping track of the names at the program point
     * this node represents.
     *
     * @return A symbol table
     */
    // the setter is implemented as package private on the abstract node class
    // which is why the SymbolTableResolver is in the AST package
    @Experimental
    JSymbolTable getSymbolTable();


    @Override
    JavaNode jjtGetParent();



    void setScope(Scope scope);


    // These could be moved to the Node interface
    // Ideally they would be implemented as final on AbstractNode to allow inlining
    // We could use an internal PMD rule to migrate usages, which are numerous

    /**
     * Returns true if this node has no children.
     */
    default boolean isLeaf() {
        return jjtGetNumChildren() == 0;
    }


    /**
     * Returns true if this node has children.
     */
    default boolean hasChildren() {
        return !isLeaf();
    }


    /**
     * Returns the last child of this node, or null
     * if it doesn't exist.
     */
    default Node getLastChild() {
        return isLeaf() ? null : jjtGetChild(jjtGetNumChildren() - 1);
    }


    /**
     * Returns the first child of this node, or null
     * if it doesn't exist.
     */
    default Node getFirstChild() {
        return isLeaf() ? null : jjtGetChild(0);
    }

}
