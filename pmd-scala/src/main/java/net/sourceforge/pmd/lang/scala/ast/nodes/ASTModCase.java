/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Mod;

public class ASTModCase extends AbstractScalaNode<Mod.Case> {

    public ASTModCase(Mod.Case scalaNode) {
        super(scalaNode);
    }
}
