/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift.ast;

import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrNode;
import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrTreeVisitor;


/**
 * Supertype of all swift nodes.
 */
public interface SwiftNode<T> extends AntlrNode<SwiftNode<?>> {

    T getParseTree();


    <P, R> R acceptVisitor(SwiftVisitor<P, R> visitor, P data);


    @Override
    default <P, R> R acceptVisitor(AntlrTreeVisitor<P, R, ?> visitor, P data) {
        if (visitor instanceof SwiftVisitor) {
            return acceptVisitor((SwiftVisitor<P, R>) visitor, data);
        }
        throw new IllegalArgumentException("Cannot accept visitor " + visitor);
    }

}
