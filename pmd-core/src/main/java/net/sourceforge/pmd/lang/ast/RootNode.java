/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import net.sourceforge.pmd.ViolationSuppressor;

/**
 * This interface can be used to tag the root node of various ASTs.
 */
public interface RootNode extends Node {
    // that's only a marker interface.


    default ViolationSuppressor getSuppressor() {
        return ViolationSuppressor.noop();
    }
}
