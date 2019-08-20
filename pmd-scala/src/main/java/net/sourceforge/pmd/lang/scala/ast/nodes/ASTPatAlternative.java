/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Pat;

public class ASTPatAlternative extends AbstractScalaNode<Pat.Alternative> {

    public ASTPatAlternative(Pat.Alternative scalaNode) {
        super(scalaNode);
    }
}
