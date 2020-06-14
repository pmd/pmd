/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import scala.meta.Importee;

/**
 * The ASTImporteeName node implementation.
 */
public final class ASTImporteeName extends AbstractScalaNode<Importee.Name> {

    ASTImporteeName(Importee.Name scalaNode) {
        super(scalaNode);
    }

    @Override
    protected <D, R> R acceptVisitor(ScalaParserVisitor<D, R> visitor, D data) {
        return visitor.visit(this, data);
    }
}
