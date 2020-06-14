/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import scala.meta.Decl;

/**
 * The ASTDeclVar node implementation.
 */
public final class ASTDeclVar extends AbstractScalaNode<Decl.Var> {

    ASTDeclVar(Decl.Var scalaNode) {
        super(scalaNode);
    }

    @Override
    protected <D, R> R acceptVisitor(ScalaParserVisitor<D, R> visitor, D data) {
        return visitor.visit(this, data);
    }
}
