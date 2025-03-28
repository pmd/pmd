/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift.ast;

import org.antlr.v4.runtime.ParserRuleContext;

import net.sourceforge.pmd.lang.ast.AstVisitor;
import net.sourceforge.pmd.lang.ast.impl.antlr4.BaseAntlrInnerNode;

// package private base class
abstract class SwiftInnerNode
    extends BaseAntlrInnerNode<SwiftNode> implements SwiftNode {

    SwiftInnerNode() {
        super();
    }

    SwiftInnerNode(ParserRuleContext parent, int invokingStateNumber) {
        super(parent, invokingStateNumber);
    }

    @Override
    public <P, R> R acceptVisitor(AstVisitor<? super P, ? extends R> visitor, P data) {
        if (visitor instanceof SwiftVisitor<? super P, ? extends R> swiftVisitor) {
            // some of the generated antlr nodes have no accept method...
            return swiftVisitor.visitSwiftNode(this, data);
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
