/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

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
}
