/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import net.sourceforge.pmd.annotation.Experimental;

/**
 *  Represents a language-independent token such as constants, values language reserved keywords, or comments.
 */
public interface GenericToken {

    /**
     * Obtain the next generic token according to the input stream which generated the instance of this token.
     * @return the next generic token if it exists; null if it does not exist
     */
    GenericToken getNext();

    /**
     * Obtain a comment-type token which, according to the input stream which generated the instance of this token,
     * precedes this instance token and succeeds the previous generic token (if there is any).
     * @return the comment-type token if it exists; null if it does not exist
     */
    GenericToken getPreviousComment();

    /**
     * Gets the token's text.
     * @return the token's text
     */
    String getImage();

    /**
     * Gets the line where the token's region begins
     * @return a non-negative integer containing the begin line
     */
    int getBeginLine();

    /**
     * Gets the line where the token's region ends
     * @return a non-negative integer containing the end line
     */
    int getEndLine();

    /**
     * Gets the column offset from the start of the begin line where the token's region begins
     * @return a non-negative integer containing the begin column
     */
    int getBeginColumn();

    /**
     * Gets the column offset from the start of the end line where the token's region ends
     * @return a non-negative integer containing the begin column
     */
    int getEndColumn();

    /**
     * Gets a unique integer representing the kind of token this is.
     * The semantics of this kind depend on the language.
     *
     * <p><strong>Note:</strong> This is an experimental API.
     *
     * <p>The returned constants can be looked up in the language's "*ParserConstants",
     * e.g. CppParserConstants or JavaParserConstants. These constants are considered
     * internal API and may change at any time when the language's grammar is changed.
     */
    @Experimental
    int getKind();
}
