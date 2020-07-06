/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

// The AstVisitor of #2589
public interface AstVisitor<P, R> {

    R visitNode(Node node, P param);

}
