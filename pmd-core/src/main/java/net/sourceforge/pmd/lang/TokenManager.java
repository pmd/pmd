/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

/**
 * Common interface for interacting with parser Token Managers.
 */
public interface TokenManager {
    // TODO : Change the return to GenericToken in 7.0.0 - maybe even use generics TokenManager<T extends GenericToken>
    Object getNextToken();


    /**
     * @deprecated For removal in 7.0.0
     */
    @Deprecated
    void setFileName(String fileName);
}
