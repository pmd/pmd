/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.ast.Node;

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
     * Set the Java Class associated with this node.
     * 
     * @param type
     *            A Java Class
     */
    void setType(Class<?> type);
}
