/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.codearea;

import java.util.Collection;

import org.fxmisc.richtext.StyleSpans;

/**
 * Language-specific engine for syntax highlighting.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public interface SyntaxHighlighter {

    /**
     * Gets the terse name of the language this highlighter cares for. That's used as a css class for text regions.
     *
     * @return The terse name of the language
     */
    String getLanguageTerseName();


    /**
     * Computes the syntax highlighting on the given text.
     *
     * @param text The text
     *
     * @return The bounds of the computed style spans
     */
    StyleSpans<Collection<String>> computeHighlighting(String text);


}
