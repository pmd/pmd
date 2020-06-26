/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift.ast;

import org.antlr.v4.runtime.ParserRuleContext;

import net.sourceforge.pmd.lang.ast.AstVisitor;
import net.sourceforge.pmd.lang.ast.impl.antlr4.BaseAntlrInnerNode;

public abstract class SwiftInnerNode
    extends BaseAntlrInnerNode<SwiftNode> implements SwiftNode {

    SwiftInnerNode() {
        super();
    }

    SwiftInnerNode(ParserRuleContext parent, int invokingStateNumber) {
        super(parent, invokingStateNumber);
    }

    @Override
    public <P, R> R acceptVisitor(AstVisitor<? super P, ? extends R> visitor, P data) {
        if (visitor instanceof SwiftVisitor) {
            // some of the generated antlr nodes have no accept method...
            return ((SwiftVisitor<? super P, ? extends R>) visitor).visitSwiftNode(this, data);
        }
        return visitor.visitNode(this, data);
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
