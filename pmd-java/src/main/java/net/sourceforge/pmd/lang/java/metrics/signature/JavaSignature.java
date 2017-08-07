/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.signature;

import net.sourceforge.pmd.lang.java.ast.AccessNode;

/**
 * Generic signature. This class is extended by classes specific to operations and fields.
 *
 * @author Clément Fournier
 */
public abstract class JavaSignature {

    /** Visibility. */
    public final Visibility visibility;


    /**
     * Initialises the visibility.
     *
     * @param visibility The visibility of the signature
     */
    protected JavaSignature(Visibility visibility) {
        this.visibility = visibility;
    }


    /**
     * The visibility of a node.
     */
    public enum Visibility {
        PUBLIC, PACKAGE, PROTECTED, PRIVATE;


        /**
         * Returns the Visibility enum key for a node.
         *
         * @param node A node
         *
         * @return The visibility enum key for a node
         */
        public static Visibility get(AccessNode node) {
            if (node.isPublic()) {
                return PUBLIC;
            } else if (node.isPackagePrivate()) {
                return PACKAGE;
            } else if (node.isProtected()) {
                return PROTECTED;
            } else {
                return PRIVATE;
            }
        }
    }
}
