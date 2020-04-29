/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.antlr4;


import net.sourceforge.pmd.lang.ast.Node;

public interface AntlrTreeVisitor<P, R, N extends Node> {

    R visitAnyNode(N n, P data);

}
