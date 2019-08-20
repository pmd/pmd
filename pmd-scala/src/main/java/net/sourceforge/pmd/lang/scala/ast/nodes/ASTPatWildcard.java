/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Pat;

public class ASTPatWildcard extends AbstractScalaNode<Pat.Wildcard> {

    public ASTPatWildcard(Pat.Wildcard scalaNode) {
        super(scalaNode);
    }
}
