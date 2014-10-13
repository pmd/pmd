/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.symboltable;


/**
 * This is a declaration of a name, e.g. a variable or method name.
 * See {@link AbstractNameDeclaration} for a base class.
 */
public interface NameDeclaration {

    /**
     * Gets the node which manifests the declaration.
     * @return the node
     */
    ScopedNode getNode();

    /**
     * Gets the image of the node. This is usually the name of the declaration
     * such as the variable name.
     * @return the image
     * @see #getName()
     */
    String getImage();

    /**
     * Gets the scope in which this name has been declared.
     * @return the scope
     */
    Scope getScope();

    /**
     * Gets the name of the declaration, such as the variable name.
     * @return
     */
    String getName();
}
