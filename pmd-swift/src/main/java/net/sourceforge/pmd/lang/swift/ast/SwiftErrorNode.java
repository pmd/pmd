/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift.ast;

import org.antlr.v4.runtime.Token;

import net.sourceforge.pmd.lang.ast.impl.antlr4.BaseAntlrErrorNode;

public final class SwiftErrorNode extends BaseAntlrErrorNode<SwiftNode> implements SwiftNode {

    SwiftErrorNode(Token token) {
        super(token);
    }

}
