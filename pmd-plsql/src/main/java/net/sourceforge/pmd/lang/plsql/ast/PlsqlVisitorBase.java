/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import net.sourceforge.pmd.lang.ast.AstVisitorBase;

/**
 * Base implementation of {@link PlsqlVisitor}.
 */
public abstract class PlsqlVisitorBase<P, R> extends AstVisitorBase<P, R> implements PlsqlVisitor<P, R> {

}
