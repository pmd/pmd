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
    GenericToken getNextGenericToken();

    /**
     * Obtain a special generic token which, according to the input stream which generated the instance of this token,
     * precedes this instance token and succeeds the previous generic token (if there is any).
     * @return the special token if it exists; null if it does not exist
     */
    GenericToken getPreviousSpecialGenericToken();

    /**
     * Obtain the region where the token occupies in the source file.
     * @return the region
     */
    RegionByLine getRegionByLine();

    /**
     * Gets the string representation of the instance of this token.
     * @return the string representing this token
     */
    String getImage();
}
