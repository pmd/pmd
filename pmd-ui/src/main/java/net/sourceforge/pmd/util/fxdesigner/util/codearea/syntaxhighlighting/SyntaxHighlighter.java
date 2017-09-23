/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * Basic syntax highlighter, which colors tokens with CSS style classes.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public interface SyntaxHighlighter {


    /**
     * Gets an ordered map of regex patterns to the CSS class that must be applied. The map must be ordered by
     * priority.
     *
     * @return An ordered map
     */
    Map<String, String> getGroupNameToCssClass();


    /**
     * Gets the pattern used to tokenize the text. Token groups must be named (syntax is {@code (?<GROUP_NAME>..)}).
     * Tokens are mapped to a css class using the {@link #getGroupNameToCssClass()} method.
     *
     * @return The tokenizer pattern
     */
    Pattern getTokenizerPattern();


    /**
     * Returns the terse name of the language this highlighter handles.
     *
     * @return The name of the language
     */
    String getLanguage();


    /**
     * Gets the identifier of the resource file containing appropriate css. This string must be suitable for use within
     * a call to {@code getStyleSheets().add()}.
     *
     * @return The identifier of a css file
     */
    String getCssFileIdentifier();


}
