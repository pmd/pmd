/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift.ast;

import org.antlr.v4.runtime.tree.TerminalNode;

import net.sourceforge.pmd.lang.ast.impl.antlr4.AbstractAntlrTerminalNode;

public final class SwiftTerminal extends AbstractAntlrTerminalNode<SwiftNode<?>> implements SwiftNode<TerminalNode> {

    SwiftTerminal(TerminalNode parseTreeNode) {
        super(parseTreeNode);
    }


    @Override
    public String getXPathNodeName() {
        return SwiftTreeParser.DICO.getXPathNameOfToken(getParseTree().getSymbol().getType());
    }

    @Override
    public <P, R> R acceptVisitor(SwiftVisitor<P, R> visitor, P data) {
        return visitor.visitTerminal(this, data);
    }
}
