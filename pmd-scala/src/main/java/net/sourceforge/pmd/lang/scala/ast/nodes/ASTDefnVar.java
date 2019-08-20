/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Defn;

public class ASTDefnVar extends AbstractScalaNode<Defn.Var> {

    public ASTDefnVar(Defn.Var scalaNode) {
        super(scalaNode);
    }
}
