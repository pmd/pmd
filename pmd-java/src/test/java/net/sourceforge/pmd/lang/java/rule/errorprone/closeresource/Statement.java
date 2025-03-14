/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone.closeresource;


/**
 * This Statement has nothing to do with {@link java.sql.Statement}. So using this,
 * should not trigger the rule CloseResource, since this class is not autoclosable.
 */
public class Statement {

}
