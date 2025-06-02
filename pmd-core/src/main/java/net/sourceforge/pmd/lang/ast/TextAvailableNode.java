/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import net.sourceforge.pmd.lang.document.Chars;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.document.TextRegion;
import net.sourceforge.pmd.lang.rule.xpath.NoAttribute;

/**
 * Refinement of {@link Node} for nodes that can provide the underlying
 * source text.
 *
 * @since 7.0.0
 */
public interface TextAvailableNode extends Node {


    /**
     * Returns the exact region of text delimiting the node in the underlying
     * text document. Note that {@link #getReportLocation()} does not need
     * to match this region. {@link #getReportLocation()} can be scoped down
     * to a specific token, eg the class identifier. This region uses
     * the translated coordinate system, ie the coordinate system of
     * {@link #getTextDocument()}.
     */
    @Override
    TextRegion getTextRegion();

    /**
     * Returns the original source code underlying this node, before
     * any escapes have been translated. In particular, for a {@link RootNode},
     * returns the whole text of the file.
     *
     * @see TextDocument#sliceOriginalText(TextRegion)
     */
    @NoAttribute
    default Chars getOriginalText() {
        return getTextDocument().sliceOriginalText(getTextRegion());
    }

    /**
     * Returns the source code underlying this node, after any escapes
     * have been translated. In particular, for a {@link RootNode}, returns
     * the whole text of the file.
     *
     * @see TextDocument#sliceTranslatedText(TextRegion)
     */
    @NoAttribute
    default Chars getText() {
        return getTextDocument().sliceTranslatedText(getTextRegion());
    }


}
