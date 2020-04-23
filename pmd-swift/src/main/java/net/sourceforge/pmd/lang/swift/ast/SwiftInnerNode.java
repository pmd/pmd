/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift.ast;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrBaseInnerNode;

public abstract class SwiftInnerNode extends AntlrBaseInnerNode<SwiftInnerNode, SwiftNode> {

    protected SwiftInnerNode() {
        super();
    }

    protected SwiftInnerNode(final ParserRuleContext parent, final int invokingStateNumber) {
        super(parent, invokingStateNumber);
    }

    @Override
    protected SwiftNode castToItf(ParseTree o) {
        return (SwiftNode) o;
    }

    @Override
    protected SwiftInnerNode castToInnerNode(ParseTree o) {
        return (SwiftInnerNode) o;
    }
}
