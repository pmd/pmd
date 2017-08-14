/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.metrics.signature;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTModifierNode;

/**
 * Base class for apex field or method signatures.
 *
 * @author Clément Fournier
 */
public abstract class ApexSignature {

    /** Visibility of the field or method. */
    public final Visibility visibility;


    /** Create a signature using its visibility. */
    protected ApexSignature(Visibility visibility) {
        this.visibility = visibility;
    }


    /** Visibility of a field or method. */
    public enum Visibility {
        PRIVATE, PUBLIC, PROTECTED, GLOBAL;


        /**
         * Finds out the visibility of a method node.
         *
         * @param method The method node
         *
         * @return The visibility of the method
         */
        public static Visibility get(ASTMethod method) {
            ASTModifierNode modifierNode = method.getFirstChildOfType(ASTModifierNode.class);
            if (modifierNode.isPublic()) {
                return PUBLIC;
            } else if (modifierNode.isPrivate()) {
                return PRIVATE;
            } else if (modifierNode.isProtected()) {
                return PROTECTED;
            } else {
                return GLOBAL;
            }
        }
    }

}
