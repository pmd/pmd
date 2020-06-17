/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import scala.meta.Importee;

/**
 * The ASTImporteeUnimport node implementation.
 */
public final class ASTImporteeUnimport extends AbstractScalaNode<Importee.Unimport> {

    ASTImporteeUnimport(Importee.Unimport scalaNode) {
        super(scalaNode);
    }

    @Override
    protected <P, R> R acceptVisitor(ScalaParserVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
