/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Defn;

public class ASTDefnDef extends AbstractScalaNode<Defn.Def> {

    public ASTDefnDef(Defn.Def scalaNode) {
        super(scalaNode);
    }
}
