/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.designerbindings;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.symboltable.ScopedNode;

/**
 * Gathers some services to customise how language implementations bind
 * to the designer.
 *
 * @author Clément Fournier
 * @since 6.20.0
 */
@Experimental
public interface DesignerBindings {

    /**
     * Returns an instance of {@link RelatedNodesSelector}, or
     * null if it should be defaulted to using the old symbol table ({@link ScopedNode}).
     * That default behaviour is implemented in the designer directly.
     */
    RelatedNodesSelector getRelatedNodesSelector();


    /**
     * A base implementation for {@link DesignerBindings}.
     */
    class DefaultDesignerBindings implements DesignerBindings {

        private static final DefaultDesignerBindings INSTANCE = new DefaultDesignerBindings();

        @Override
        public RelatedNodesSelector getRelatedNodesSelector() {
            return null;
        }

        /** Returns the default instance. */
        public static DefaultDesignerBindings getInstance() {
            return INSTANCE;
        }
    }

}
