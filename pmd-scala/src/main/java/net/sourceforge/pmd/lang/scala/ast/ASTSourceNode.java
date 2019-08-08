/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.scala.ScalaParser;

import scala.meta.Source;

/**
 * The root node for a Scala AST.
 */
public class ASTSourceNode extends ScalaWrapperNode implements RootNode {

    /**
     * Create a new root node wrapper for the Scala root AST node.
     * 
     * @param scalaParser
     *            the ScalaParser used to generate the node
     * @param scalaNode
     *            the scalaNode node to wrap
     */
    public ASTSourceNode(ScalaParser scalaParser, Source scalaNode) {
        super(scalaParser, scalaNode);
    }
}
