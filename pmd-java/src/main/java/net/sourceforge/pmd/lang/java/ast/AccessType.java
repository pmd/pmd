/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Represents the type of access of an {@linkplain ASTAssignableExpr assignable expression}.
 *
 * @author Clément Fournier
 */
public enum AccessType {

    /** The value of the variable is read. */
    READ,

    /** The value is written-to, possibly being read before or after. */
    WRITE
}
