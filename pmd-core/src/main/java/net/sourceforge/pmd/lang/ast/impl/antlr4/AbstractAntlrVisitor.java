/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.antlr4;

import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

public abstract class AbstractAntlrVisitor<T> extends AbstractParseTreeVisitor<T> implements ParseTreeVisitor<T> {

    @Override
    public T visit(ParseTree tree) {
        if (tree instanceof AntlrBaseNode) {
            return visit((AntlrBaseNode) tree);
        }
        return tree.accept(this);
    }

    public T visit(final AntlrBaseNode node) {
        return node.accept(this);
    }
}
