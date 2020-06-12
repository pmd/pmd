/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.RootNode;

import scala.meta.Source;

/**
 * The ASTSource node implementation.
 */
public class ASTSource extends AbstractScalaNode<Source> implements RootNode {

    @Deprecated
    @InternalApi
    public ASTSource(Source scalaNode) {
        super(scalaNode);
    }

    @Override
    public <D, R> R accept(ScalaParserVisitor<D, R> visitor, D data) {
        return visitor.visit(this, data);
    }
}
