/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Pat;

public class ASTPatTuple extends AbstractScalaNode<Pat.Tuple> {

    public ASTPatTuple(Pat.Tuple scalaNode) {
        super(scalaNode);
    }
}
