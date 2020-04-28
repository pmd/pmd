/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import net.sourceforge.pmd.lang.rule.xpath.NoAttribute;
import net.sourceforge.pmd.util.document.TextRegion;

/**
 * Refinement of {@link Node} for nodes that can provide the underlying
 * source text.
 *
 * @since 7.0.0
 */
public interface TextAvailableNode extends Node {


    /**
     * Returns the exact region of text delimiting
     * the node in the underlying text document. Note
     * that {@link #getReportLocation()} does not need
     * to match this region.
     */
    TextRegion getTextRegion();

    /**
     * Returns the original source code underlying this node. In
     * particular, for a {@link RootNode}, returns the whole text
     * of the file.
     */
    @NoAttribute
    CharSequence getText();


}
