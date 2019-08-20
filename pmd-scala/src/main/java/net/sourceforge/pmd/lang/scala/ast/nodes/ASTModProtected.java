/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Mod;

public class ASTModProtected extends AbstractScalaNode<Mod.Protected> {

    public ASTModProtected(Mod.Protected scalaNode) {
        super(scalaNode);
    }
}
