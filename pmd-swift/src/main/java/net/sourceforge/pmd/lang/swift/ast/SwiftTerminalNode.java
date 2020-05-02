/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift.ast;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

import net.sourceforge.pmd.lang.ast.impl.antlr4.BaseAntlrTerminalNode;

public final class SwiftTerminalNode extends BaseAntlrTerminalNode<SwiftNode> implements SwiftNode {

    SwiftTerminalNode(Token token) {
        super(token);
    }

    @Override
    public String getXPathNodeName() {
        return SwiftParser.DICO.getXPathNameOfToken(getFirstAntlrToken().getType());
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
        return visitor.visitTerminal(asAntlrNode());
    }
}
