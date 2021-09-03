/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import scala.meta.Importee;

/**
 * The ASTImporteeRename node implementation.
 */
public final class ASTImporteeRename extends AbstractScalaNode<Importee.Rename> {

    ASTImporteeRename(Importee.Rename scalaNode) {
        super(scalaNode);
    }

    @Override
    protected <P, R> R acceptVisitor(ScalaParserVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
