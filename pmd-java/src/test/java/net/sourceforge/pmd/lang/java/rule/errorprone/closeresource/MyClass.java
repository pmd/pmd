/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone.closeresource;

import java.sql.PreparedStatement;

public class MyClass {

    public void cleanup() { }
    
    public void applyTransactionTimeout(PreparedStatement stmt) { }
}
