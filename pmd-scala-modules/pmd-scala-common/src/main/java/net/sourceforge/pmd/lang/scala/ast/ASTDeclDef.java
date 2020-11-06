/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import scala.meta.Decl;

/**
 * The ASTDeclDef node implementation.
 */
public class ASTDeclDef extends AbstractScalaNode<Decl.Def> {

    @Deprecated
    @InternalApi
    public ASTDeclDef(Decl.Def scalaNode) {
        super(scalaNode);
    }

    @Override
    public <D, R> R accept(ScalaParserVisitor<D, R> visitor, D data) {
        return visitor.visit(this, data);
    }
}
