/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang;

/**
 * Common interface for interacting with parser Token Managers.
 */
//FUTURE TokenManager implementations need to be moved into Language specific packages
public interface TokenManager {
    Object getNextToken();
    void setFileName(String fileName);
}
