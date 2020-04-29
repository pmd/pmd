/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift.ast;

import net.sourceforge.pmd.lang.ast.impl.antlr4.AbstractAntlrInnerNode;
import net.sourceforge.pmd.lang.ast.impl.antlr4.AbstractAntlrNode;
import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrParseTreeBase;

public class SwiftInnerNode<T extends AntlrParseTreeBase>
    extends AbstractAntlrInnerNode<T, SwiftNode<?>>
    implements SwiftNode<T> {

    SwiftInnerNode(T parseTreeNode) {
        super(parseTreeNode);
    }


    @Override
    protected void addChild(AbstractAntlrNode<?, SwiftNode<?>> child, int index) {
        super.addChild(child, index);
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
