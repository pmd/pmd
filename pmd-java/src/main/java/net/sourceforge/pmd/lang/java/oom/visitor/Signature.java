/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.visitor;

import net.sourceforge.pmd.lang.java.ast.AbstractJavaAccessNode;

/**
 * Generic signature. This class is extended by classes specific to operations and fields.
 *
 * @author Clément Fournier
 */
public abstract class Signature {

    public final Visibility visibility;

    Signature(Visibility visibility) {
        this.visibility = visibility;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Signature && visibility == ((Signature) o).visibility;
    }

    @Override
    public int hashCode() {
        return visibility.hashCode() * 2;
    }

    /**
     * The visibility of a node.
     */
    public enum Visibility {
        PUBLIC, PACKAGE, PROTECTED, PRIVATE, UNDEF;


        /**
         * Returns the Visibility enum key for a node.
         *
         * @param node A node
         *
         * @return The visibility enum key for a node.
         */
        public static Visibility get(AbstractJavaAccessNode node) {
            return node.isPublic() ? PUBLIC
                                   : node.isPackagePrivate() ? PACKAGE
                                                             : node.isProtected() ? PROTECTED
                                                                                  : node.isPrivate()
                                                                                    ? PRIVATE
                                                                                    : UNDEF;
        }
    }
}
