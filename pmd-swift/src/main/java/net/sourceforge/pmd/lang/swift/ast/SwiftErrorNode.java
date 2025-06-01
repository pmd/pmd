/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift.ast;

import net.sourceforge.pmd.lang.ast.impl.antlr4.BaseAntlrErrorNode;
import org.antlr.v4.runtime.Token;

public final class SwiftErrorNode extends BaseAntlrErrorNode<SwiftNode> implements SwiftNode {

    SwiftErrorNode(Token token) {
        super(token);
    }
}
