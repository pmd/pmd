/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.symboltable;


/**
 * A {@link NameOccurrence} represents one usage of a name declaration.
 *
 */
public interface NameOccurrence {
    /**
     * Gets the location where the usage occurred.
     * @return the node
     */
    ScopedNode getLocation();

    /**
     * Gets the image of the used declaration, such as the variable name.
     * @return the image
     */
    String getImage();
}
