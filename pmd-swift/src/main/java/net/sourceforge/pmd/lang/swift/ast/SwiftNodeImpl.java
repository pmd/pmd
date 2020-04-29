/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift.ast;

import net.sourceforge.pmd.lang.ast.impl.antlr4.AbstractAntlrNode;
import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrParseTreeBase;

/**
 *
 */
public class SwiftNodeImpl<T extends AntlrParseTreeBase>
    extends AbstractAntlrNode<SwiftNodeImpl<?>, T, SwiftNode>
    implements SwiftNode {

    SwiftNodeImpl(T parseTreeNode) {
        super(parseTreeNode);
    }

    @Override
    protected void addChild(SwiftNodeImpl<?> child, int index) {
        super.addChild(child, index);
    }

    @Override
    public String getXPathNodeName() {
        return SwiftParser.DICO.getXPathNameOfRule(getParseTree());
    }
}
