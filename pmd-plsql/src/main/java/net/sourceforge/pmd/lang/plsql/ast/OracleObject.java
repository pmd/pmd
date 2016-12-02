/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

/**
 * All nodes that represent an Oracle object.
 */
public interface OracleObject extends PLSQLNode {

    /**
     * Gets the name of the Oracle object.
     *
     * @return a String representing the name of the Oracle object.
     */
    String getObjectName();
}
