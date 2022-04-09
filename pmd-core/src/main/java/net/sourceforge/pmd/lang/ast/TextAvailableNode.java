/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import net.sourceforge.pmd.lang.rule.xpath.NoAttribute;

/**
 * Refinement of {@link Node} for nodes that can provide the underlying
 * source text.
 *
 * @since 7.0.0
 */
public interface TextAvailableNode extends Node {

    /*
      Note for future: I initially implemented a CharSequence that shares
      the char array for the full file, which seems advantageous, but tbh
      is out of scope of the first prototype

      Problem with using strings is that I suspect it can be very easy to
      create significant memory issues without paying attention...

      See 046958adad for the removal commit
     */


    /**
     * Returns the original source code underlying this node. In
     * particular, for a {@link RootNode}, returns the whole text
     * of the file.
     */
    @NoAttribute
    CharSequence getText();


}
