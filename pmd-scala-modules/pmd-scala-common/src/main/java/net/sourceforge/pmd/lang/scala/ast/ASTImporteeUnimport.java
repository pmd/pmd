/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import scala.meta.Importee;

/**
 * The ASTImporteeUnimport node implementation.
 */
public class ASTImporteeUnimport extends AbstractScalaNode<Importee.Unimport> {

    @Deprecated
    @InternalApi
    public ASTImporteeUnimport(Importee.Unimport scalaNode) {
        super(scalaNode);
    }

    @Override
    public <D, R> R accept(ScalaParserVisitor<D, R> visitor, D data) {
        return visitor.visit(this, data);
    }
}
