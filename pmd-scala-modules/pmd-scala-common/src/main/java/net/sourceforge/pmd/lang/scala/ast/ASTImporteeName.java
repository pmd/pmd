/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import scala.meta.Importee;

/**
 * The ASTImporteeName node implementation.
 */
public class ASTImporteeName extends AbstractScalaNode<Importee.Name> {

    @Deprecated
    @InternalApi
    public ASTImporteeName(Importee.Name scalaNode) {
        super(scalaNode);
    }

    @Override
    public <D, R> R accept(ScalaParserVisitor<D, R> visitor, D data) {
        return visitor.visit(this, data);
    }
}
