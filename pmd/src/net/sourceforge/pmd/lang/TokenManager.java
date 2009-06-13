/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang;

/**
 * Common interface for interacting with parser Token Managers.
 */
public interface TokenManager {
    Object getNextToken();
    void setFileName(String fileName);
}
