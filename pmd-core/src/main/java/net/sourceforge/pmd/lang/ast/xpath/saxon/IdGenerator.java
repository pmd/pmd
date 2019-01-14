/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.xpath.saxon;

import net.sourceforge.pmd.annotation.InternalApi;


/**
 * This class is used to generate unique IDs for nodes.
 */
@Deprecated
@InternalApi
public class IdGenerator {
    private int id;

    public int getNextId() {
        return id++;
    }
}
