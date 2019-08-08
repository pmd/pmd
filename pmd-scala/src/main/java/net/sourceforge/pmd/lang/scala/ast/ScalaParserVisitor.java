/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

/**
 * A Visitor Pattern Interface for the Scala AST.
 */
public interface ScalaParserVisitor {
    /**
     * Visit the Source Node (the root node of the tree).
     * 
     * @param node
     *            the root node of the tree
     * @param data
     *            context-specific data
     * @return context-specific data
     */
    Object visit(ASTSourceNode node, Object data);

    /**
     * Visit an arbitrary Scala Node (any node in the tree).
     * 
     * @param node
     *            the node of the tree
     * @param data
     *            context-specific data
     * @return context-specific data
     */
    Object visit(ScalaNode node, Object data);
}
