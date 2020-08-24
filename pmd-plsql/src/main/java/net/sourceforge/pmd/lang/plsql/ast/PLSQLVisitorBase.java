/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import net.sourceforge.pmd.lang.ast.AstVisitorBase;

/**
 * Base implementation of {@link PLSQLVisitor}.
 */
public abstract class PLSQLVisitorBase<P, R> extends AstVisitorBase<P, R> implements PLSQLVisitor<P, R> {

}
