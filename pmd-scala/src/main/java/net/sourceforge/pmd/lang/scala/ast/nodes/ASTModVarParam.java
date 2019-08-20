/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Mod;

public class ASTModVarParam extends AbstractScalaNode<Mod.VarParam> {

    public ASTModVarParam(Mod.VarParam scalaNode) {
        super(scalaNode);
    }
}
