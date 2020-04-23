/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift.ast;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrBaseNode;

/**
 * Supertype of all swift nodes.
 */
public abstract class SwiftNode extends AntlrBaseNode<SwiftNode> {

    protected SwiftNode() {
        super();
    }

    protected SwiftNode(final ParserRuleContext parent, final int invokingStateNumber) {
        super(parent, invokingStateNumber);
    }

    @Override
    protected SwiftNode cast(ParseTree o) {
        return (SwiftNode) o;
    }
}
