/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift.ast;

import net.sourceforge.pmd.lang.ast.impl.GenericNode;
import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrParseTreeBase;


/**
 * Supertype of all swift nodes.
 */
public interface SwiftNode<T extends AntlrParseTreeBase> extends GenericNode<SwiftNode<?>> {


    T getParseTree();


    <P, R> R acceptVisitor(SwiftVisitor<P, R> visitor, P data);

}
