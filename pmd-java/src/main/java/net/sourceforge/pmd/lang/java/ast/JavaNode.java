/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.ast.Node;
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


    void setScope(Scope scope);


    default Node getLastChild() {
        return jjtGetChild(jjtGetNumChildren() - 1);
    }


    default Node getFirstChild() {
        return jjtGetChild(0);
    }

}
