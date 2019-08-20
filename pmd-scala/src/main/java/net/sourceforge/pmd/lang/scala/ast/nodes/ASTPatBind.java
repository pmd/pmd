/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Pat;

public class ASTPatBind extends AbstractScalaNode<Pat.Bind> {

    public ASTPatBind(Pat.Bind scalaNode) {
        super(scalaNode);
    }
}
