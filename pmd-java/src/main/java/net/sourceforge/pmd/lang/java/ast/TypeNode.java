/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.typeresolution.TypeWrapper;

/**
 * This interface allows a Java Class to be associated with a node.
 */
public interface TypeNode extends Node {

    /**
     * Get the Java Class associated with this node.
     *
     * @return The Java Class, may return <code>null</code>.
     */
    Class<?> getType();

    /**
     * Get the TypeWrapper associated with this node. The Class object
     * contained in the TypeWrapper will always be equal to that which
     * is returned by <code>getType()</code>.
     *
     * @return The TypeWrapper, may return <code>null</code>
     */
    TypeWrapper getTypeWrapper();

    /**
     * Set the TypeWrapper associated with this node.
     *
     * @param type A TypeWrapper object
     */
    void setTypeWrapper(TypeWrapper type);

    /**
     * Set the Java Class associated with this node.
     *
     * @param type A Java Class
     */
    void setType(Class<?> type);
}
