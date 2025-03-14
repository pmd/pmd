/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift.ast;

import org.antlr.v4.runtime.Token;
import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.ast.impl.antlr4.BaseAntlrTerminalNode;

public final class SwiftTerminalNode extends BaseAntlrTerminalNode<SwiftNode> implements SwiftNode {

    SwiftTerminalNode(Token token) {
        super(token);
    }

    @Override
    public @NonNull String getText() {
        String constImage = SwiftParser.DICO.getConstantImageOfToken(getFirstAntlrToken());
        return constImage == null ? getFirstAntlrToken().getText()
                                  : constImage;
    }

    @Override
    public String getXPathNodeName() {
        return SwiftParser.DICO.getXPathNameOfToken(getFirstAntlrToken().getType());
    }

}
