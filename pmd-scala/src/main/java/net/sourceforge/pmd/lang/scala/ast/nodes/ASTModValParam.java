/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Mod;

public class ASTModValParam extends AbstractScalaNode<Mod.ValParam> {

    public ASTModValParam(Mod.ValParam scalaNode) {
        super(scalaNode);
    }
}
