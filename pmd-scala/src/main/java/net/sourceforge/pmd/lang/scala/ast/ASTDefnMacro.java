/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import scala.meta.Defn;

/**
 * The ASTDefnMacro node implementation.
 */
public class ASTDefnMacro extends AbstractScalaNode<Defn.Macro> {

    @Deprecated
    @InternalApi
    public ASTDefnMacro(Defn.Macro scalaNode) {
        super(scalaNode);
    }

    @Override
    public <D, R> R accept(ScalaParserVisitor<D, R> visitor, D data) {
        return visitor.visit(this, data);
    }
}
