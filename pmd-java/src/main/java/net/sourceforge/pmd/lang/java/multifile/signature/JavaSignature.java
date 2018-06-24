/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.multifile.signature;

import net.sourceforge.pmd.lang.ast.SignedNode;
import net.sourceforge.pmd.lang.java.ast.AccessNode;
import net.sourceforge.pmd.lang.metrics.Signature;

/**
 * Generic signature. This class is extended by classes specific to operations and fields.
 *
 * @author Clément Fournier
 */
public abstract class JavaSignature<N extends SignedNode<N>> implements Signature<N> {

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
