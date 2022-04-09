/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.ast;

import net.sourceforge.pmd.lang.ast.AstVisitorBase;

/**
 * Base implementation of {@link ModelicaVisitor}.
 */
public abstract class ModelicaVisitorBase<P, R> extends AstVisitorBase<P, R> implements ModelicaVisitor<P, R> {

}
