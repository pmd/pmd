/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift.ast;

import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrTreeVisitor;

/**
 *
 */
public interface SwiftVisitor<P, R> extends AntlrTreeVisitor<P, R, SwiftNode<?>> {

    default R visitTerminal(SwiftTerminal node, P data) {
        return visitAnyNode(node, data);
    }


    default R visitInnerNode(SwiftInnerNode<?> node, P data) {
        return visitAnyNode(node, data);
    }


    default R visitRoot(SwRootNode node, P data) {
        return visitInnerNode(node, data);
    }


    default R visitIdent(SwIdentifier node, P data) {
        return visitInnerNode(node, data);
    }


}
