/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift.ast;

import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrTreeVisitor;

/**
 *
 */
public interface SwiftVisitor<P, R> extends AntlrTreeVisitor<P, R, SwiftNode<?>> {


    default R visitRoot(SwiftRootNode node, P data) {
        return visitAnyNode(node, data);
    }


}
