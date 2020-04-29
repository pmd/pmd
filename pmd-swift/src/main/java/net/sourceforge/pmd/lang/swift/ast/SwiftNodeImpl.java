/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift.ast;

import net.sourceforge.pmd.lang.ast.impl.antlr4.AbstractAntlrNode;
import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrParseTreeBase;
import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrTreeVisitor;

/**
 *
 */
public class SwiftNodeImpl<T extends AntlrParseTreeBase>
    extends AbstractAntlrNode<SwiftNodeImpl<?>, T, SwiftNode<?>>
    implements SwiftNode<T> {

    SwiftNodeImpl(T parseTreeNode) {
        super(parseTreeNode);
    }

    @Override
    protected void addChild(SwiftNodeImpl<?> child, int index) {
        super.addChild(child, index);
    }

    @Override
    public final <P, R> R acceptVisitor(AntlrTreeVisitor<P, R, ?> visitor, P data) {
        if (visitor instanceof SwiftVisitor) {
            return acceptVisitor((SwiftVisitor<P, R>) visitor, data);
        }
        throw new IllegalArgumentException("Cannot accept visitor " + visitor);
    }

    @Override
    public <P, R> R acceptVisitor(SwiftVisitor<P, R> visitor, P data) {
        return visitor.visitAnyNode(this, data);
    }

    @Override
    public String getXPathNodeName() {
        return SwiftTreeParser.DICO.getXPathNameOfRule(getParseTree());
    }
}
