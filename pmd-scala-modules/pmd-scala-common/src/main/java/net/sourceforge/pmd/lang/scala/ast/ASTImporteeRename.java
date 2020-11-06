/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import scala.meta.Importee;

/**
 * The ASTImporteeRename node implementation.
 */
public class ASTImporteeRename extends AbstractScalaNode<Importee.Rename> {

    @Deprecated
    @InternalApi
    public ASTImporteeRename(Importee.Rename scalaNode) {
        super(scalaNode);
    }

    @Override
    public <D, R> R accept(ScalaParserVisitor<D, R> visitor, D data) {
        return visitor.visit(this, data);
    }
}
