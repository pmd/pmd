/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.metrics;

import net.sourceforge.pmd.lang.java.ast.AbstractJavaAccessNode;

/**
 * @author Clément Fournier (clement.fournier@insa-rennes.fr)
 *
 */
public class Signature {

    public final Visibility visibility;

    public static enum Visibility {
        PUBLIC, PACKAGE, PROTECTED, PRIVATE, UNDEF;

        public static final Visibility[] ALL = new Visibility[] { PUBLIC, PACKAGE, PROTECTED, PRIVATE };

    }
    
    public static Visibility getVisibility(AbstractJavaAccessNode node) {
        return node.isPublic() ? Visibility.PUBLIC
                : node.isPackagePrivate() ? Visibility.PACKAGE
                        : node.isProtected() ? Visibility.PROTECTED
                                : node.isPrivate() ? Visibility.PRIVATE : Visibility.UNDEF;
    }

    public Signature(Visibility visibility) {
        this.visibility = visibility;
    }
    
}
