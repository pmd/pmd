/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Pat;

public class ASTPatExtract extends AbstractScalaNode<Pat.Extract> {

    public ASTPatExtract(Pat.Extract scalaNode) {
        super(scalaNode);
    }
}
