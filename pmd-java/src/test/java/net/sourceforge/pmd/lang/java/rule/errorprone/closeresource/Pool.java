/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone.closeresource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public interface Pool {

    Connection getConnection();

    Statement getStmt();

    ResultSet getRS();
}
