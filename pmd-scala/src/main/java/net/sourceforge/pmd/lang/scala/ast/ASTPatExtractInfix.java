/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import scala.meta.Pat;

/**
 * The ASTPatExtractInfix node implementation.
 */
public class ASTPatExtractInfix extends AbstractScalaNode<Pat.ExtractInfix> {

    @Deprecated
    @InternalApi
    public ASTPatExtractInfix(Pat.ExtractInfix scalaNode) {
        super(scalaNode);
    }

    @Override
    public <D, R> R accept(ScalaParserVisitor<D, R> visitor, D data) {
        return visitor.visit(this, data);
    }
}
