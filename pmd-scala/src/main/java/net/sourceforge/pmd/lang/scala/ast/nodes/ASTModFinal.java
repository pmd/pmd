/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Mod;

public class ASTModFinal extends AbstractScalaNode<Mod.Final> {

    public ASTModFinal(Mod.Final scalaNode) {
        super(scalaNode);
    }
}
