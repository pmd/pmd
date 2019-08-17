/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import java.util.Collections;
import java.util.Map;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.annotation.InternalApi;

/**
 * This interface can be used to tag the root node of various ASTs.
 */
public interface RootNode extends Node {
    // that's only a marker interface.


    @InternalApi
    @Experimental
    default Map<Integer, String> getNoPmdComments() {
        return Collections.emptyMap();
    }

}
