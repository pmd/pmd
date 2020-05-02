/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift.ast;

import org.antlr.v4.runtime.ParserRuleContext;

import net.sourceforge.pmd.lang.ast.impl.antlr4.BaseAntlrInnerNode;

public abstract class SwiftInnerNode
    extends BaseAntlrInnerNode<SwiftNode> implements SwiftNode {

    SwiftInnerNode() {
        super();
    }

    SwiftInnerNode(ParserRuleContext parent, int invokingStateNumber) {
        super(parent, invokingStateNumber);
    }

    @Override // override to make visible in package
    protected PmdAsAntlrInnerNode<SwiftNode> asAntlrNode() {
        return super.asAntlrNode();
    }

    @Override
    public String getXPathNodeName() {
        return SwiftParser.DICO.getXPathNameOfRule(getRuleIndex());
    }
}
