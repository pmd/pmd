/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Pat;

public class ASTPatExtractInfix extends AbstractScalaNode<Pat.ExtractInfix> {

    public ASTPatExtractInfix(Pat.ExtractInfix scalaNode) {
        super(scalaNode);
    }
}
