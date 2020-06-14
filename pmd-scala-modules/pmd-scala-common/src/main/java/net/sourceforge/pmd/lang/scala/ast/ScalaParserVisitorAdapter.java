/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import net.sourceforge.pmd.lang.ast.AstVisitorBase;
import net.sourceforge.pmd.lang.ast.Node;

/**
 * An Adapter for the Scala Parser that implements the Visitor Pattern.
 *
 * @param <D> The type of the data input
 * @param <R> The type of the returned data
 */
public class ScalaParserVisitorAdapter<D, R> extends AstVisitorBase<D, R> implements ScalaParserVisitor<D, R> {

    @Override
    public R visitNode(Node node, D param) {
        return visitChildren(node, param);
    }
}
