/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import net.sourceforge.pmd.lang.ast.GenericToken;

/**
 * Common interface for interacting with parser Token Managers.
 */
public interface TokenManager<T extends GenericToken<T>> {

    T getNextToken();

}
