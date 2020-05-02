/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift.ast;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

import net.sourceforge.pmd.lang.ast.impl.GenericNode;


/**
 * Supertype of all swift nodes.
 */
public interface SwiftNode extends GenericNode<SwiftNode> {

    <T> T accept(ParseTreeVisitor<? extends T> visitor);

}
