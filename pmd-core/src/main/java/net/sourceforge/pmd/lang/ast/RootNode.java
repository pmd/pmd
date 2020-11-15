/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import java.util.Collections;
import java.util.Map;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.annotation.InternalApi;

/**
 * This interface identifies the root node of an AST. Each language
 * implementation must ensure that every AST its parser produces has
 * a RootNode as its root, and that there is no other RootNode instance
 * in the tree.
 */
public interface RootNode extends Node {


    /**
     * Returns the map of line numbers to suppression / review comments.
     * Only single line comments are considered, that start with the configured
     * "suppressMarker", which by default is "PMD". The text after the
     * suppressMarker is used as a "review comment" and included in this map.
     *
     * <p>
     * This map is later used to determine, if a violation is being suppressed.
     * It is suppressed, if the line of the violation is contained in this suppress map.
     *
     * @return map of the suppress lines with the corresponding review comments.
     */
    @InternalApi
    @Experimental
    default Map<Integer, String> getNoPmdComments() {
        return Collections.emptyMap();
    }

    @Override
    AstInfo<? extends RootNode> getAstInfo();

}
